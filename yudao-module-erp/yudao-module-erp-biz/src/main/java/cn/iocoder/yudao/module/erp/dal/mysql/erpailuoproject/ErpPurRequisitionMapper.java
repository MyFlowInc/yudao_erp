package cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurRequisitionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpPurRequisitionMapper extends BaseMapperX<ErpPurRequisitionDO> {
    default List<ErpPurRequisitionDO> selectErpPurRequisitionList(ErpPurRequisitionDO erpPurItem) {
        return selectList(ErpPurRequisitionDO::getType, erpPurItem.getType());
    }
}
