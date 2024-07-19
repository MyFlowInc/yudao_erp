package cn.iocoder.yudao.module.erp.dal.mysql.requisition;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;

/**
 * 新增请购 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface PurchaseRequisitionMapper extends BaseMapperX<PurchaseRequisitionDO> {

    default PageResult<PurchaseRequisitionDO> selectPage(PurchaseRequisitionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchaseRequisitionDO>()
                .eqIfPresent(PurchaseRequisitionDO::getId, reqVO.getId())
                .eqIfPresent(PurchaseRequisitionDO::getRequisitionCode, reqVO.getRequisitionCode())
                .eqIfPresent(PurchaseRequisitionDO::getRequisitionType, reqVO.getRequisitionType())
                .betweenIfPresent(PurchaseRequisitionDO::getRequisitionTime, reqVO.getRequisitionTime())
                .betweenIfPresent(PurchaseRequisitionDO::getEstimatedTime, reqVO.getEstimatedTime())
                .eqIfPresent(PurchaseRequisitionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PurchaseRequisitionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PurchaseRequisitionDO::getId));
    }

    default PurchaseRequisitionDO selectByNo(String no) {
        return selectOne(PurchaseRequisitionDO::getRequisitionCode, no);
    }

    default int updateByIdAndStatus(Long id, Integer status, PurchaseRequisitionDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<PurchaseRequisitionDO>()
                .eq(PurchaseRequisitionDO::getId, id).eq(PurchaseRequisitionDO::getStatus, status));
    }

}