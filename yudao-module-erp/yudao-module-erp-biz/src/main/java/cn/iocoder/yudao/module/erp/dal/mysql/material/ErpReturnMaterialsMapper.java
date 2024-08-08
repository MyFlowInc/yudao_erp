package cn.iocoder.yudao.module.erp.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
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
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpReturnMaterialsDO>()
                .eqIfPresent(ErpReturnMaterialsDO::getId, reqVO.getId())
                .eqIfPresent(ErpReturnMaterialsDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpReturnMaterialsDO::getSupplierId, reqVO.getSupplierId())
                .eqIfPresent(ErpReturnMaterialsDO::getAssociationRequisitionId, reqVO.getAssociationRequisitionId())
                .eqIfPresent(ErpReturnMaterialsDO::getAssociationProjectId, reqVO.getAssociationProjectId())
                .betweenIfPresent(ErpReturnMaterialsDO::getInTime, reqVO.getInTime())
                .eqIfPresent(ErpReturnMaterialsDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpReturnMaterialsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpReturnMaterialsDO::getId));
    }
    default int updateReturnMaterialsStatus(Long id, Integer status, ErpReturnMaterialsDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpReturnMaterialsDO>()
                .eq(ErpReturnMaterialsDO::getId, id).eq(ErpReturnMaterialsDO::getStatus, status));
    }

    default ErpReturnMaterialsDO selectByNo(String no) {
        return selectOne(ErpReturnMaterialsDO::getNo, no);
    }

}