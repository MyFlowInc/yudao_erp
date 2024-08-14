package cn.iocoder.yudao.module.erp.dal.mysql.costing;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成本核算项 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpCostItemMapper extends BaseMapperX<ErpCostItemDO> {

    default List<ErpCostItemDO> selectListByCostId(Long costId) {
        return selectList(ErpCostItemDO::getCostId, costId);
    }

    default int deleteByCostId(Long costId) {
        return delete(ErpCostItemDO::getCostId, costId);
    }

}