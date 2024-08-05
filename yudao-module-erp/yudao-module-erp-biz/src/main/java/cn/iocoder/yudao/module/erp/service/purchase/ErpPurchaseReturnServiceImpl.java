package cn.iocoder.yudao.module.erp.service.purchase;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.returns.ErpPurchaseReturnPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.returns.ErpPurchaseReturnSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.*;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.*;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.finance.ErpAccountService;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static com.fhs.common.constant.Constant.ZREO;

// TODO 芋艿：记录操作日志

/**
 * ERP 采购退货 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpPurchaseReturnServiceImpl implements ErpPurchaseReturnService {

    @Resource
    private ErpPurchaseReturnMapper purchaseReturnMapper;
    @Resource
    private ErpPurchaseReturnItemMapper purchaseReturnItemMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;
    @Resource
    private ErpPurchaseInService purchaseInService;
    @Resource
    private ErpPurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpPurchaseOrderService purchaseOrderService;
    @Resource
    private ErpPurchaseOrderItemMapper purchaseOrderItemMapper;
    @Resource
    private ErpAccountService accountService;
    @Resource
    private ErpStockRecordService stockRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchaseReturn(ErpPurchaseReturnSaveReqVO createReqVO) {
        // 1.1 校验采购订单已审核
        ErpPurchaseOrderDO purchaseOrder = purchaseOrderService.validatePurchaseOrder(createReqVO.getOrderId());
        // 1.2 校验退货项的有效性
        List<ErpPurchaseReturnItemDO> purchaseReturnItems = validatePurchaseReturnItems(createReqVO.getItems());
//        // 1.3 校验结算账户
//        accountService.validateAccount(createReqVO.getAccountId());
        // 1.4 生成退货单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.PURCHASE_RETURN_NO_PREFIX);
        if (purchaseReturnMapper.selectByNo(no) != null) {
            throw exception(PURCHASE_RETURN_NO_EXISTS);
        }

        // 2.1 插入退货
        ErpPurchaseReturnDO purchaseReturn = BeanUtils.toBean(createReqVO, ErpPurchaseReturnDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus()))
                .setOrderNo(purchaseOrder.getNo()).setSupplierId(purchaseOrder.getSupplierId());
        calculateTotalPrice(purchaseReturn, purchaseReturnItems);
        purchaseReturnMapper.insert(purchaseReturn);
        // 2.2 插入退货项
        purchaseReturnItems.forEach(o -> o.setReturnId(purchaseReturn.getId()));
        purchaseReturnItemMapper.insertBatch(purchaseReturnItems);

        return purchaseReturn.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseReturn(ErpPurchaseReturnSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpPurchaseReturnDO purchaseReturn = validatePurchaseReturnExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseReturn.getStatus())) {
            throw exception(PURCHASE_RETURN_UPDATE_FAIL_APPROVE, purchaseReturn.getNo());
        }
        // 1.2 校验采购订单已审核
        ErpPurchaseOrderDO purchaseOrder = purchaseOrderService.validatePurchaseOrder(updateReqVO.getOrderId());
        // 1.3 校验结算账户
//        accountService.validateAccount(updateReqVO.getAccountId());
        // 1.4 校验订单项的有效性
        List<ErpPurchaseReturnItemDO> purchaseReturnItems = validatePurchaseReturnItems(updateReqVO.getItems());

        // 2.1 更新退货
        ErpPurchaseReturnDO updateObj = BeanUtils.toBean(updateReqVO, ErpPurchaseReturnDO.class)
                .setOrderNo(purchaseOrder.getNo()).setSupplierId(purchaseOrder.getSupplierId());
        calculateTotalPrice(updateObj, purchaseReturnItems);
        purchaseReturnMapper.updateById(updateObj);
        // 2.2 更新退货项
        updatePurchaseReturnItemList(updateReqVO.getId(), purchaseReturnItems);

//        // 3.1 更新采购订单的出库数量
//        updatePurchaseOrderReturnCount(updateObj.getOrderId());

        // 3.2 注意：如果采购订单编号变更了，需要更新“老”采购订单的出库数量
//        if (ObjectUtil.notEqual(purchaseReturn.getOrderId(), updateObj.getOrderId())) {
//            updatePurchaseOrderReturnCount(purchaseReturn.getOrderId());
//        }
    }

    private void calculateTotalPrice(ErpPurchaseReturnDO purchaseReturn, List<ErpPurchaseReturnItemDO> purchaseReturnItems) {
        purchaseReturn.setTotalCount(getSumValue(purchaseReturnItems, ErpPurchaseReturnItemDO::getCount, BigDecimal::add));
        purchaseReturn.setTotalProductPrice(getSumValue(purchaseReturnItems, ErpPurchaseReturnItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
        purchaseReturn.setTotalTaxPrice(getSumValue(purchaseReturnItems, ErpPurchaseReturnItemDO::getTaxPrice, BigDecimal::add, BigDecimal.ZERO));
        purchaseReturn.setTotalPrice(purchaseReturn.getTotalProductPrice().add(purchaseReturn.getTotalTaxPrice()));
        // 计算优惠价格
        if (purchaseReturn.getDiscountPercent() == null) {
            purchaseReturn.setDiscountPercent(BigDecimal.ZERO);
        }
        purchaseReturn.setDiscountPrice(MoneyUtils.priceMultiplyPercent(purchaseReturn.getTotalPrice(), purchaseReturn.getDiscountPercent()));
        purchaseReturn.setTotalPrice(purchaseReturn.getTotalPrice().subtract(purchaseReturn.getDiscountPrice()).add(purchaseReturn.getOtherPrice()));
    }

    private void updatePurchaseOrderReturnCount(Long orderId) {
        // 1.1 查询采购订单对应的采购出库单列表
        List<ErpPurchaseReturnDO> purchaseReturns = purchaseReturnMapper.selectListByOrderId(orderId);
        // 1.2 查询对应的采购订单项的退货数量
        Map<Long, BigDecimal> returnCountMap = purchaseReturnItemMapper.selectOrderItemCountSumMapByReturnIds(
                convertList(purchaseReturns, ErpPurchaseReturnDO::getId));
        // 2. 更新采购订单的出库数量
        purchaseOrderService.updatePurchaseOrderReturnCount(orderId, returnCountMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseReturnStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpPurchaseReturnDO purchaseReturn = validatePurchaseReturnExists(id);
        // 1.2 校验状态
        if (purchaseReturn.getStatus().equals(status)) {
            throw exception(approve ? PURCHASE_RETURN_APPROVE_FAIL : PURCHASE_RETURN_PROCESS_FAIL);
        }
        // 1.3 校验已退款
        if (!approve && purchaseReturn.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw exception(PURCHASE_RETURN_PROCESS_FAIL_EXISTS_REFUND);
        }
        // 3. 变更库存
        List<ErpPurchaseReturnItemDO> purchaseReturnItems = purchaseReturnItemMapper.selectListByReturnId(id);
        Integer bizType = approve ? ErpStockRecordBizTypeEnum.PURCHASE_RETURN.getType()
                : ErpStockRecordBizTypeEnum.PURCHASE_RETURN_CANCEL.getType();
        //变更批次数量
        purchaseReturnItems.forEach(purchaseReturnItem -> {
            ErpPurchaseOrderItemDO erpPurchaseOrderItemDO = purchaseOrderItemMapper.selectById(purchaseReturnItem.getOrderItemId());
            //更新采购订单的出库数量
            updatePurchaseOrderReturnCount(erpPurchaseOrderItemDO.getOrderId());
            //更新批次数量
            updatePurchaseOrderBatchCount(purchaseReturnItem.getOrderItemId());
            //通过approve判断还是入库
            BigDecimal count = approve ? purchaseReturnItem.getCount().negate() : purchaseReturnItem.getCount();
            //创建库存明细信息
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    purchaseReturnItem.getProductId(), purchaseReturnItem.getWarehouseId(), count,
                    bizType, purchaseReturnItem.getReturnId(), purchaseReturnItem.getId(), purchaseReturn.getNo()));
        });
        // 2. 更新状态
        int updateCount = purchaseReturnMapper.updateByIdAndStatus(id, purchaseReturn.getStatus(),
                new ErpPurchaseReturnDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? PURCHASE_RETURN_APPROVE_FAIL : PURCHASE_RETURN_PROCESS_FAIL);
        }
    }

    private void updatePurchaseOrderBatchCount(Long id) {
        List<ErpPurchaseReturnItemDO> erpPurchaseReturnItemDOS = purchaseReturnItemMapper.selectListByItemId(id);
        Iterator<ErpPurchaseReturnItemDO> iterator = erpPurchaseReturnItemDOS.iterator();
        while (iterator.hasNext()) {
            ErpPurchaseReturnItemDO item = iterator.next();
            ErpPurchaseReturnDO erpPurchaseInDO = purchaseReturnMapper.selectById(item.getReturnId());
            if (Objects.equals(erpPurchaseInDO.getStatus(), ErpAuditStatus.APPROVE.getStatus())) {
                iterator.remove();
            }
        }
        if (!erpPurchaseReturnItemDOS.isEmpty()){
            //累加所有
            BigDecimal total = erpPurchaseReturnItemDOS.stream()
                    // 获取每个 PurchaseReturnItem 的 count 属性
                    .map(ErpPurchaseReturnItemDO::getCount)
                    // 利用 reduce 进行累加操作，初始值为 BigDecimal.ZERO
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            erpPurchaseReturnItemDOS.forEach(o -> {
                //更新采购项退货数量
                ErpPurchaseOrderItemDO erpPurchaseOrderItemDO = purchaseOrderItemMapper.selectById(o.getOrderItemId());
                if (erpPurchaseOrderItemDO!=null){
                    BigDecimal subtract = erpPurchaseOrderItemDO.getInCount().subtract(total);
                    if (subtract.compareTo(BigDecimal.ZERO) < 0) {
                        // 如果 subtract 小于 0 的处理逻辑
                        throw exception(PURCHASE_RETURN_ITEM_IS_GREATER_THAN_ORDER_IN_ITEM);
                    }
                    ErpPurchaseOrderDO purchaseOrder = purchaseOrderService.getPurchaseOrder(erpPurchaseOrderItemDO.getOrderId());
                    purchaseOrderMapper.updateById(purchaseOrder.setInCount(purchaseOrder.getInCount().subtract(total)));
                    purchaseOrderItemMapper.updateById(erpPurchaseOrderItemDO.setReturnCount(total).setInCount(subtract));
                    //更新批次数量
                    purchaseInService.updateBatchQuantity(erpPurchaseOrderItemDO.getAssociatedBatchId(),total, 20);
                }
            });
        }
    }

    @Override
    public void updatePurchaseReturnRefundPrice(Long id, BigDecimal refundPrice) {
        ErpPurchaseReturnDO purchaseReturn = purchaseReturnMapper.selectById(id);
        if (purchaseReturn.getRefundPrice().equals(refundPrice)) {
            return;
        }
        if (refundPrice.compareTo(purchaseReturn.getTotalPrice()) > 0) {
            throw exception(PURCHASE_RETURN_FAIL_REFUND_PRICE_EXCEED, refundPrice, purchaseReturn.getTotalPrice());
        }
        purchaseReturnMapper.updateById(new ErpPurchaseReturnDO().setId(id).setRefundPrice(refundPrice));
    }

    private List<ErpPurchaseReturnItemDO> validatePurchaseReturnItems(List<ErpPurchaseReturnSaveReqVO.Item> list) {
        // 1. 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpPurchaseReturnSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 2. 转化为 ErpPurchaseReturnItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpPurchaseReturnItemDO.class, item -> {
            item.setProductUnitId(productMap.get(item.getProductId()).getUnitId());
            item.setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()));
            if (item.getTotalPrice() == null) {
                return;
            }
            if (item.getTaxPercent() != null) {
                item.setTaxPrice(MoneyUtils.priceMultiplyPercent(item.getTotalPrice(), item.getTaxPercent()));
            }
        }));
    }

    private void updatePurchaseReturnItemList(Long id, List<ErpPurchaseReturnItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpPurchaseReturnItemDO> oldList = purchaseReturnItemMapper.selectListByReturnId(id);
        // id 不同，就认为是不同的记录
        List<List<ErpPurchaseReturnItemDO>> diffList = diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));
        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setReturnId(id));
            purchaseReturnItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            purchaseReturnItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            purchaseReturnItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpPurchaseReturnItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseReturn(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpPurchaseReturnDO> purchaseReturns = purchaseReturnMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(purchaseReturns)) {
            return;
        }
        purchaseReturns.forEach(purchaseReturn -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseReturn.getStatus())) {
                throw exception(PURCHASE_RETURN_DELETE_FAIL_APPROVE, purchaseReturn.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        purchaseReturns.forEach(purchaseReturn -> {
            // 2.1 删除订单
            purchaseReturnMapper.deleteById(purchaseReturn.getId());
            // 2.2 删除订单项
            purchaseReturnItemMapper.deleteByReturnId(purchaseReturn.getId());

            // 2.3 更新采购订单的出库数量
            updatePurchaseOrderReturnCount(purchaseReturn.getOrderId());
        });

    }

    private ErpPurchaseReturnDO validatePurchaseReturnExists(Long id) {
        ErpPurchaseReturnDO purchaseReturn = purchaseReturnMapper.selectById(id);
        if (purchaseReturn == null) {
            throw exception(PURCHASE_RETURN_NOT_EXISTS);
        }
        return purchaseReturn;
    }

    @Override
    public ErpPurchaseReturnDO getPurchaseReturn(Long id) {
        return purchaseReturnMapper.selectById(id);
    }

    @Override
    public ErpPurchaseReturnDO validatePurchaseReturn(Long id) {
        ErpPurchaseReturnDO purchaseReturn = getPurchaseReturn(id);
        if (ObjectUtil.notEqual(purchaseReturn.getStatus(), ErpAuditStatus.APPROVE.getStatus())) {
            throw exception(PURCHASE_RETURN_NOT_APPROVE);
        }
        return purchaseReturn;
    }

    @Override
    public PageResult<ErpPurchaseReturnDO> getPurchaseReturnPage(ErpPurchaseReturnPageReqVO pageReqVO) {
        return purchaseReturnMapper.selectPage(pageReqVO);
    }

    // ==================== 采购退货项 ====================

    @Override
    public List<ErpPurchaseReturnItemDO> getPurchaseReturnItemListByReturnId(Long returnId) {
        return purchaseReturnItemMapper.selectListByReturnId(returnId);
    }

    @Override
    public List<ErpPurchaseReturnItemDO> getPurchaseReturnItemListByReturnIds(Collection<Long> returnIds) {
        if (CollUtil.isEmpty(returnIds)) {
            return Collections.emptyList();
        }
        return purchaseReturnItemMapper.selectListByReturnIds(returnIds);
    }

}
