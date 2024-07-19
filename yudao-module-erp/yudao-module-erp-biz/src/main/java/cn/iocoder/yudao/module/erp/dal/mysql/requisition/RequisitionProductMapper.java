package cn.iocoder.yudao.module.erp.dal.mysql.requisition;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请购产品 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface RequisitionProductMapper extends BaseMapperX<RequisitionProductDO> {

    default PageResult<RequisitionProductDO> selectPage(PageParam reqVO, String associationRequisition) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RequisitionProductDO>()
            .eq(RequisitionProductDO::getAssociationRequisition, associationRequisition)
            .orderByDesc(RequisitionProductDO::getId));
    }

    default int deleteByAssociationRequisition(String associationRequisition) {
        return delete(RequisitionProductDO::getAssociationRequisition, associationRequisition);
    }

}