package cn.iocoder.yudao.module.erp.service.requisition;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 新增请购 Service 接口
 *
 * @author 那就这样吧
 */
public interface PurchaseRequisitionService {

    /**
     * 创建新增请购
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseRequisition(@Valid PurchaseRequisitionSaveReqVO createReqVO);

    /**
     * 更新新增请购
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseRequisition(@Valid PurchaseRequisitionSaveReqVO updateReqVO);

    /**
     * 删除新增请购
     *
     * @param id 编号
     */
    void deletePurchaseRequisition(Long id);

    /**
     * 获得新增请购
     *
     * @param id 编号
     * @return 新增请购
     */
    PurchaseRequisitionDO getPurchaseRequisition(Long id);

    /**
     * 获得新增请购分页
     *
     * @param pageReqVO 分页查询
     * @return 新增请购分页
     */
    PageResult<PurchaseRequisitionDO> getPurchaseRequisitionPage(PurchaseRequisitionPageReqVO pageReqVO);

    // ==================== 子表（请购产品） ====================

    /**
     * 获得请购产品分页
     *
     * @param pageReqVO 分页查询
     * @param associationRequisition 关联请购单
     * @return 请购产品分页
     */
    PageResult<RequisitionProductDO> getRequisitionProductPage(PageParam pageReqVO, String associationRequisition);

    /**
     * 创建请购产品
     *
     * @param requisitionProduct 创建信息
     * @return 编号
     */
    Long createRequisitionProduct(@Valid RequisitionProductDO requisitionProduct);

    /**
     * 更新请购产品
     *
     * @param requisitionProduct 更新信息
     */
    void updateRequisitionProduct(@Valid RequisitionProductDO requisitionProduct);

    /**
     * 删除请购产品
     *
     * @param id 编号
     */
    void deleteRequisitionProduct(Long id);

	/**
	 * 获得请购产品
	 *
	 * @param id 编号
     * @return 请购产品
	 */
    RequisitionProductDO getRequisitionProduct(Long id);

}