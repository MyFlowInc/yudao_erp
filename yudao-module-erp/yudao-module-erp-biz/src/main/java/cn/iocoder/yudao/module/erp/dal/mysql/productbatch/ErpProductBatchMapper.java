package cn.iocoder.yudao.module.erp.dal.mysql.productbatch;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo.*;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * ERP产品批次信息 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpProductBatchMapper extends BaseMapperX<ErpProductBatchDO> {

    default PageResult<ErpProductBatchDO> selectPage(ErpProductBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpProductBatchDO>()
                .eqIfPresent(ErpProductBatchDO::getId, reqVO.getId())
                .likeIfPresent(ErpProductBatchDO::getName, reqVO.getName())
                .eqIfPresent(ErpProductBatchDO::getType, reqVO.getType())
                .eqIfPresent(ErpProductBatchDO::getAssociationProductId, reqVO.getAssociationProductId())
                .eqIfPresent(ErpProductBatchDO::getUnitPrice, reqVO.getUnitPrice())
                .eqIfPresent(ErpProductBatchDO::getCode, reqVO.getCode())
                .eqIfPresent(ErpProductBatchDO::getRemark, reqVO.getRemark())
                .eqIfPresent(ErpProductBatchDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpProductBatchDO::getCreateTime, reqVO.getCreateTime())
                .ne(ErpProductBatchDO::getDeleted,1)
                .orderByDesc(ErpProductBatchDO::getId));
    }
}