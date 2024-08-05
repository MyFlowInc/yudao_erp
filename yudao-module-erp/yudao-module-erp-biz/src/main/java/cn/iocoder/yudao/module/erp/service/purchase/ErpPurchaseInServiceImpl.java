package cn.iocoder.yudao.module.erp.service.purchase;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.in.ErpPurchaseInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.in.ErpPurchaseInSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseInMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseOrderItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpPurchaseOrderMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.finance.ErpAccountService;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static com.fhs.common.constant.Constant.ONE;
import static com.fhs.common.constant.Constant.ZERO;

// TODO 芋艿：记录操作日志

/**
 * ERP 采购入库 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ErpPurchaseInServiceImpl implements ErpPurchaseInService {

    @Resource
    private ErpPurchaseInMapper purchaseInMapper;
    @Resource
    private ErpPurchaseInItemMapper purchaseInItemMapper;
    @Resource
    private ErpPurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;
    @Resource
    private ErpProductBatchMapper productBatchMapper;
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
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchaseIn(ErpPurchaseInSaveReqVO createReqVO) {
        // 1.1 校验采购订单已审核
        ErpPurchaseOrderDO purchaseOrder = purchaseOrderService.validatePurchaseOrder(createReqVO.getOrderId());
        // 1.2 校验入库项的有效性
        List<ErpPurchaseInItemDO> purchaseInItems = validatePurchaseInItems(createReqVO.getItems());
//        // 1.3 校验结算账户
//        accountService.validateAccount(createReqVO.getAccountId());
        // 1.4 生成入库单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.PURCHASE_IN_NO_PREFIX);
        if (purchaseInMapper.selectByNo(no) != null) {
            throw exception(PURCHASE_IN_NO_EXISTS);
        }
        // 2.1 插入入库
        ErpPurchaseInDO purchaseIn = BeanUtils.toBean(createReqVO, ErpPurchaseInDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus()))
                .setOrderNo(purchaseOrder.getNo()).setSupplierId(purchaseOrder.getSupplierId());
        calculateTotalPrice(purchaseIn, purchaseInItems);

        purchaseInMapper.insert(purchaseIn);
        // 2.2 插入入库项
        purchaseInItems.forEach(o ->{
            ErpPurchaseOrderItemDO erpPurchaseOrderItemDO = purchaseOrderItemMapper.selectById(o.getOrderItemId());
            //判断入库数量是不是 >采购数
            if (erpPurchaseOrderItemDO != null){
               //判断入库数量是不是 >采购数-已入库数
                if (o.getCount().compareTo(erpPurchaseOrderItemDO.getCount().subtract(erpPurchaseOrderItemDO.getInCount())) > 0) {
                    throw exception(PURCHASE_IN_PROCESS_FAIL_MAX_STOCK);
                }
                o.setInId(purchaseIn.getId());
                o.setAssociatedBatchId(erpPurchaseOrderItemDO.getAssociatedBatchId());
            }
        } );
        purchaseInItemMapper.insertBatch(purchaseInItems);
        return purchaseIn.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseIn(ErpPurchaseInSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpPurchaseInDO purchaseIn = validatePurchaseInExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseIn.getStatus())) {
            throw exception(PURCHASE_IN_UPDATE_FAIL_APPROVE, purchaseIn.getNo());
        }
        // 1.2 校验采购订单已审核
        ErpPurchaseOrderDO purchaseOrder = purchaseOrderService.validatePurchaseOrder(updateReqVO.getOrderId());
        // 1.3 校验结算账户
        accountService.validateAccount(updateReqVO.getAccountId());
        // 1.4 校验订单项的有效性
        List<ErpPurchaseInItemDO> purchaseInItems = validatePurchaseInItems(updateReqVO.getItems());

        // 2.1 更新入库
        ErpPurchaseInDO updateObj = BeanUtils.toBean(updateReqVO, ErpPurchaseInDO.class)
                .setOrderNo(purchaseOrder.getNo()).setSupplierId(purchaseOrder.getSupplierId());
        calculateTotalPrice(updateObj, purchaseInItems);
        purchaseInMapper.updateById(updateObj);
        // 2.2 更新入库项
        updatePurchaseInItemList(updateReqVO.getId(), purchaseInItems);

//        // 3.1 更新采购订单的入库数量
//        updatePurchaseOrderInCount(updateObj.getOrderId());
        // 3.2 注意：如果采购订单编号变更了，需要更新“老”采购订单的入库数量
//        if (ObjectUtil.notEqual(purchaseIn.getOrderId(), updateObj.getOrderId())) {
//            updatePurchaseOrderInCount(purchaseIn.getOrderId());
//        }
    }

    private void calculateTotalPrice(ErpPurchaseInDO purchaseIn, List<ErpPurchaseInItemDO> purchaseInItems) {
        purchaseIn.setTotalCount(getSumValue(purchaseInItems, ErpPurchaseInItemDO::getCount, BigDecimal::add));
        purchaseIn.setTotalProductPrice(getSumValue(purchaseInItems, ErpPurchaseInItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
        purchaseIn.setTotalTaxPrice(getSumValue(purchaseInItems, ErpPurchaseInItemDO::getTaxPrice, BigDecimal::add, BigDecimal.ZERO));
        purchaseIn.setTotalPrice(purchaseIn.getTotalProductPrice().add(purchaseIn.getTotalTaxPrice()));
        // 计算优惠价格
        if (purchaseIn.getDiscountPercent() == null) {
            purchaseIn.setDiscountPercent(BigDecimal.ZERO);
        }
        purchaseIn.setDiscountPrice(MoneyUtils.priceMultiplyPercent(purchaseIn.getTotalPrice(), purchaseIn.getDiscountPercent()));
        purchaseIn.setTotalPrice(purchaseIn.getTotalPrice().subtract(purchaseIn.getDiscountPrice()).add(purchaseIn.getOtherPrice()));
    }

    private void updatePurchaseOrderInCount(Long orderId) {
        // 1.1 查询采购订单对应的采购入库单列表
        List<ErpPurchaseInDO> purchaseIns = purchaseInMapper.selectListByOrderId(orderId);
        // 1.2 查询对应的采购订单项的入库数量
        Map<Long, BigDecimal> returnCountMap = purchaseInItemMapper.selectOrderItemCountSumMapByInIds(
                convertList(purchaseIns, ErpPurchaseInDO::getId));
        // 2. 更新采购订单的入库数量
        purchaseOrderService.updatePurchaseOrderInCount(orderId, returnCountMap);
    }
    //校验此采购项是否已结束流程
    public void verifyIfselected(){
        throw exception(PURCHASE_RETURN_IS_END);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseInStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpPurchaseInDO purchaseIn = validatePurchaseInExists(id);
        // 1.2 校验状态
        if (purchaseIn.getStatus().equals(status)) {
            throw exception(approve ? PURCHASE_IN_APPROVE_FAIL : PURCHASE_IN_PROCESS_FAIL);
        }
        // 1.3 校验已付款
        if (!approve && purchaseIn.getPaymentPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw exception(PURCHASE_IN_PROCESS_FAIL_EXISTS_PAYMENT);
        }

        if (Objects.equals(status, ErpAuditStatus.APPROVE.getStatus())){
            List<ErpPurchaseInItemDO> erpPurchaseInItemDOS = purchaseInItemMapper.selectListByInId(id);

            erpPurchaseInItemDOS = erpPurchaseInItemDOS.stream()
                    .filter(item -> !item.getDeleted())
                    .collect(Collectors.toList());

            erpPurchaseInItemDOS.forEach( o->{
                        ErpPurchaseOrderItemDO erpPurchaseOrderItemDO = purchaseOrderItemMapper.selectById(o.getOrderItemId());
                        if (erpPurchaseOrderItemDO != null){
                            //判断入库数量是不是 >采购数-已入库数
                            if (o.getCount().compareTo(erpPurchaseOrderItemDO.getCount().subtract(erpPurchaseOrderItemDO.getInCount())) > 0) {
                                throw exception(PURCHASE_IN_PROCESS_FAIL_MAX_STOCK);
                            }
                        }
                    });

            // 如果订单中存在采购项
            if (!erpPurchaseInItemDOS.isEmpty()) {
                // 根据 OrderItemId 分组
                Map<Long, List<ErpPurchaseInItemDO>> groupedByProductIds = erpPurchaseInItemDOS.stream()
                        .collect(Collectors.groupingBy(ErpPurchaseInItemDO::getOrderItemId));
                List<ErpPurchaseInItemDO> itemsWithDifferentProductId = groupedByProductIds.values().stream()
                        // 只保留 AssociatedRequisitionProductId 唯一的项
                        .filter(list -> list.size() == 1)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                // 没有相同的采购项
                if (!itemsWithDifferentProductId.isEmpty()) {
                    itemsWithDifferentProductId.forEach(item -> {
                        ErpPurchaseOrderItemDO erpPurchaseOrderItemDO = purchaseOrderItemMapper.selectById(item.getOrderItemId());
                        ErpPurchaseOrderDO erpPurchaseOrderDO = purchaseOrderMapper.selectById(erpPurchaseOrderItemDO.getOrderId());
                        List<ErpPurchaseInItemDO> erpPurchaseInItemDOS1 = purchaseInItemMapper.selectListByOrderId(erpPurchaseOrderItemDO.getId());
                        Iterator<ErpPurchaseInItemDO> iterator = erpPurchaseInItemDOS1.iterator();
                        while (iterator.hasNext()) {
                            // 使用迭代器的去除为审核的数据
                            ErpPurchaseInItemDO items = iterator.next();
                            ErpPurchaseInDO erpPurchaseInDO = purchaseInMapper.selectById(items.getInId());
                            if (Objects.equals(erpPurchaseInDO.getStatus(), ErpAuditStatus.APPROVE.getStatus())) {
                                iterator.remove();
                            }
                        }
                        BigDecimal totalCount = erpPurchaseInItemDOS1.stream()
                                // 获取每个对象的 count 字段值
                                .map(ErpPurchaseInItemDO::getCount)
                                // 过滤掉空值
                                .filter(Objects::nonNull)
                                // 初始值为 BigDecimal.ZERO，累加所有值
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        // 比较入库单数量与采购数量
                        if (compareBigDecimal(totalCount, erpPurchaseOrderItemDO.getCount()) > ZERO) {
                            throw exception(PURCHASE_IN_PROCESS_FAIL_MAX_STOCK);
                        }
                            //更新采购订单的入库数量
                            updatePurchaseOrderInCount(erpPurchaseOrderDO.getId());
                            //更新批次数量
                            updateBatchQuantity(erpPurchaseOrderItemDO.getAssociatedBatchId(),totalCount,10);
                            //更新采购项已采购数量
                            purchaseOrderItemMapper.updateById(erpPurchaseOrderItemDO.setInCount(totalCount));
//                        }
                    });
                }
                }
            }
        // 2. 先更新状态
        int updateCount = purchaseInMapper.updateByIdAndStatus(id, purchaseIn.getStatus(),
                new ErpPurchaseInDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? PURCHASE_IN_APPROVE_FAIL : PURCHASE_IN_PROCESS_FAIL);
        }
        // 3. 变更库存
        List<ErpPurchaseInItemDO> purchaseInItems = purchaseInItemMapper.selectListByInId(id);
        Integer bizType = approve ? ErpStockRecordBizTypeEnum.PURCHASE_IN.getType()
                : ErpStockRecordBizTypeEnum.PURCHASE_IN_CANCEL.getType();
        purchaseInItems.forEach(purchaseInItem -> {
            BigDecimal count = approve ? purchaseInItem.getCount() : purchaseInItem.getCount().negate();
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    purchaseInItem.getProductId(), purchaseInItem.getWarehouseId(), count,
                    bizType, purchaseInItem.getInId(), purchaseInItem.getId(), purchaseIn.getNo()));
        });
    }

    @Override
    public void updatePurchaseInPaymentPrice(Long id, BigDecimal paymentPrice) {
        ErpPurchaseInDO purchaseIn = purchaseInMapper.selectById(id);
        if (purchaseIn.getPaymentPrice().equals(paymentPrice)) {
            return;
        }
        if (paymentPrice.compareTo(purchaseIn.getTotalPrice()) > 0) {
            throw exception(PURCHASE_IN_FAIL_PAYMENT_PRICE_EXCEED, paymentPrice, purchaseIn.getTotalPrice());
        }
        purchaseInMapper.updateById(new ErpPurchaseInDO().setId(id).setPaymentPrice(paymentPrice));
    }

    private List<ErpPurchaseInItemDO> validatePurchaseInItems(List<ErpPurchaseInSaveReqVO.Item> list) {
        // 1. 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpPurchaseInSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 2. 转化为 ErpPurchaseInItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpPurchaseInItemDO.class, item -> {
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

    public static int compareBigDecimal(BigDecimal bd1, BigDecimal bd2) {
        return bd1.compareTo(bd2);
    }
    //更新产品批次数量
    @Override
    public void updateBatchQuantity(Long id,BigDecimal count,Integer status){
        ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(id);
        // 使用三元条件表达式根据status的值来确定count的处理方式
        count = (status == 10) ? count.abs() : (status == 20) ? count.negate() : count;
        // 空值检查
        if (erpProductBatchDO != null && count != null) {
            // 获取当前的库存数量
            BigDecimal currentQuantity = erpProductBatchDO.getInventoryQuantity();
            // 将 count 添加到当前库存数量
            BigDecimal newQuantity = currentQuantity.add(count);
            // 更新 ErpProductBatchDO 对象的库存数量
            erpProductBatchDO.setInventoryQuantity(newQuantity);
            productBatchMapper.updateById(erpProductBatchDO);
        }
    }
    private void updatePurchaseInItemList(Long id, List<ErpPurchaseInItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpPurchaseInItemDO> oldList = purchaseInItemMapper.selectListByInId(id);
        // id 不同，就认为是不同的记录
        List<List<ErpPurchaseInItemDO>> diffList = diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setInId(id));

            purchaseInItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            purchaseInItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            purchaseInItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpPurchaseInItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseIn(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpPurchaseInDO> purchaseIns = purchaseInMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(purchaseIns)) {
            return;
        }
        purchaseIns.forEach(purchaseIn -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseIn.getStatus())) {
                throw exception(PURCHASE_IN_DELETE_FAIL_APPROVE, purchaseIn.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        purchaseIns.forEach(purchaseIn -> {
            // 2.1 删除订单
            purchaseInMapper.deleteById(purchaseIn.getId());
            // 2.2 删除订单项
            purchaseInItemMapper.deleteByInId(purchaseIn.getId());

            // 2.3 更新采购订单的入库数量
            updatePurchaseOrderInCount(purchaseIn.getOrderId());
        });

    }

    private ErpPurchaseInDO validatePurchaseInExists(Long id) {
        ErpPurchaseInDO purchaseIn = purchaseInMapper.selectById(id);
        if (purchaseIn == null) {
            throw exception(PURCHASE_IN_NOT_EXISTS);
        }
        return purchaseIn;
    }

    @Override
    public ErpPurchaseInDO getPurchaseIn(Long id) {
        return purchaseInMapper.selectById(id);
    }

    @Override
    public ErpPurchaseInDO validatePurchaseIn(Long id) {
        ErpPurchaseInDO purchaseIn = validatePurchaseInExists(id);
        if (ObjectUtil.notEqual(purchaseIn.getStatus(), ErpAuditStatus.APPROVE.getStatus())) {
            throw exception(PURCHASE_IN_NOT_APPROVE);
        }
        return purchaseIn;
    }

    @Override
    public PageResult<ErpPurchaseInDO> getPurchaseInPage(ErpPurchaseInPageReqVO pageReqVO) {
        return purchaseInMapper.selectPage(pageReqVO);
    }

    // ==================== 采购入库项 ====================

    @Override
    public List<ErpPurchaseInItemDO> getPurchaseInItemListByInId(Long inId) {
        return purchaseInItemMapper.selectListByInId(inId);
    }

    @Override
    public List<ErpPurchaseInItemDO> getPurchaseInItemListByInIds(Collection<Long> inIds) {
        if (CollUtil.isEmpty(inIds)) {
            return Collections.emptyList();
        }
        return purchaseInItemMapper.selectListByInIds(inIds);
    }

}
