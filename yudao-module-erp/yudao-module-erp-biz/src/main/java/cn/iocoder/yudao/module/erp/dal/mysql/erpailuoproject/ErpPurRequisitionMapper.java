package cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurRequisitionDO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 15276
 */
@Mapper
@DS("ailuo")
public interface ErpPurRequisitionMapper extends BaseMapperX<ErpPurRequisitionDO> {
    default List<ErpPurRequisitionDO> selectErpPurRequisitionList(ErpPurRequisitionDO erpPurItem) {
        MPJLambdaWrapperX<ErpPurRequisitionDO> query = new MPJLambdaWrapperX<ErpPurRequisitionDO>()
                .eqIfPresent(ErpPurRequisitionDO::getType, erpPurItem.getType())
                .eqIfPresent(ErpPurRequisitionDO::getUuid, erpPurItem.getUuid());
        return selectList(query);
    }
}
