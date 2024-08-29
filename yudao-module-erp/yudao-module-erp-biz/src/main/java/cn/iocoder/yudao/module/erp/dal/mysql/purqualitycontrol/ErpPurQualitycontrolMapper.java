package cn.iocoder.yudao.module.erp.dal.mysql.purqualitycontrol;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.purqualitycontrol.ErpPurQualitycontrolDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface ErpPurQualitycontrolMapper extends BaseMapperX<ErpPurQualitycontrolDO> {
    default List<ErpPurQualitycontrolDO> selectPurQualitycontrolList(ErpPurQualitycontrolDO erpPurQualitycontrolDO) {
        return selectList(ErpPurQualitycontrolDO::getType, erpPurQualitycontrolDO.getType());
    }
}
