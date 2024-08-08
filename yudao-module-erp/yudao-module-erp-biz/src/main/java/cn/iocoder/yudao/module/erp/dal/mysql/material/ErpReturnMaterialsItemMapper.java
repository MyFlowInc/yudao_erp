package cn.iocoder.yudao.module.erp.dal.mysql.material;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.returnmaterialsitem.ErpReturnMaterialsItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 还料入库单项 Mapper
 *
 * @author 那就这样吧
 */
@Mapper
public interface ErpReturnMaterialsItemMapper extends BaseMapperX<ErpReturnMaterialsItemDO> {

    default List<ErpReturnMaterialsItemDO> selectListByReturnId(Long returnId) {
        return selectList(ErpReturnMaterialsItemDO::getReturnId, returnId);
    }

    default void deleteByReturnId(Long returnId) {
        delete(ErpReturnMaterialsItemDO::getReturnId, returnId);
    }
}