package cn.iocoder.yudao.module.erp.service.project;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.mysql.project.ErpAiluoProjectsMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Service
@Validated
@DS("ailuo")
public class ErpAiluoProjectsServiceImpl implements ErpAiluoProjectsService{
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private ErpAiluoProjectsMapper erpAiluoProjectsMapper;

    @Override
    public List<ErpAiluoProjectDO> getActiveProjects() {
        return erpAiluoProjectsMapper.selectListByStatus("ended");
    }

    @Override
    public List<ErpAiluoProjectDO> getProjectList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return erpAiluoProjectsMapper.selectBatchIds(ids);
    }
    @Override
    public Map<Long, ErpAiluoProjectDO> getProjectMap(Collection<Long> ids) {
        return ErpAiluoProjectsService.super.getProjectMap(ids);
    }
}
