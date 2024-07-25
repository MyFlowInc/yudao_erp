package cn.iocoder.yudao.module.erp.service.project;

import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.mysql.project.ErpAiluoProjectsMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
@Service
@Validated
public class ErpAiluoProjectsServiceImpl implements ErpAiluoProjectsService{
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private ErpAiluoProjectsMapper erpAiluoProjectsMapper;

    @Override
    @DS("ailuo")
    public List<ErpAiluoProjectDO> getActiveProjects() {
        return erpAiluoProjectsMapper.selectListByStatus("ended");
    }
}
