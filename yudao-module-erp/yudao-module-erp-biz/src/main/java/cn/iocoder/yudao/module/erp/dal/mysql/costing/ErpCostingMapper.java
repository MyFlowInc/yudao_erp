package cn.iocoder.yudao.module.erp.dal.mysql.costing;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostingDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.erp.controller.admin.costing.vo.*;

/**
 * 成本核算 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpCostingMapper extends BaseMapperX<ErpCostingDO> {

    default PageResult<ErpCostingDO> selectPage(ErpCostingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpCostingDO>()
                .eqIfPresent(ErpCostingDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpCostingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ErpCostingDO::getAssociationProjectId, reqVO.getAssociationProjectId())
                .betweenIfPresent(ErpCostingDO::getCostingTime, reqVO.getCostingTime())
                .betweenIfPresent(ErpCostingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpCostingDO::getId));
    }

    default int updateByIdAndStatus(Long id, Integer status, ErpCostingDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpCostingDO>()
                .eq(ErpCostingDO::getId, id).eq(ErpCostingDO::getStatus, status));
    }

    default ErpCostingDO selectByNo(String no) {
        return selectOne(ErpCostingDO::getNo, no);
    }
}