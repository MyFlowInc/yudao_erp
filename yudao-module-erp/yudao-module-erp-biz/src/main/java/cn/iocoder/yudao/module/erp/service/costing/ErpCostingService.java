package cn.iocoder.yudao.module.erp.service.costing;

import java.util.*;
import cn.iocoder.yudao.module.erp.controller.admin.costing.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostingDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

import javax.validation.Valid;

/**
 * 成本核算 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpCostingService {

    /**
     * 创建成本核算
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCosting(@Valid ErpCostingSaveReqVO createReqVO);

    /**
     * 更新成本核算
     *
     * @param updateReqVO 更新信息
     */
    void updateCosting(@Valid ErpCostingSaveReqVO updateReqVO);

    void updateByIdAndStatus(Long id, Integer status);

    /**
     * 删除成本核算
     *
     * @param ids 编号
     */
    void deleteCosting(List<Long> ids);

    /**
     * 获得成本核算
     *
     * @param id 编号
     * @return 成本核算
     */
    ErpCostingDO getCosting(Long id);

    /**
     * 获得成本核算分页
     *
     * @param pageReqVO 分页查询
     * @return 成本核算分页
     */
    PageResult<ErpCostingDO> getCostingPage(ErpCostingPageReqVO pageReqVO);

    // ==================== 子表（成本核算项） ====================

    /**
     * 获得成本核算项列表
     *
     * @param costId 核算单id
     * @return 成本核算项列表
     */
    List<ErpCostItemDO> getCostItemListByCostId(Long costId);

}