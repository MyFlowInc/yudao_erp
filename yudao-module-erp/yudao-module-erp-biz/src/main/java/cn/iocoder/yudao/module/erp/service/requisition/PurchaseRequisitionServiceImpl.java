package cn.iocoder.yudao.module.erp.service.requisition;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.order.ErpPurchaseOrderSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.service.finance.ErpAccountService;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.purchase.ErpSupplierService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.requisition.PurchaseRequisitionMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.requisition.RequisitionProductMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * 新增请购 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class PurchaseRequisitionServiceImpl implements PurchaseRequisitionService {

    @Resource
    private PurchaseRequisitionMapper purchaseRequisitionMapper;
    @Resource
    private RequisitionProductMapper requisitionProductMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ErpProductService productService;

    @Resource
    private ErpAccountService accountService;

    @Override
    public Long createPurchaseRequisition(PurchaseRequisitionSaveReqVO createReqVO) {
        // 1.1 校验订单项的有效性
        List<RequisitionProductDO> requisitionItems = validateRequisitionItems(createReqVO.getItems());
        // 1.2 生成订单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.REQUISITION_ORDER_NO_PREFIX);
        if (purchaseRequisitionMapper.selectByNo(no) != null) {
            throw exception(REQUISITION_ORDER_NO_EXISTS);
        }

        // 2.1 插入订单
        PurchaseRequisitionDO purchaseRequisition = BeanUtils.toBean(createReqVO, PurchaseRequisitionDO.class, in -> in
                .setRequisitionCode(no).setStatus(ErpAuditStatus.PROCESS.getStatus()));
        purchaseRequisitionMapper.insert(purchaseRequisition);
        // 2.2 插入订单项
        requisitionItems.forEach(o -> o.setAssociationRequisition(purchaseRequisition.getId()));
        requisitionProductMapper.insertBatch(requisitionItems);
        return purchaseRequisition.getId();
    }

    @Override
    public void updatePurchaseRequisition(PurchaseRequisitionSaveReqVO updateReqVO) {
        // 1.1 校验存在
        PurchaseRequisitionDO purchaseRequisition = validatePurchaseRequisitionExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseRequisition.getStatus())) {
            throw exception(REQUISITION_ORDER_UPDATE_FAIL_APPROVE, purchaseRequisition.getRequisitionCode());
        }
        // 1.2 校验订单项的有效性
        List<RequisitionProductDO> requisitionItems = validateRequisitionItems(updateReqVO.getItems());

        // 2.1 更新订单
        PurchaseRequisitionDO updateObj = BeanUtils.toBean(updateReqVO, PurchaseRequisitionDO.class);
        purchaseRequisitionMapper.updateById(updateObj);
        // 2.2 更新订单项
        updateRequisitionProductItemList(updateReqVO.getId(), requisitionItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseRequisition(List<Long> ids) {
        // 1. 校验不处于已审批
        List<PurchaseRequisitionDO> purchaseRequisitions = purchaseRequisitionMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(purchaseRequisitions)) {
            return;
        }
        purchaseRequisitions.forEach(purchaseOrder -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(purchaseOrder.getStatus())) {
                throw exception(REQUISITION_ORDER_UPDATE_FAIL_APPROVE, purchaseOrder.getRequisitionCode());
            }
        });

        // 2. 遍历删除，并记录操作日志
        purchaseRequisitions.forEach(purchaseOrder -> {
            // 2.1 删除订单
            purchaseRequisitionMapper.deleteById(purchaseOrder.getId());
            // 2.2 删除订单项
            requisitionProductMapper.deleteByAssociationRequisition(purchaseOrder.getId());
        });
    }

    private PurchaseRequisitionDO validatePurchaseRequisitionExists(Long id) {
        PurchaseRequisitionDO purchaseRequisition = purchaseRequisitionMapper.selectById(id);
        if (purchaseRequisition == null) {
            throw exception(PURCHASE_REQUISITION_NOT_EXISTS);
        }
        return purchaseRequisition;
    }

    @Override
    public PurchaseRequisitionDO getPurchaseRequisition(Long id) {
        return purchaseRequisitionMapper.selectById(id);
    }

    @Override
    public PageResult<PurchaseRequisitionDO> getPurchaseRequisitionPage(PurchaseRequisitionPageReqVO pageReqVO) {
        return purchaseRequisitionMapper.selectPage(pageReqVO);
    }

    private List<RequisitionProductDO> validateRequisitionItems(List<PurchaseRequisitionSaveReqVO.Item> list) {
        // 1. 校验产品存在
        productService.validProductList(convertSet(list, PurchaseRequisitionSaveReqVO.Item::getProductId));
        // 2. 转化为 RequisitionProductDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, RequisitionProductDO.class));
    }

    private void updateRequisitionProductItemList(Long id, List<RequisitionProductDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<RequisitionProductDO> oldList = requisitionProductMapper.selectListByOrderId(id);
        List<List<RequisitionProductDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setAssociationRequisition(id));
            requisitionProductMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            requisitionProductMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            requisitionProductMapper.deleteBatchIds(convertList(diffList.get(2), RequisitionProductDO::getId));
        }
    }

    @Override
    public void updatePurchaseRequisitionStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        PurchaseRequisitionDO purchaseRequisition = validatePurchaseRequisitionExists(id);
        // 1.2 校验状态
        if (purchaseRequisition.getStatus().equals(status)) {
            throw exception(approve ? REQUISITION_ORDER_UPDATE_FAIL_APPROVE : REQUISITION_ORDER__PROCESS_FAIL);
        }

        // 2. 更新状态
        int updateCount = purchaseRequisitionMapper.updateByIdAndStatus(id, purchaseRequisition.getStatus(),
                new PurchaseRequisitionDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? REQUISITION_ORDER_UPDATE_FAIL_APPROVE : REQUISITION_ORDER__PROCESS_FAIL);
        }
    }

    // ==================== 请购单项 ====================

    @Override
    public List<RequisitionProductDO> getRequisitionProductListByOrderId(Long orderId) {
        return requisitionProductMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<RequisitionProductDO> getRequisitionProductListByOrderIds(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        return requisitionProductMapper.selectListByOrderIds(orderIds);
    }

}