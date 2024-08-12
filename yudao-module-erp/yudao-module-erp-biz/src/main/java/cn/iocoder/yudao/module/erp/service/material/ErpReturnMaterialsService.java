package cn.iocoder.yudao.module.erp.service.material;

import java.util.*;

import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import javax.validation.Valid;

/**
 * ERP 还料入库单 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpReturnMaterialsService {

    /**
     * 创建ERP 还料入库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReturnMaterials(@Valid ErpReturnMaterialsSaveReqVO createReqVO);

    /**
     * 更新ERP 还料入库单
     *
     * @param updateReqVO 更新信息
     */
    void updateReturnMaterials(@Valid ErpReturnMaterialsSaveReqVO updateReqVO);

    /**
     * 更新还料入库单的状态
     *
     * @param id 编号
     * @param status 状态
     */
    void updateReturnMaterialsStatus(Long id, Integer status);

    /**
     * 删除ERP 还料入库单
     *
     * @param ids 编号
     */
    void deleteReturnMaterials(List<Long> ids);
    /**
     * 获得ERP 还料入库单
     *
     * @param id 编号
     * @return ERP 还料入库单
     */
    ErpReturnMaterialsDO getReturnMaterials(Long id);

    /**
     * 获得ERP 还料入库单分页
     *
     * @param pageReqVO 分页查询
     * @return ERP 还料入库单分页
     */
    PageResult<ErpReturnMaterialsDO> getReturnMaterialsPage(ErpReturnMaterialsPageReqVO pageReqVO);

    // ==================== 子表（ERP 还料入库单项） ====================

    /**
     * 获得ERP 还料入库单项列表
     *
     * @param returnId 还料单编号
     * @return ERP 还料入库单项列表
     */
    List<ErpReturnMaterialsItemDO> getReturnMaterialsItemListByReturnId(Long returnId);

    /**
     * 获得所有关联同一领料单的ERP 还料入库单项列表
     *
     * @param id 领料项编号
     * @return ERP 还料入库单项列表
     */
    List<ErpReturnMaterialsItemDO> selectListByPickingItemId(Long id);


    List<ErpReturnMaterialsItemDO> selectListByReturnIds(Collection<Long> inIds);
}