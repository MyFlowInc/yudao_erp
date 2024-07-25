package cn.iocoder.yudao.module.erp.dal.mysql.project;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ErpAiluoProjectsMapper extends BaseMapperX<ErpAiluoProjectDO> {
    default List<ErpAiluoProjectDO> selectListByStatus(String status) {
        return selectList(ErpAiluoProjectDO::getStatus, status);
    }
}
