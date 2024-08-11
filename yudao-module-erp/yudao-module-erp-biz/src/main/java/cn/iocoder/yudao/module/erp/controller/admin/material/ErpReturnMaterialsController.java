package cn.iocoder.yudao.module.erp.controller.admin.material;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsSaveReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.productbatch.ErpProductBatchService;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import cn.iocoder.yudao.module.erp.service.requisition.PurchaseRequisitionService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
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

import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsItemDO;
import cn.iocoder.yudao.module.erp.service.material.ErpReturnMaterialsService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "管理后台 - ERP 还料入库单")
@RestController
@RequestMapping("/erp/return-materials")
@Validated
public class ErpReturnMaterialsController {

    @Resource
    private ErpReturnMaterialsService returnMaterialsService;
    @Resource
    private ErpProductService productService;
    @Resource
    private PurchaseRequisitionService purchaseRequisitionService;
    @Resource
    private ErpProductBatchService productBatchService;
    @Resource
    private ErpAiluoProjectsService ailuoProjectsService;
    @Resource
    private ErpStockService stockService;
    @PostMapping("/create")
    @Operation(summary = "创建ERP 还料入库单")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:create')")
    public CommonResult<Long> createReturnMaterials(@Valid @RequestBody ErpReturnMaterialsSaveReqVO createReqVO) {
        return success(returnMaterialsService.createReturnMaterials(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新ERP 还料入库单")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:update')")
    public CommonResult<Boolean> updateReturnMaterials(@Valid @RequestBody ErpReturnMaterialsSaveReqVO updateReqVO) {
        returnMaterialsService.updateReturnMaterials(updateReqVO);
        return success(true);
    }
    @PutMapping("/update-status")
    @Operation(summary = "更新请购单的状态")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:update-status')")
    public CommonResult<Boolean> updateReturnMaterialsStatus(@RequestParam("id") Long id,
                                                       @RequestParam("status") Integer status) {
        returnMaterialsService.updateReturnMaterialsStatus(id, status);
        return success(true);
    }
    @DeleteMapping("/delete")
    @Operation(summary = "删除ERP 还料入库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:return-materials:delete')")
    public CommonResult<Boolean> deleteReturnMaterials(@RequestParam("ids") List<Long> ids) {
        returnMaterialsService.deleteReturnMaterials(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得ERP 还料入库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:query')")
    public CommonResult<ErpReturnMaterialsRespVO> getReturnMaterials(@RequestParam("id") Long id) {
        ErpReturnMaterialsDO returnMaterials = returnMaterialsService.getReturnMaterials(id);
        if (returnMaterials == null) {
            return success(null);
        }
        List<ErpReturnMaterialsItemDO> returnMaterialsItemListByReturnId = returnMaterialsService.getReturnMaterialsItemListByReturnId(id);
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(returnMaterialsItemListByReturnId, ErpReturnMaterialsItemDO::getProductId));
        return success(BeanUtils.toBean(returnMaterials, ErpReturnMaterialsRespVO.class,item->{
             item.setItems(BeanUtils.toBean(returnMaterialsItemListByReturnId,ErpReturnMaterialsRespVO.Item.class,items->{
                 ErpStockDO stock = stockService.getStock(items.getProductId(), items.getWarehouseId());
                 items.setStockCount(stock != null ? stock.getCount() : BigDecimal.ZERO);
                 MapUtils.findAndThen(productMap, items.getProductId(), product -> items.setProductName(product.getName())
                         .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()));
                 if (items.getAssociatedBatchId()!= null){
                     ErpProductBatchDO productBatch = productBatchService.getProductBatch(items.getAssociatedBatchId());
                     items.setAssociatedBatchName(productBatch.getName());
                     items.setAssociationBatchNum(productBatch.getInventoryQuantity().intValue());
                 }
             }));
        }));
    }

    @GetMapping("/page")
    @Operation(summary = "获得ERP 还料入库单分页")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:query')")
    public CommonResult<PageResult<ErpReturnMaterialsRespVO>> getReturnMaterialsPage(@Valid ErpReturnMaterialsPageReqVO pageReqVO) {
        PageResult<ErpReturnMaterialsDO> pageResult = returnMaterialsService.getReturnMaterialsPage(pageReqVO);
        // 1.1 项目列表
        Map<Long, ErpAiluoProjectDO> projectMap = ailuoProjectsService.getProjectMap(
                convertSet(pageResult.getList(), ErpReturnMaterialsDO::getAssociationProjectId));
        Map<Long, PurchaseRequisitionDO> purchaseRequisitionMap =
                purchaseRequisitionService.getPurchaseRequisitionMap
                        (convertSet(pageResult.getList(), ErpReturnMaterialsDO::getAssociationRequisitionId));
        return success(BeanUtils.toBean(pageResult, ErpReturnMaterialsRespVO.class,item->{
            MapUtils.findAndThen(purchaseRequisitionMap, item.getAssociationRequisitionId(), requisitionDO -> item.setAssociationRequisitionNo(requisitionDO.getRequisitionCode()));
            MapUtils.findAndThen(projectMap, item.getAssociationProjectId(), project -> item.setAssociationProjectName(project.getName()));
        }));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出ERP 还料入库单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportReturnMaterialsExcel(@Valid ErpReturnMaterialsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpReturnMaterialsDO> list = returnMaterialsService.getReturnMaterialsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "ERP 还料入库单.xls", "数据", ErpReturnMaterialsRespVO.class,
                        BeanUtils.toBean(list, ErpReturnMaterialsRespVO.class));
    }

    // ==================== 子表（ERP 还料入库单项） ====================

    @GetMapping("/return-materials-item/list-by-return-id")
    @Operation(summary = "获得ERP 还料入库单项列表")
    @Parameter(name = "returnId", description = "还料单编号")
    @PreAuthorize("@ss.hasPermission('erp:return-materials:query')")
    public CommonResult<List<ErpReturnMaterialsItemDO>> getReturnMaterialsItemListByReturnId(@RequestParam("returnId") Long returnId) {
        return success(returnMaterialsService.getReturnMaterialsItemListByReturnId(returnId));
    }

}