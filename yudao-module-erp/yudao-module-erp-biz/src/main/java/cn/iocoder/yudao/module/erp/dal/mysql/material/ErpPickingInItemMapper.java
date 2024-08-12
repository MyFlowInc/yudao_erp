package cn.iocoder.yudao.module.erp.dal.mysql.material;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 领料出库单项 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpPickingInItemMapper extends BaseMapperX<ErpPickingInItemDO> {

    default List<ErpPickingInItemDO> selectListByInId(Long inId) {
        return selectList(ErpPickingInItemDO::getInId, inId);
    }
    default int deleteByInId(Long inId) {
        return delete(ErpPickingInItemDO::getInId, inId);
    }
    default List<ErpPickingInItemDO> selectListByInIds(Collection<Long> inIds) {
        return selectList(ErpPickingInItemDO::getInId, inIds);
    }
}