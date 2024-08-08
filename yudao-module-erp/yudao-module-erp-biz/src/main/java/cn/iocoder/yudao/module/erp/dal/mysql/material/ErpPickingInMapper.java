package cn.iocoder.yudao.module.erp.dal.mysql.material;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
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
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpPickingInDO>()
                .eqIfPresent(ErpPickingInDO::getId, reqVO.getId())
                .eqIfPresent(ErpPickingInDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpPickingInDO::getSupplierId, reqVO.getSupplierId())
                .eqIfPresent(ErpPickingInDO::getAssociationRequisitionId, reqVO.getAssociationRequisitionId())
                .betweenIfPresent(ErpPickingInDO::getInTime, reqVO.getInTime())
                .eqIfPresent(ErpPickingInDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpPickingInDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ErpPickingInDO::getId));
    }
    default int updateByIdAndStatus(Long id, Integer status, ErpPickingInDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpPickingInDO>()
                .eq(ErpPickingInDO::getId, id).eq(ErpPickingInDO::getStatus, status));
    }

    default ErpPickingInDO selectByNo(String no) {
        return selectOne(ErpPickingInDO::getNo, no);
    }
}