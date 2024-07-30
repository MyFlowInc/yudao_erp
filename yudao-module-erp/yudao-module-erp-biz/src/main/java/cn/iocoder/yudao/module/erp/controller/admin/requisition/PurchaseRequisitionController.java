package cn.iocoder.yudao.module.erp.controller.admin.requisition;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.order.ErpPurchaseOrderRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseOrderItemDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import cn.iocoder.yudao.module.erp.service.purchase.ErpPurchaseOrderService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.apache.ibatis.annotations.One;
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
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.fhs.common.constant.Constant.*;
import static org.apache.commons.lang3.BooleanUtils.NO;

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
    private ErpPurchaseOrderService purchaseOrderService;
    @Resource
    private PurchaseRequisitionService purchaseRequisitionService;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpStockService stockService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ErpAiluoProjectsService ailuoProjectsService;


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
                    List<ErpPurchaseOrderItemDO> erpPurchaseOrderItemDOS = purchaseOrderService.selectItemList(item.getId());
                    if (erpPurchaseOrderItemDOS != null){
                        // 分组：相同 AssociatedRequisitionProductId 的放在一组，唯一的放在另一组
                        Map<Long, List<ErpPurchaseOrderItemDO>> groupedByProductId = erpPurchaseOrderItemDOS.stream()
                                .collect(Collectors.groupingBy(ErpPurchaseOrderItemDO::getAssociatedRequisitionProductId));
                        // 分组后的结果
                        List<List<ErpPurchaseOrderItemDO>> duplicateGroups = new ArrayList<>();
                        List<ErpPurchaseOrderItemDO> uniqueItems = new ArrayList<>();
                        for (Map.Entry<Long, List<ErpPurchaseOrderItemDO>> entry : groupedByProductId.entrySet()) {
                            List<ErpPurchaseOrderItemDO> group = entry.getValue();
                            if (group.size() > 1) {
                                //重复组
                                duplicateGroups.add(group);
                            } else {
                                //唯一组
                                uniqueItems.add(group.get(0));
                            }
                        }
                        if (!duplicateGroups.isEmpty()){
                            // 计算每个分组中的 count 总和，将结果存储在 Map<Long, BigDecimal> 中
                            Map<Long, BigDecimal> sumByProductId = new HashMap<>();
                            groupedByProductId.forEach((productId, items) -> {
                                BigDecimal sum = items.stream()
                                        // 获取 BigDecimal 类型的值
                                        .map(ErpPurchaseOrderItemDO::getCount)
                                        // 使用 reduce 方法进行求和
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                sumByProductId.put(productId, sum);
                            });;
                            item.setPurchasedNum(sumByProductId.get(item.getId()));
                        };
                        //唯一组
                        if (!uniqueItems.isEmpty()){
                            uniqueItems.forEach( o->{
                                if (Objects.equals(o.getAssociatedRequisitionProductId(), item.getId())){
                                    item.setPurchasedNum(o.getCount());
                                }
                            });
                        }
                    }
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
        return success(buildPurchaseRequisitionVOPageResult(pageResult));
    }
    @GetMapping("/list")
    @Operation(summary = "获得新增请购分页")
    @PreAuthorize("@ss.hasPermission('erp:purchase-requisition:query')")
    public CommonResult<List<PurchaseRequisitionRespVO>> getPurchaseRequisitionListAndProductList(@Valid PurchaseRequisitionPageReqVO pageReqVO) {
        List<PurchaseRequisitionDO> purchaseRequisitionDOS = purchaseRequisitionService.selectStatusIsNotEndList(pageReqVO);
        List<PurchaseRequisitionRespVO> result = purchaseRequisitionDOS.stream()
                .map(o -> {
                    List<RequisitionProductDO> requisitionProductItemList =
                            purchaseRequisitionService.getRequisitionProductListByOrderId(o.getId());
                    List<RequisitionProductDO> filteredList = requisitionProductItemList.stream()
                            .filter(item -> ZREO == item.getStatus())
                            .collect(Collectors.toList());
                    Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                            convertSet(filteredList, RequisitionProductDO::getProductId));
                    return BeanUtils.toBean(o, PurchaseRequisitionRespVO.class,
                        purchaseRequisitionRespVO -> {
                        purchaseRequisitionRespVO.setItems(
                                BeanUtils.toBean(filteredList, PurchaseRequisitionRespVO.Item.class, item -> {
                                    MapUtils.findAndThen(productMap, item.getProductId(), product -> item.setProductName(product.getName())
                                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()));
                                }));
                    });
                })
                .collect(Collectors.toList());
        return success(result);
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

    private PageResult<PurchaseRequisitionRespVO> buildPurchaseRequisitionVOPageResult(PageResult<PurchaseRequisitionDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 1.1 项目列表
        Map<Long, ErpAiluoProjectDO> projectMap = ailuoProjectsService.getProjectMap(
                convertSet(pageResult.getList(), purchaseRequisition -> purchaseRequisition.getAssociationProject()));
        // 1.2 管理员信息
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), purchaseRequisition -> Long.parseLong(purchaseRequisition.getCreator())));
        // 2. 开始拼接
        return BeanUtils.toBean(pageResult, PurchaseRequisitionRespVO.class, purchaseRequisition -> {
            List<RequisitionProductDO> productItemList = purchaseRequisitionService.getRequisitionProductListByOrderId(purchaseRequisition.getId());
            List<String> productNames = CollectionUtils.convertList(productItemList, item -> productService.getProduct(item.getProductId()).getName());
            purchaseRequisition.setProductName(String.join("，", productNames));

            MapUtils.findAndThen(projectMap, purchaseRequisition.getAssociationProject(), project -> purchaseRequisition.setProjectName(project.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(purchaseRequisition.getCreator()), user -> purchaseRequisition.setCreatorName(user.getNickname()));
        });
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