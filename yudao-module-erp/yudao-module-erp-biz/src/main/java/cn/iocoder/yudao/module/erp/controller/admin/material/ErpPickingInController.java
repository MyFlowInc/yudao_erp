package cn.iocoder.yudao.module.erp.controller.admin.material;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInSaveReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.product.vo.product.ErpProductRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.in.ErpPurchaseInRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.stock.vo.out.ErpStockOutRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.PurchaseRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockOutItemDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.productbatch.ErpProductBatchService;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import cn.iocoder.yudao.module.erp.service.requisition.PurchaseRequisitionService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.service.material.ErpPickingInService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 15276
 */
@Tag(name = "管理后台 - ERP 领料出库单")
@RestController
@RequestMapping("/erp/picking-in")
@Validated
public class ErpPickingInController {

    @Resource
    private ErpPickingInService pickingInService;
    @Resource
    private ErpProductService productService;
    @Resource
    private PurchaseRequisitionService purchaseRequisitionService;
    @Resource
    private ErpAiluoProjectsService ailuoProjectsService;
    @Resource
    private ErpProductBatchService productBatchService;
    @Resource
    private ErpStockService stockService;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建ERP 领料出库单")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:create')")
    public CommonResult<Long> createPickingIn(@Valid @RequestBody ErpPickingInSaveReqVO createReqVO) {
        return success(pickingInService.createPickingIn(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新ERP 领料出库单")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:update')")
    public CommonResult<Boolean> updatePickingIn(@Valid @RequestBody ErpPickingInSaveReqVO updateReqVO) {
        pickingInService.updatePickingIn(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新请购单的状态")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:update-status')")
    public CommonResult<Boolean> updatePickingInStatus(@RequestParam("id") Long id,
                                                                 @RequestParam("status") Integer status) {
        pickingInService.updatePickingInStatus(id, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除ERP 领料出库单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:picking-in:delete')")
    public CommonResult<Boolean> deletePickingIn(@RequestParam("ids") List<String> ids) {
        pickingInService.deletePickingIn(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得ERP 领料出库单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:query')")
    public CommonResult<ErpPickingInRespVO> getPickingIn(@RequestParam("id") Long id) {
        ErpPickingInDO pickingIn = pickingInService.getPickingIn(id);
        if (pickingIn == null) {
            return success(null);
        }
        List<ErpPickingInItemDO> pickingInItemDOList = pickingInService.getPickingInItemListByInId(id);
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(pickingInItemDOList, ErpPickingInItemDO::getProductId));
        return success(BeanUtils.toBean(pickingIn, ErpPickingInRespVO.class,item ->{
            if (item.getAssociationRequisitionId()!=null){
                PurchaseRequisitionDO purchaseRequisition = purchaseRequisitionService.getPurchaseRequisition(item.getAssociationRequisitionId());
                item.setRequisitionCode(purchaseRequisition.getRequisitionCode());
            }
            item.setItems(BeanUtils.toBean(pickingInItemDOList, ErpPickingInRespVO.Item.class, items -> {
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
    @Operation(summary = "获得ERP 领料出库单分页")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:query')")
    public CommonResult<PageResult<ErpPickingInRespVO>> getPickingInPage(@Valid ErpPickingInPageReqVO pageReqVO) {
        PageResult<ErpPickingInDO> pageResult = pickingInService.getPickingInPage(pageReqVO);
        List<ErpPickingInDO> list = pageResult.getList();
        if (pageReqVO.getRequisitionCode() != null && list !=null) {
            PurchaseRequisitionDO purchaseRequisitionDO = purchaseRequisitionService.selectByNo(pageReqVO.getRequisitionCode());
            if (purchaseRequisitionDO != null) {
                // 更新 list 为过滤后的结果
                list = list.stream()
                        .filter(p -> (p.getAssociationRequisitionId() != null))
                        .filter(p -> Objects.equals(p.getAssociationRequisitionId(), purchaseRequisitionDO.getId()))
                        .collect(Collectors.toList());
                pageResult.setList(list);
            }
        }
        // 1.1 项目列表
        Map<Long, ErpAiluoProjectDO> projectMap = ailuoProjectsService.getProjectMap(
                convertSet(list, ErpPickingInDO::getAssociationProjectId));
        Map<Long, PurchaseRequisitionDO> purchaseRequisitionMap =
                purchaseRequisitionService.getPurchaseRequisitionMap
                        (convertSet(list, ErpPickingInDO::getAssociationRequisitionId));
        //查询产品列表
        List<ErpPickingInItemDO> erpPickingInItemDOS = pickingInService.selectListByInIds(
                convertSet(list, ErpPickingInDO::getId));
        // 1.2 产品信息
        Map<Long, ErpProductRespVO> productMap = productService.getProductVOMap(
                convertSet(erpPickingInItemDOS, ErpPickingInItemDO::getProductId));
        Map<Long, List<ErpPickingInItemDO>> longListMap = convertMultiMap(erpPickingInItemDOS, ErpPickingInItemDO::getInId);
        // 1.4 管理员信息
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(list, purchaseIn -> Long.parseLong(purchaseIn.getCreator())));

        return success(BeanUtils.toBean(pageResult, ErpPickingInRespVO.class,item ->{
            item.setItems(BeanUtils.toBean(longListMap.get(item.getId()), ErpPickingInRespVO.Item.class,
                    items -> MapUtils.findAndThen(productMap, items.getProductId(), product -> items.setProductName(product.getName())
                            .setProductBarCode(product.getBarCode()).setProductUnitName(product.getUnitName()))));
            item.setProductNames(CollUtil.join(item.getItems(), "，", ErpPickingInRespVO.Item::getProductName));
            MapUtils.findAndThen(userMap, Long.parseLong(item.getCreator()), user -> item.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(purchaseRequisitionMap, item.getAssociationRequisitionId(), requisitionDO -> item.setAssociationRequisitionNo(requisitionDO.getRequisitionCode()));
            MapUtils.findAndThen(projectMap, item.getAssociationProjectId(), project -> item.setAssociationProjectName(project.getName()));
        }));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出ERP 领料出库单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPickingInExcel(@Valid ErpPickingInPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpPickingInDO> list = pickingInService.getPickingInPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "ERP 领料入库单.xls", "数据", ErpPickingInRespVO.class,
                        BeanUtils.toBean(list, ErpPickingInRespVO.class));
    }

    // ==================== 子表（ERP 领料入库单项） ====================

    @GetMapping("/picking-in-item/list-by-in-id")
    @Operation(summary = "获得ERP 领料出库单项列表")
    @Parameter(name = "inId", description = "领料单编号")
    @PreAuthorize("@ss.hasPermission('erp:picking-in:query')")
    public CommonResult<List<ErpPickingInItemDO>> getPickingInItemListByInId(@RequestParam("inId") Long inId) {
        return success(pickingInService.getPickingInItemListByInId(inId));
    }

}