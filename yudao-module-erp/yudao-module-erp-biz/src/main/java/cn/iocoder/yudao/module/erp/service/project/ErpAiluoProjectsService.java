package cn.iocoder.yudao.module.erp.service.project;

import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

public interface ErpAiluoProjectsService {
    List<ErpAiluoProjectDO> getActiveProjects();

    List<ErpAiluoProjectDO> getProjectList(Collection<Long> ids);

    default Map<Long, ErpAiluoProjectDO> getProjectMap(Collection<Long> ids) {
        return convertMap(getProjectList(ids), ErpAiluoProjectDO::getId);
    }
}
