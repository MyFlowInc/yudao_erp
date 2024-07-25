package cn.iocoder.yudao.module.erp.controller.admin.project;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.project.vo.ErpAiluoSplProjectVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - ERP 艾罗项目")
@RestController
@RequestMapping("/erp/ailuo-projects")
@Validated
public class ErpAiluoProjectsController {
    @Resource
    private ErpAiluoProjectsService projectsService;

    @GetMapping("/simple-list")
    @Operation(summary = "获得ailuo项目信息")
    public CommonResult<List<ErpAiluoSplProjectVO>> getErpSplProjectList() {
        List<ErpAiluoProjectDO> ret = projectsService.getActiveProjects();
        return success(BeanUtils.toBean(ret, ErpAiluoSplProjectVO.class));
    }
}
