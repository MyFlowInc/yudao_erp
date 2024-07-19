package cn.iocoder.yudao.module.erp.service.requisition;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Long createPurchaseRequisition(PurchaseRequisitionSaveReqVO createReqVO) {
        // 插入
        PurchaseRequisitionDO purchaseRequisition = BeanUtils.toBean(createReqVO, PurchaseRequisitionDO.class);
        purchaseRequisitionMapper.insert(purchaseRequisition);
        // 返回
        return purchaseRequisition.getId();
    }

    @Override
    public void updatePurchaseRequisition(PurchaseRequisitionSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseRequisitionExists(updateReqVO.getId());
        // 更新
        PurchaseRequisitionDO updateObj = BeanUtils.toBean(updateReqVO, PurchaseRequisitionDO.class);
        purchaseRequisitionMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseRequisition(Long id) {
        // 校验存在
        validatePurchaseRequisitionExists(id);
        // 删除
        purchaseRequisitionMapper.deleteById(id);

        // 删除子表
        deleteRequisitionProductByAssociationRequisition(id);
    }

    private void validatePurchaseRequisitionExists(Long id) {
        if (purchaseRequisitionMapper.selectById(id) == null) {
            throw exception(PURCHASE_REQUISITION_NOT_EXISTS);
        }
    }

    @Override
    public PurchaseRequisitionDO getPurchaseRequisition(Long id) {
        return purchaseRequisitionMapper.selectById(id);
    }

    @Override
    public PageResult<PurchaseRequisitionDO> getPurchaseRequisitionPage(PurchaseRequisitionPageReqVO pageReqVO) {
        return purchaseRequisitionMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（请购产品） ====================

    @Override
    public PageResult<RequisitionProductDO> getRequisitionProductPage(PageParam pageReqVO, String associationRequisition) {
        return requisitionProductMapper.selectPage(pageReqVO, associationRequisition);
    }

    @Override
    public Long createRequisitionProduct(RequisitionProductDO requisitionProduct) {
        requisitionProductMapper.insert(requisitionProduct);
        return requisitionProduct.getId();
    }

    @Override
    public void updateRequisitionProduct(RequisitionProductDO requisitionProduct) {
        // 校验存在
        validateRequisitionProductExists(requisitionProduct.getId());
        // 更新
        requisitionProductMapper.updateById(requisitionProduct);
    }

    @Override
    public void deleteRequisitionProduct(Long id) {
        // 校验存在
        validateRequisitionProductExists(id);
        // 删除
        requisitionProductMapper.deleteById(id);
    }

    @Override
    public List<RequisitionProductDO> getRequisitionProduct(Long id) {
        return requisitionProductMapper.selectListProductId(id);
    }


    private void validateRequisitionProductExists(Long id) {
        if (requisitionProductMapper.selectById(id) == null) {
            throw exception(REQUISITION_PRODUCT_NOT_EXISTS);
        }
    }

    private void deleteRequisitionProductByAssociationRequisition(Long associationRequisition) {
        requisitionProductMapper.deleteByAssociationRequisition(String.valueOf(associationRequisition));
    }


}