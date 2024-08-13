package cn.iocoder.yudao.module.erp.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.PurchaseRequisitionPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 领料入库单 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpPickingInMapper extends BaseMapperX<ErpPickingInDO> {

    default PageResult<ErpPickingInDO> selectPage(ErpPickingInPageReqVO reqVO) {
        MPJLambdaWrapperX<ErpPickingInDO> query = new MPJLambdaWrapperX<ErpPickingInDO>()
                .eqIfPresent(ErpPickingInDO::getId, reqVO.getId())
                .eqIfPresent(ErpPickingInDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpPickingInDO::getCreator, reqVO.getCreatorName())
                .eqIfPresent(ErpPickingInDO::getAssociationRequisitionId, reqVO.getAssociationRequisitionId())
                .eqIfPresent(ErpPickingInDO::getAssociationProjectId, reqVO.getAssociationProjectId())
                .betweenIfPresent(ErpPickingInDO::getInTime, reqVO.getInTime())
                .eqIfPresent(ErpPickingInDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpPickingInDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpPickingInDO::getId);
        if (reqVO.getProductId() != null) {
            query.leftJoin(ErpPickingInItemDO.class, ErpPickingInItemDO::getInId, ErpPickingInDO::getId)
                    .eq(reqVO.getProductId() != null, ErpPickingInItemDO::getProductId, reqVO.getProductId())
                    // 避免 1 对多查询，产生相同的 1
                    .groupBy(ErpPickingInDO::getId);
        }
        return selectPage(reqVO,query);

    }


    default int updateByIdAndStatus(Long id, Integer status, ErpPickingInDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpPickingInDO>()
                .eq(ErpPickingInDO::getId, id).eq(ErpPickingInDO::getStatus, status));
    }

    default ErpPickingInDO selectByNo(String no) {
        return selectOne(ErpPickingInDO::getNo, no);
    }
}