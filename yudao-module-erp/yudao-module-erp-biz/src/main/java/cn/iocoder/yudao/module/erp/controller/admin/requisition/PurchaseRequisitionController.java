package cn.iocoder.yudao.module.erp.controller.admin.requisition;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.requisition.JDBCUtil.JDBCConfig;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import javax.servlet.http.*;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import static com.alibaba.druid.util.JdbcUtils.executeQuery;

import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.module.erp.service.requisition.PurchaseRequisitionService;

@Tag(name = "管理后台 - ERP 请购单")
@RestController
@RequestMapping("/erp/purchase-requisition")
@Validated
public class PurchaseRequisitionController {
    @Resource
    private PurchaseRequisitionService purchaseRequisitionService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpStockService stockService;

    @PostMapping("/create")
    @Operation(summary = "创建新增请购")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:create')")
    public CommonResult<Long> createPurchaseRequisition(@Valid @RequestBody PurchaseRequisitionSaveReqVO createReqVO) {
        return success(purchaseRequisitionService.createPurchaseRequisition(createReqVO));
    }
    @PutMapping("/update")
    @Operation(summary = "更新新增请购")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:update')")
    public CommonResult<Boolean> updatePurchaseRequisition(@Valid @RequestBody PurchaseRequisitionSaveReqVO updateReqVO) {
        purchaseRequisitionService.updatePurchaseRequisition(updateReqVO);
        return success(true);
    }
    @DeleteMapping("/delete")
    @Operation(summary = "删除新增请购")
    @Parameter(name = "ids", description = "编号数组", required = true)
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:delete')")
    public CommonResult<Boolean> deletePurchaseRequisition(@RequestParam("ids") List<Long> ids) {
        purchaseRequisitionService.deletePurchaseRequisition(ids);
        return success(true);
    }
    @GetMapping("/get")
    @Operation(summary = "获得新增请购")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:query')")
    public CommonResult<PurchaseRequisitionRespVO> getPurchaseRequisition(@RequestParam("id") Long id) {
        PurchaseRequisitionDO purchaseRequisition = purchaseRequisitionService.getPurchaseRequisition(id);
        if (purchaseRequisition == null) {
            return success(null);
        }
        List<RequisitionProductDO> requisitionProductItemList = purchaseRequisitionService.getRequisitionProductListByOrderId(id);
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(requisitionProductItemList, RequisitionProductDO::getProductId));
        return success(BeanUtils.toBean(purchaseRequisition, PurchaseRequisitionRespVO.class, purchaseRequisitionVO ->
                purchaseRequisitionVO.setItems(BeanUtils.toBean(requisitionProductItemList, PurchaseRequisitionRespVO.Item.class, item -> {
                    BigDecimal purchaseCount = stockService.getStockCount(item.getProductId());
                    item.setStockCount(purchaseCount != null ? purchaseCount : BigDecimal.ZERO);
                    MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()));
                }))));
    }
    @PutMapping("/update-status")
    @Operation(summary = "更新请购单的状态")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:update-status')")
    public CommonResult<Boolean> updatePurchaseRequisitionStatus(@RequestParam("id") Long id,
                                                                 @RequestParam("status") Integer status) {
        purchaseRequisitionService.updatePurchaseRequisitionStatus(id, status);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得新增请购分页")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:query')")
    public CommonResult<PageResult<PurchaseRequisitionRespVO>> getPurchaseRequisitionPage(@Valid PurchaseRequisitionPageReqVO pageReqVO) {
        PageResult<PurchaseRequisitionDO> pageResult = purchaseRequisitionService.getPurchaseRequisitionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchaseRequisitionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出新增请购 Excel")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurchaseRequisitionExcel(@Valid PurchaseRequisitionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchaseRequisitionDO> list = purchaseRequisitionService.getPurchaseRequisitionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "新增请购.xls", "数据", PurchaseRequisitionRespVO.class,
                        BeanUtils.toBean(list, PurchaseRequisitionRespVO.class));
    }


    @GetMapping("/project")
    @Operation(summary = "获得ailuo项目信息")
    public CommonResult<List<ErpAiluoSplProjectVO>> getErpSplProjectList() {
        List<ErpAiluoSplProjectVO> erpAiluoSplProjects = null;
        try {
            String sql = "SELECT * FROM spl_project WHERE status = ?";
            erpAiluoSplProjects = JDBCConfig.executeQuery(sql, "ended");
            // 处理结果集
        } catch (SQLException e) {
            // 处理异常
            e.printStackTrace();
        }
        return success(erpAiluoSplProjects);
    }

//    // ==================== 子表（请购产品） ====================
//
//    @GetMapping("/requisition-product/page")
//    @Operation(summary = "获得请购产品分页")
//    @Parameter(name = "associationRequisition", description = "关联请购单")
//    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:query')")
//    public CommonResult<PageResult<RequisitionProductDO>> getRequisitionProductPage(PageParam pageReqVO,
//                                                                                        @RequestParam("associationRequisition") String associationRequisition) {
//        return success(purchaseRequisitionService.getRequisitionProductPage(pageReqVO, associationRequisition));
//    }
//
//    @PostMapping("/requisition-product/create")
//    @Operation(summary = "创建请购产品")
//    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:create')")
//    public CommonResult<Long> createRequisitionProduct(@Valid @RequestBody RequisitionProductDO requisitionProduct) {
//        return success(purchaseRequisitionService.createRequisitionProduct(requisitionProduct));
//    }
//
//    @PutMapping("/requisition-product/update")
//    @Operation(summary = "更新请购产品")
//    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:update')")
//    public CommonResult<Boolean> updateRequisitionProduct(@Valid @RequestBody RequisitionProductDO requisitionProduct) {
//        purchaseRequisitionService.updateRequisitionProduct(requisitionProduct);
//        return success(true);
//    }
//
//    @DeleteMapping("/requisition-product/delete")
//    @Parameter(name = "id", description = "编号", required = true)
//    @Operation(summary = "删除请购产品")
//    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:delete')")
//    public CommonResult<Boolean> deleteRequisitionProduct(@RequestParam("id") Long id) {
//        purchaseRequisitionService.deleteRequisitionProduct(id);
//        return success(true);
//    }

}