package cn.iocoder.yudao.module.erp.service.requisition;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

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
     * @param ids 编号
     */
    void deletePurchaseRequisition(List<Long> ids);

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

    /**
     * 获得所有未完成的请购单
     *
     * @param reqVO 分页查询
     * @return 未完成的请购单
     */
    List<PurchaseRequisitionDO> selectStatusIsNotEndList(PurchaseRequisitionPageReqVO reqVO);

    // ==================== 请购单项 ====================

    /**
     * 获得请购单项列表
     *
     * @param orderId 请购单编号
     * @return 请购单项列表
     */
    List<RequisitionProductDO> getRequisitionProductListByOrderId(Long orderId);

    /**
     * 更新请购单的状态
     *
     * @param id 编号
     * @param status 状态
     */
    void updatePurchaseRequisitionStatus(Long id, Integer status);
    /**
     * 获得请购项
     *
     * @param id 编号
     * @return 请购项
     */
    RequisitionProductDO getPurchaseRequisitionProduct(Long id);

    PurchaseRequisitionDO selectByNo(String no);

    List<PurchaseRequisitionDO> getPurchaseRequisitionList(Collection<Long> ids);

    default Map<Long, PurchaseRequisitionDO> getPurchaseRequisitionMap(Collection<Long> ids) {
        return convertMap(getPurchaseRequisitionList(ids), PurchaseRequisitionDO::getId);
    }
}