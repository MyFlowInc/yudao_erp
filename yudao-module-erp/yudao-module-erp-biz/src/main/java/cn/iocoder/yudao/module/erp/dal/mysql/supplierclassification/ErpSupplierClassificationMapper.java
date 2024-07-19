package cn.iocoder.yudao.module.erp.dal.mysql.supplierclassification;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification.ErpSupplierClassificationDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo.*;

/**
 * 供应商分类 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpSupplierClassificationMapper extends BaseMapperX<ErpSupplierClassificationDO> {

    default List<ErpSupplierClassificationDO> selectList(ErpSupplierClassificationListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ErpSupplierClassificationDO>()
                .eqIfPresent(ErpSupplierClassificationDO::getId, reqVO.getId())
                .eqIfPresent(ErpSupplierClassificationDO::getParentId, reqVO.getParentId())
                .likeIfPresent(ErpSupplierClassificationDO::getName, reqVO.getName())
                .eqIfPresent(ErpSupplierClassificationDO::getCode, reqVO.getCode())
                .eqIfPresent(ErpSupplierClassificationDO::getSort, reqVO.getSort())
                .eqIfPresent(ErpSupplierClassificationDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpSupplierClassificationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpSupplierClassificationDO::getId));
    }

	default ErpSupplierClassificationDO selectByParentIdAndName(Long parentId, String name) {
	    return selectOne(ErpSupplierClassificationDO::getParentId, parentId, ErpSupplierClassificationDO::getName, name);
	}

    default Long selectCountByParentId(Long parentId) {
        return selectCount(ErpSupplierClassificationDO::getParentId, parentId);
    }

}