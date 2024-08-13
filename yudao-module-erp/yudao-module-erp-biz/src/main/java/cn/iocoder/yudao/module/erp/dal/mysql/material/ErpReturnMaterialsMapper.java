package cn.iocoder.yudao.module.erp.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 还料入库单 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpReturnMaterialsMapper extends BaseMapperX<ErpReturnMaterialsDO> {

    default PageResult<ErpReturnMaterialsDO> selectPage(ErpReturnMaterialsPageReqVO reqVO) {
        MPJLambdaWrapperX<ErpReturnMaterialsDO> query = new MPJLambdaWrapperX<ErpReturnMaterialsDO>()
                .eqIfPresent(ErpReturnMaterialsDO::getId, reqVO.getId())
                .eqIfPresent(ErpReturnMaterialsDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpReturnMaterialsDO::getCreator, reqVO.getCreatorName())
                .eqIfPresent(ErpReturnMaterialsDO::getAssociationRequisitionId, reqVO.getAssociationRequisitionId())
                .eqIfPresent(ErpReturnMaterialsDO::getAssociationProjectId, reqVO.getAssociationProjectId())
                .betweenIfPresent(ErpReturnMaterialsDO::getInTime, reqVO.getInTime())
                .eqIfPresent(ErpReturnMaterialsDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpReturnMaterialsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpReturnMaterialsDO::getId);
        if (reqVO.getProductId() != null) {
            query.leftJoin(ErpReturnMaterialsItemDO.class, ErpReturnMaterialsItemDO::getReturnId, ErpPickingInDO::getId)
                    .eq(reqVO.getProductId() != null, ErpReturnMaterialsItemDO::getProductId, reqVO.getProductId())
                    // 避免 1 对多查询，产生相同的 1
                    .groupBy(ErpReturnMaterialsDO::getId);
        }
        return selectPage(reqVO, query);
        
    }


    default int updateReturnMaterialsStatus(Long id, Integer status, ErpReturnMaterialsDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpReturnMaterialsDO>()
                .eq(ErpReturnMaterialsDO::getId, id).eq(ErpReturnMaterialsDO::getStatus, status));
    }

    default ErpReturnMaterialsDO selectByNo(String no) {
        return selectOne(ErpReturnMaterialsDO::getNo, no);
    }

}