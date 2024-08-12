package cn.iocoder.yudao.module.erp.service.material;

import java.util.*;

import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;

import javax.validation.Valid;

/**
 * ERP 领料出库单 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpPickingInService {

    /**
     * 创建ERP 领料出库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPickingIn(@Valid ErpPickingInSaveReqVO createReqVO);

    /**
     * 更新ERP 领料出库单
     *
     * @param updateReqVO 更新信息
     */
    void updatePickingIn(@Valid ErpPickingInSaveReqVO updateReqVO);
    /**
     * 更新领料出库单的状态
     *
     * @param id 编号
     * @param status 状态
     */
    void updatePickingInStatus(Long id, Integer status);
    /**
     * 删除ERP 领料出库单
     *
     * @param id 编号
     */
    void deletePickingIn(List<String> id);

    /**
     * 获得ERP 领料出库单
     *
     * @param id 编号
     * @return ERP 领料出库单
     */
    ErpPickingInDO getPickingIn(Long id);

    /**
     * 获得ERP 领料出库单分页
     *
     * @param pageReqVO 分页查询
     * @return ERP 领料出库单分页
     */
    PageResult<ErpPickingInDO> getPickingInPage(ErpPickingInPageReqVO pageReqVO);

    // ==================== 子表（ERP 领料出库单项） ====================

    /**
     * 获得ERP 领料出库单项列表
     *
     * @param inId 领料单编号
     * @return ERP 领料出库单项列表
     */
    List<ErpPickingInItemDO> getPickingInItemListByInId(Long inId);

    List<ErpPickingInItemDO> selectListByInIds(Collection<Long> inIds);
}