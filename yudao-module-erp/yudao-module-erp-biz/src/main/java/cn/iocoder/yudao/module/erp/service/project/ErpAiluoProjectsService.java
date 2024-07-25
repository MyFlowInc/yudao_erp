package cn.iocoder.yudao.module.erp.service.project;

import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;

import java.util.List;

public interface ErpAiluoProjectsService {
    List<ErpAiluoProjectDO> getActiveProjects();
}
