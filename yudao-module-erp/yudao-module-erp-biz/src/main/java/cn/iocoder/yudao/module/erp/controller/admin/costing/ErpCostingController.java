package cn.iocoder.yudao.module.erp.controller.admin.costing;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.common.ErpBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.productbatch.ErpProductBatchService;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import java.time.LocalDateTime;
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

import cn.iocoder.yudao.module.erp.controller.admin.costing.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostingDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostItemDO;
import cn.iocoder.yudao.module.erp.service.costing.ErpCostingService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "管理后台 - 成本核算")
@RestController
@RequestMapping("/erp/costing")
@Validated
public class ErpCostingController {

    @Resource
    private ErpCostingService costingService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ErpAiluoProjectsService ailuoProjectsService;
    @Resource
    private ErpProductBatchService productBatchService;
    @PostMapping("/create")
    @Operation(summary = "创建成本核算")
    @PreAuthorize("@ss.hasPermission('erp:costing:create')")
    public CommonResult<Long> createCosting(@Valid @RequestBody ErpCostingSaveReqVO createReqVO) {
        return success(costingService.createCosting(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新成本核算")
    @PreAuthorize("@ss.hasPermission('erp:costing:update')")
    public CommonResult<Boolean> updateCosting(@Valid @RequestBody ErpCostingSaveReqVO updateReqVO) {
        costingService.updateCosting(updateReqVO);
        return success(true);
    }
    @PutMapping("/update-status")
    @Operation(summary = "更新成本核算单的状态")
    @PreAuthorize("@ss.hasPermission('erp:costing:update-status')")
    public CommonResult<Boolean> updatePurchaseRequisitionStatus(@RequestParam("id") Long id,
                                                                 @RequestParam("status") Integer status) {
        costingService.updateByIdAndStatus(id, status);
        return success(true);
    }
    @DeleteMapping("/delete")
    @Operation(summary = "删除成本核算")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:costing:delete')")
    public CommonResult<Boolean> deleteCosting(@RequestParam("ids") List<Long> ids) {
        costingService.deleteCosting(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得成本核算")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:costing:query')")
    public CommonResult<ErpCostingRespVO> getCosting(@RequestParam("id") Long id,Integer type) {
        ErpCostingDO costing = costingService.getCosting(id);
            List<ErpCostItemDO> costItemListByCostId = costingService.getCostItemListByCostId(id);
                if (Objects.equals(costing.getStatus(), ErpAuditStatus.APPROVE.getStatus())){
                    return success(BeanUtils.toBean(costing, ErpCostingRespVO.class));
                }

            // 如果类型不为空，则进行过滤
            if (type != null) {
                // 过滤出类型匹配的元素
                costItemListByCostId = costItemListByCostId.stream()
                        .filter(p -> p.getType().equals(type))
                        .collect(Collectors.toList());
                // 你可以在这里使用 filteredList 进行后续操作
            }
            List<ErpCostItemDO> finalCostItemListByCostId = costItemListByCostId;
            return success(BeanUtils.toBean(costing, ErpCostingRespVO.class, item ->{
                item.setItems(BeanUtils.toBean(finalCostItemListByCostId,ErpCostingRespVO.Item.class, item1 -> {
                    if (item1.getAssociatedBatchId() != null){
                        item1.setAssociatedBatchName(productBatchService.getProductBatch(item1.getAssociatedBatchId()).getName());
                    }
                }));
            }));
    }

    @GetMapping("/page")
    @Operation(summary = "获得成本核算分页")
    @PreAuthorize("@ss.hasPermission('erp:costing:query')")
    public CommonResult<PageResult<ErpCostingRespVO>> getCostingPage(@Valid ErpCostingPageReqVO pageReqVO) {
        PageResult<ErpCostingDO> pageResult = costingService.getCostingPage(pageReqVO);
        List<ErpCostingDO> list = pageResult.getList();
        // 1.1 项目列表
        Map<Long, ErpAiluoProjectDO> projectMap = ailuoProjectsService.getProjectMap(
                convertSet(list, ErpCostingDO::getAssociationProjectId));
        // 1.4 管理员信息
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(list, purchaseIn -> Long.parseLong(purchaseIn.getCreator())));

        return success(BeanUtils.toBean(pageResult, ErpCostingRespVO.class,erpCostingRespVO -> {
            erpCostingRespVO.setItems(BeanUtils.toBean(erpCostingRespVO.getItems(),ErpCostingRespVO.Item.class, item1 -> {
                if (item1.getAssociatedBatchId() != null){
                    item1.setAssociatedBatchName(productBatchService.getProductBatch(item1.getAssociatedBatchId()).getName());
                }
            }) );
            MapUtils.findAndThen(userMap, Long.parseLong(erpCostingRespVO.getCreator()), user -> erpCostingRespVO.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(projectMap, erpCostingRespVO.getAssociationProjectId(), project -> erpCostingRespVO.setAssociationProjectName(project.getName()));
        }));
    }

    @GetMapping("/costItemPage")
    @Operation(summary = "获得成本核算分页")
    @PreAuthorize("@ss.hasPermission('erp:costing:query')")
    public CommonResult<PageResult<ErpCostingRespVO.Item>> getCostingItemPage(@Valid ErpCostingPageReqVO pageReqVO) {
        PageResult<ErpCostItemDO> erpCostItemDOPageResult = costingService.selectPage(pageReqVO);
        return success(BeanUtils.toBean(erpCostItemDOPageResult, ErpCostingRespVO.Item.class,o->{
                    if (o.getAssociatedBatchId() != null) {
                        o.setAssociatedBatchName(productBatchService.getProductBatch(o.getAssociatedBatchId()).getName());
                    }
            }));
        }
    @GetMapping("/export-excel")
    @Operation(summary = "导出成本核算单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:costing:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCostingExcel(@Valid ErpCostingPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpCostingDO> list = costingService.getCostingPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "成本核算单.xls", "数据", ErpCostingRespVO.class,
                BeanUtils.toBean(list, ErpCostingRespVO.class));
    }

    @GetMapping("/export-excel-item")
    @Operation(summary = "导出成本核算单 Excel")
    @PreAuthorize("@ss.hasPermission('erp:costing:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCostingItemExcel(@Valid ErpCostingPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<ErpCostItemDO> erpCostItemDOPageResult = costingService.selectPage(pageReqVO);
        List<ErpCostItemDO> list = erpCostItemDOPageResult.getList();
        List<ErpCostingRespVO.Item> bean = BeanUtils.toBean(list, ErpCostingRespVO.Item.class);
        // 使用流进行分类
        Map<Integer, List<ErpCostingRespVO.Item>> dataMap = bean.stream()
                .collect(Collectors.groupingBy(ErpCostingRespVO.Item::getType));
        // 导出 Excel
        ExcelUtils.writeCostItemMap(response, "成本核算项.xls",dataMap,ErpCostingRespVO.Item.class);
    }

    // ==================== 子表（成本核算项） ====================

    @GetMapping("/cost-item/list-by-cost-id")
    @Operation(summary = "获得成本核算项列表")
    @Parameter(name = "costId", description = "核算单id")
    @PreAuthorize("@ss.hasPermission('erp:costing:query')")
    public CommonResult<List<ErpCostItemDO>> getCostItemListByCostId(@RequestParam("costId") Long costId) {
        return success(costingService.getCostItemListByCostId(costId));
    }

}

