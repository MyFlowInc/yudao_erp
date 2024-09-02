package cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurItemDO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 15276
 */
@Mapper
@DS("ailuo")
public interface ErpPurItemMapper extends BaseMapperX<ErpPurItemDO> {
    default List<ErpPurItemDO> selectErpPurItemList(ErpPurItemDO erpPurItem) {
        return selectList(ErpPurItemDO::getType, erpPurItem.getType());
    }
}
