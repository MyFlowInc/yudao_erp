package cn.iocoder.yudao.module.erp.controller.admin.productbatch;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

import cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.service.productbatch.ErpProductBatchService;

@Tag(name = "管理后台 - ERP产品批次信息")
@RestController
@RequestMapping("/erp/product-batch")
@Validated
public class ErpProductBatchController {

    @Resource
    private ErpProductBatchService productBatchService;

    @Resource
    private ErpProductService productService;
    @PostMapping("/create")
    @Operation(summary = "创建ERP产品批次信息")
    @PreAuthorize("@ss.hasPermission('erp:product-batch:create')")
    public CommonResult<Long> createProductBatch(@Valid @RequestBody ErpProductBatchSaveReqVO createReqVO) {
        return success(productBatchService.createProductBatch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新ERP产品批次信息")
    @PreAuthorize("@ss.hasPermission('erp:product-batch:update')")
    public CommonResult<Boolean> updateProductBatch(@Valid @RequestBody ErpProductBatchSaveReqVO updateReqVO) {
        productBatchService.updateProductBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除ERP产品批次信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:product-batch:delete')")
    public CommonResult<Boolean> deleteProductBatch(@RequestParam("id") Long id) {
        productBatchService.deleteProductBatch(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得ERP产品批次信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:product-batch:query')")
    public CommonResult<ErpProductBatchRespVO> getProductBatch(@RequestParam("id") Long id) {
        ErpProductBatchDO productBatch = productBatchService.getProductBatch(id);
        return success(BeanUtils.toBean(productBatch, ErpProductBatchRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得ERP产品批次信息分页")
    @PreAuthorize("@ss.hasPermission('erp:product-batch:query')")
    public CommonResult<PageResult<ErpProductBatchRespVO>> getProductBatchPage(@Valid ErpProductBatchPageReqVO pageReqVO) {
        PageResult<ErpProductBatchDO> pageResult = productBatchService.getProductBatchPage(pageReqVO);
        // 1.1 项目列表
        Map<Long, ErpProductRespVO> productList = productService.getProductVOMap(
                convertSet(pageResult.getList(), products -> Long.parseLong(String.valueOf(products.getAssociationProductId()))));
        return success(BeanUtils.toBean(pageResult, ErpProductBatchRespVO.class,productMap ->{
            MapUtils.findAndThen(productList,Long.valueOf(productMap.getAssociationProductId()),product -> productMap.setProductName(product.getName()));}));
    }
    @GetMapping("/export-excel")
    @Operation(summary = "导出ERP产品批次信息 Excel")
    @PreAuthorize("@ss.hasPermission('erp:product-batch:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductBatchExcel(@Valid ErpProductBatchPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpProductBatchDO> list = productBatchService.getProductBatchPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "ERP产品批次信息.xls", "数据", ErpProductBatchRespVO.class,
                        BeanUtils.toBean(list, ErpProductBatchRespVO.class));
    }

}