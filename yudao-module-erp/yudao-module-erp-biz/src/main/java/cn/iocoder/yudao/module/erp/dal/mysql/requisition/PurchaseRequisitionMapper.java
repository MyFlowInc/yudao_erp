package cn.iocoder.yudao.module.erp.dal.mysql.requisition;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;
import reactor.core.publisher.Sinks;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 新增请购 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface PurchaseRequisitionMapper extends BaseMapperX<PurchaseRequisitionDO> {

    default PageResult<PurchaseRequisitionDO> selectPage(PurchaseRequisitionPageReqVO reqVO) {
        MPJLambdaWrapperX<PurchaseRequisitionDO> query = new MPJLambdaWrapperX<PurchaseRequisitionDO>()
                .eqIfPresent(PurchaseRequisitionDO::getId, reqVO.getId())
                .eqIfPresent(PurchaseRequisitionDO::getRequisitionCode, reqVO.getRequisitionCode())
                .eqIfPresent(PurchaseRequisitionDO::getRequisitionType, reqVO.getRequisitionType())
                .eqIfPresent(PurchaseRequisitionDO::getAssociationProject,reqVO.getAssociationProject())
                .betweenIfPresent(PurchaseRequisitionDO::getRequisitionTime, reqVO.getRequisitionTime())
                .betweenIfPresent(PurchaseRequisitionDO::getEstimatedTime, reqVO.getEstimatedTime())
                .eqIfPresent(PurchaseRequisitionDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PurchaseRequisitionDO::getOpen, reqVO.getOpen())
                .betweenIfPresent(PurchaseRequisitionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PurchaseRequisitionDO::getId);
        if (reqVO.getProductId() != null) {
            query.leftJoin(RequisitionProductDO.class, RequisitionProductDO::getAssociationRequisition, PurchaseRequisitionDO::getId)
                    .eq(reqVO.getProductId() != null, RequisitionProductDO::getProductId, reqVO.getProductId())
                    // 避免 1 对多查询，产生相同的 1
                    .groupBy(PurchaseRequisitionDO::getId);
        }
        return selectPage(reqVO,query);
    }

    default List<PurchaseRequisitionDO> selectStatusIsNotEndList(PurchaseRequisitionPageReqVO reqVO) {
        LambdaQueryWrapper<PurchaseRequisitionDO> wrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(reqVO.getId())) {
            wrapper.eq(PurchaseRequisitionDO::getId, reqVO.getId());
        }
        // 添加状态不等于的条件
//        wrapper.ne(PurchaseRequisitionDO::getOpen, 1);
        wrapper.eq(PurchaseRequisitionDO::getStatus, 20);
        wrapper.ne(PurchaseRequisitionDO::getDeleted,true);
        wrapper.orderByDesc(PurchaseRequisitionDO::getId);
        return selectList(wrapper);
    }

    default PurchaseRequisitionDO selectByNo(String no) {
        return selectOne(PurchaseRequisitionDO::getRequisitionCode, no);
    }

    default int updateByIdAndStatus(Long id, Integer status, PurchaseRequisitionDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<PurchaseRequisitionDO>()
                .eq(PurchaseRequisitionDO::getId, id).eq(PurchaseRequisitionDO::getStatus, status));
    }

}