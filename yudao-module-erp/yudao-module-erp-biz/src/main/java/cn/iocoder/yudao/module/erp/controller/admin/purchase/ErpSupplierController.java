package cn.iocoder.yudao.module.erp.controller.admin.purchase;

import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierListReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierSaveReqVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;
import cn.iocoder.yudao.module.erp.service.purchase.ErpSupplierService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "管理后台 - ERP 供应商")
@RestController
@RequestMapping("/erp/supplier")
@Validated
public class ErpSupplierController {

    @Resource
    private ErpSupplierService supplierService;

    @PostMapping("/create")
    @Operation(summary = "创建ERP 供应商")
    @PreAuthorize("@ss.hasPermission('erp:supplier:create')")
    public CommonResult<Long> createSupplier(@Valid @RequestBody ErpSupplierSaveReqVO createReqVO) {
        return success(supplierService.createSupplier(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新ERP 供应商")
    @PreAuthorize("@ss.hasPermission('erp:supplier:update')")
    public CommonResult<Boolean> updateSupplier(@Valid @RequestBody ErpSupplierSaveReqVO updateReqVO) {
        supplierService.updateSupplier(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除ERP 供应商")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:supplier:delete')")
    public CommonResult<Boolean> deleteSupplier(@RequestParam("id") Long id) {
        supplierService.deleteSupplier(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得ERP 供应商")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:supplier:query')")
    public CommonResult<ErpSupplierRespVO> getSupplier(@RequestParam("id") Long id) {
        ErpSupplierDO supplier = supplierService.getSupplier(id);
        return success(BeanUtils.toBean(supplier, ErpSupplierRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得ERP 供应商列表")
    @PreAuthorize("@ss.hasPermission('erp:supplier:query')")
    public CommonResult<List<ErpSupplierRespVO>> getSupplierList(@Valid ErpSupplierListReqVO listReqVO) {
//        if (listReqVO.getParentId() == null||listReqVO.getParentId().isEmpty()){
//            listReqVO.setParentId(String.valueOf(0));
//        }
        List<ErpSupplierDO> list = supplierService.getSupplierList(listReqVO);
        return success(BeanUtils.toBean(list, ErpSupplierRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出ERP 供应商 Excel")
    @PreAuthorize("@ss.hasPermission('erp:supplier:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSupplierExcel(@Valid ErpSupplierListReqVO listReqVO,
              HttpServletResponse response) throws IOException {
        List<ErpSupplierDO> list = supplierService.getSupplierList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "ERP 供应商.xls", "数据", ErpSupplierRespVO.class,
                        BeanUtils.toBean(list, ErpSupplierRespVO.class));
    }

}