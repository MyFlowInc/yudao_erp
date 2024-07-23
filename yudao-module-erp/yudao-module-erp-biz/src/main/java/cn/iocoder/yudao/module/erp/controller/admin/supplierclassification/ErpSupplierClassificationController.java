package cn.iocoder.yudao.module.erp.controller.admin.supplierclassification;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification.ErpSupplierClassificationDO;
import cn.iocoder.yudao.module.erp.service.supplierclassification.ErpSupplierClassificationService;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "管理后台 - 供应商分类")
@RestController
@RequestMapping("/erp/supplier-classification")
@Validated
public class ErpSupplierClassificationController {

    @Resource
    private ErpSupplierClassificationService supplierClassificationService;

    @PostMapping("/create")
    @Operation(summary = "创建供应商分类")
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:create')")
    public CommonResult<Long> createSupplierClassification(@Valid @RequestBody ErpSupplierClassificationSaveReqVO createReqVO) {
        return success(supplierClassificationService.createSupplierClassification(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新供应商分类")
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:update')")
    public CommonResult<Boolean> updateSupplierClassification(@Valid @RequestBody ErpSupplierClassificationSaveReqVO updateReqVO) {
        supplierClassificationService.updateSupplierClassification(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除供应商分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:delete')")
    public CommonResult<Boolean> deleteSupplierClassification(@RequestParam("id") Long id) {
        supplierClassificationService.deleteSupplierClassification(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得供应商分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:query')")
    public CommonResult<ErpSupplierClassificationRespVO> getSupplierClassification(@RequestParam("id") Long id) {
        ErpSupplierClassificationDO supplierClassification = supplierClassificationService.getSupplierClassification(id);
        return success(BeanUtils.toBean(supplierClassification, ErpSupplierClassificationRespVO.class));
    }

    @PermitAll
    @GetMapping("/list")
    @Operation(summary = "获得供应商分类列表")
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:query')")
    public CommonResult<List<ErpSupplierClassificationRespVO>> getSupplierClassificationList(@Valid ErpSupplierClassificationListReqVO listReqVO) {
        List<ErpSupplierClassificationDO> list = supplierClassificationService.getSupplierClassificationList(listReqVO);
        return success(BeanUtils.toBean(list, ErpSupplierClassificationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出供应商分类 Excel")
    @PreAuthorize("@ss.hasPermission('erp:supplier-classification:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSupplierClassificationExcel(@Valid ErpSupplierClassificationListReqVO listReqVO,
              HttpServletResponse response) throws IOException {
        List<ErpSupplierClassificationDO> list = supplierClassificationService.getSupplierClassificationList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "供应商分类.xls", "数据", ErpSupplierClassificationRespVO.class,
                        BeanUtils.toBean(list, ErpSupplierClassificationRespVO.class));
    }

}