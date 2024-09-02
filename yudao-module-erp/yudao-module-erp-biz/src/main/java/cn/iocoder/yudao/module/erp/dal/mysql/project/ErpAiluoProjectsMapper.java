package cn.iocoder.yudao.module.erp.dal.mysql.project;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 15276
 */
@Mapper
@DS("ailuo")
public interface ErpAiluoProjectsMapper extends BaseMapperX<ErpAiluoProjectDO> {
    default List<ErpAiluoProjectDO> selectListByStatus(String status) {
        return selectList(ErpAiluoProjectDO::getStatus, status);
    }
}
