package cn.iocoder.yudao.module.erp.dal.mysql.purchase;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 供应商 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpSupplierMapper extends BaseMapperX<ErpSupplierDO> {

    default PageResult<ErpSupplierDO> selectPage(ErpSupplierPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpSupplierDO>()
                .eqIfPresent(ErpSupplierDO::getId, reqVO.getId())
                .likeIfPresent(ErpSupplierDO::getName, reqVO.getName())
                .eqIfPresent(ErpSupplierDO::getSupplierClassification, reqVO.getSupplierClassification())
                .eqIfPresent(ErpSupplierDO::getParentId, reqVO.getParentId())
                .eqIfPresent(ErpSupplierDO::getGrade, reqVO.getGrade())
                .eqIfPresent(ErpSupplierDO::getContact, reqVO.getContact())
                .eqIfPresent(ErpSupplierDO::getMobile, reqVO.getMobile())
                .eqIfPresent(ErpSupplierDO::getTelephone, reqVO.getTelephone())
                .eqIfPresent(ErpSupplierDO::getEmail, reqVO.getEmail())
                .eqIfPresent(ErpSupplierDO::getFax, reqVO.getFax())
                .eqIfPresent(ErpSupplierDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ErpSupplierDO::getSort, reqVO.getSort())
                .eqIfPresent(ErpSupplierDO::getTaxNo, reqVO.getTaxNo())
                .eqIfPresent(ErpSupplierDO::getTaxPercent, reqVO.getTaxPercent())
                .likeIfPresent(ErpSupplierDO::getBankName, reqVO.getBankName())
                .eqIfPresent(ErpSupplierDO::getBankAccount, reqVO.getBankAccount())
                .eqIfPresent(ErpSupplierDO::getBankAddress, reqVO.getBankAddress())
                .betweenIfPresent(ErpSupplierDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpSupplierDO::getId));
    }

    default List<ErpSupplierDO> selectListByStatus(Integer status) {
        return selectList(ErpSupplierDO::getStatus, status);
    }

}