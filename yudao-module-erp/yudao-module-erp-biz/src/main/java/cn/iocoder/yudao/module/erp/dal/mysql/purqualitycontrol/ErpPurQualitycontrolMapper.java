package cn.iocoder.yudao.module.erp.dal.mysql.purqualitycontrol;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purqualitycontrol.ErpPurQualitycontrolDO;

import java.util.List;

public interface ErpPurQualitycontrolMapper extends BaseMapperX<ErpPurQualitycontrolDO> {
    default List<ErpPurQualitycontrolDO> selectPurQualitycontrolList(ErpPurQualitycontrolDO erpPurQualitycontrolDO) {
        return selectList(ErpPurQualitycontrolDO::getType, erpPurQualitycontrolDO.getType());
    }
}
