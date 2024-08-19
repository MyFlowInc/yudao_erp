package cn.iocoder.yudao.module.erp.service.costing;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpReturnMaterialsItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpReturnMaterialsMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.project.ErpAiluoProjectsMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.common.ErpBizTypeEnum;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.iocoder.yudao.module.erp.controller.admin.costing.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostingDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.costing.ErpCostingMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.costing.ErpCostItemMapper;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.erp.enums.common.ErpBizTypeEnum.*;

/**
 * 成本核算 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpCostingServiceImpl implements ErpCostingService {

    @Resource
    private ErpCostingMapper costingMapper;
    @Resource
    private ErpCostItemMapper costItemMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;
    @Resource
    private ErpAiluoProjectsService projectsService;
    @Resource
    private ErpProductMapper productMapper;
    @Resource
    private ErpProductBatchMapper productBatchMapper;
    @Resource
    private ErpPickingInMapper pickingInMapper;
    @Resource
    private ErpPickingInItemMapper pickingInItemMapper;
    @Resource
    private ErpReturnMaterialsMapper returnMaterialsMapper;
    @Resource
    private ErpReturnMaterialsItemMapper returnMaterialsItemMapper;
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public Long createCosting(ErpCostingSaveReqVO createReqVO) {
        // 插入
        ErpCostingDO costing = BeanUtils.toBean(createReqVO, ErpCostingDO.class);
        //校验项目合法性
        validateProject(costing.getAssociationProjectId());
        //校验NO编号唯一
        // 1.2 生成订单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.COSTING_NO_PREFIX);
        if (costingMapper.selectByNo(no) != null) {
            throw exception(COSTING_NO_EXISTS);
        }
        costing.setNo(no);
        costingMapper.insert(costing);
        // 插入子表
        if (createReqVO.getItems() != null) {
            createReqVO.getItems().forEach(o -> o.setCostId(costing.getId()));

            BeanUtils.toBean(createReqVO.getItems(),ErpCostItemDO.class,erpCostItemDO -> {
                //领料则为相反数，还料不进行处理
                if (erpCostItemDO.getType().equals(PICKING.getType())){
                    erpCostItemDO.setMoney(erpCostItemDO.getMoney().negate()).setUnitPrice(erpCostItemDO.getUnitPrice());
                }
                erpCostItemDO.setCostId(costing.getId());
                costItemMapper.insert(erpCostItemDO);
            });
        }
        // 返回
        return costing.getId();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCosting(ErpCostingSaveReqVO updateReqVO) {
        // 校验存在
        validateCostingExists(updateReqVO.getId());
        // 更新
        ErpCostingDO updateObj = BeanUtils.toBean(updateReqVO, ErpCostingDO.class);
        costingMapper.updateById(updateObj);
        // 更新子表
        updateCostItemList(updateReqVO.getId(), convertList(updateReqVO.getItems(), o -> BeanUtils.toBean(o, ErpCostItemDO.class)));
    }

    @Override
    public void updateByIdAndStatus(Long id, Integer status) {
        ErpCostingDO erpCostingDO = validateCostingExists(id);
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        //总领料数
        BigDecimal pickingTotalCount = BigDecimal.ZERO;
        //总领料成本
        BigDecimal pickingMoneyCount = BigDecimal.ZERO;
        //总还料数
        BigDecimal erpReturnMaterialsTotalCount = BigDecimal.ZERO;
        //总领料数
        BigDecimal erpReturnMaterialsMoneyCount = BigDecimal.ZERO;
        //领料项集合
        AtomicReference<List<ErpPickingInItemDO>> erpPickingInItemDOS = new AtomicReference<>(new ArrayList<>());
        //还料项
        AtomicReference<List<ErpReturnMaterialsItemDO>> erpReturnMaterialsDOs = new AtomicReference<>(new ArrayList<>());
        // 获取领料单并拼接领料项
            PageResult<ErpPickingInDO> erpPickingInDOPageResult = pickingInMapper.selectPage(new ErpPickingInPageReqVO().setAssociationProjectId(erpCostingDO.getAssociationProjectId()).setStatus(ErpAuditStatus.APPROVE.getStatus()));
            List<ErpPickingInDO> list = erpPickingInDOPageResult.getList();
        // 开始时间结束时间不为空，否则过滤列表
        LocalDateTime startTime = erpCostingDO.getStartTime();
        LocalDateTime endTime = erpCostingDO.getEndTime();
        if (startTime != null && endTime != null){
            list = list.stream()
                    .filter(item -> {
                        LocalDateTime inTime = item.getInTime();
                        return inTime != null && (inTime.isAfter(startTime) || inTime.isEqual(startTime)) &&
                                (inTime.isBefore(endTime) || inTime.isEqual(endTime));
                    })
                    .collect(Collectors.toList());
        }
            if (list != null){
                list.forEach(o -> {
                    List<ErpPickingInItemDO> items = pickingInItemMapper.selectListByInId(o.getId());
                    erpPickingInItemDOS.updateAndGet(existingList -> {
                        existingList.addAll(items);
                        return existingList;
                    });
                    items.forEach(i->{
                        ErpProductDO erpProductDO = productMapper.selectById(i.getProductId());
                        ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(i.getAssociatedBatchId());
                        costItemMapper.insert(new ErpCostItemDO().setCostId(id).setName(erpProductDO.getName()).setAssociatedBatchId(erpProductBatchDO.getId())
                                .setCount(i.getCount()).setUnitPrice(i.getProductPrice()).setMoney(i.getTotalPrice().negate()).setType(PICKING.getType()));
                    });
                });
            }
            // 获取还料单并拼接还料项
            PageResult<ErpReturnMaterialsDO> erpReturnMaterialsDOPageResult = returnMaterialsMapper
                    .selectPage(new ErpReturnMaterialsPageReqVO().setAssociationProjectId(erpCostingDO
                    .getAssociationProjectId()).setStatus(ErpAuditStatus.APPROVE.getStatus()));
            List<ErpReturnMaterialsDO> returnMaterialsList = erpReturnMaterialsDOPageResult.getList();
        // 过滤列表
            if (startTime != null && endTime != null){
                returnMaterialsList = returnMaterialsList.stream()
                        .filter(item -> {
                            LocalDateTime inTime = item.getInTime();
                            return inTime != null && (inTime.isAfter(startTime) || inTime.isEqual(startTime)) &&
                                    (inTime.isBefore(endTime) || inTime.isEqual(endTime));
                        })
                        .collect(Collectors.toList());
            }
            if (returnMaterialsList!=null){
                returnMaterialsList.forEach(o -> {
                    List<ErpReturnMaterialsItemDO> items = returnMaterialsItemMapper.selectListByReturnId(o.getId());
                    erpReturnMaterialsDOs.updateAndGet(existingList -> {
                        existingList.addAll(items);
                        return existingList;
                    });
                items.forEach(i->{
                    ErpProductDO erpProductDO = productMapper.selectById(i.getProductId());
                    ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(i.getAssociatedBatchId());
                    costItemMapper.insert(new ErpCostItemDO().setCostId(id).setName(erpProductDO.getName()).setAssociatedBatchId(erpProductBatchDO.getId())
                            .setCount(i.getCount()).setUnitPrice(i.getProductPrice()).setMoney(i.getTotalPrice()).setType(RETURN_MATERIALS.getType()));
                });
                });
            }
        //得到总领料量
        if (erpPickingInItemDOS.get() != null){
            pickingTotalCount = erpPickingInItemDOS.get().stream()
                    .map(ErpPickingInItemDO::getCount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            pickingMoneyCount = erpPickingInItemDOS.get().stream()
                    .map(ErpPickingInItemDO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        //总还料数量及成本
        if (erpReturnMaterialsDOs.get() !=null){
            erpReturnMaterialsTotalCount = erpReturnMaterialsDOs.get().stream()
                    .map(ErpReturnMaterialsItemDO::getCount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            erpReturnMaterialsMoneyCount = erpReturnMaterialsDOs.get().stream()
                    .map(ErpReturnMaterialsItemDO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        //总物料成本
        BigDecimal materialCost = pickingMoneyCount.subtract(erpReturnMaterialsMoneyCount);

        List<ErpCostItemDO> erpCostItemDOS = costItemMapper.selectListByCostId(id);
        //其他收入列表
        List<ErpCostItemDO> erpOtherIncomeCostItemDOS = new ArrayList<>();
        //其他支出列表
        List<ErpCostItemDO> erpOtherExpensesCostItemDOS = new ArrayList<>();
        erpCostItemDOS.forEach(o -> {
            if (o.getType().equals(OTHER_INCOME.getType())){
                erpOtherIncomeCostItemDOS.add(o);
            }
            if (o.getType().equals(OTHER_EXPENSES.getType())){
                erpOtherExpensesCostItemDOS.add(o);
            }
        });
        BigDecimal reduce = BigDecimal.ZERO;
        BigDecimal reduce1 = BigDecimal.ZERO;
        //获取其他收入总收入
        reduce = erpOtherIncomeCostItemDOS.stream().map(ErpCostItemDO::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        //获取其他支出，总支出
        reduce1 = erpOtherExpensesCostItemDOS.stream().map(ErpCostItemDO::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        //获取其他收入支出成本
        BigDecimal add = reduce.add(reduce1);
        //总成本
        BigDecimal allCost = materialCost.add(add);
        // 2. 更新状态
        int updateCount = costingMapper.updateByIdAndStatus(id, erpCostingDO.getStatus(),
                new ErpCostingDO().setStatus(Integer.valueOf(String.valueOf(status)))
                        .setPickingCount(pickingTotalCount)
                        .setPickingCost(pickingMoneyCount)
                        .setReturnMaterialsCount(erpReturnMaterialsTotalCount)
                        .setReturnMaterialsCost(erpReturnMaterialsMoneyCount)
                        .setMaterialCost(materialCost)
                        .setOtherCost(reduce)
                        .setOtherExpensesCost(reduce1)
                        .setTotalCost(allCost));
        if (updateCount == 0) {
            throw exception(COSTING_UPDATE_FAIL_APPROVE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCosting(List<Long> ids) {
        // 校验存在
        List<ErpCostingDO> erpCostingDOS = costingMapper.selectBatchIds(ids);
        if (erpCostingDOS == null || erpCostingDOS.isEmpty()) {
            return;
        }
        erpCostingDOS.forEach(erpCostingDO -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(erpCostingDO.getStatus())) {
                throw exception(COSTING_UPDATE_FAIL_APPROVE, erpCostingDO.getNo());
            }
            // 删除
            costingMapper.deleteById(erpCostingDO.getId());
            deleteCostItemByCostId(erpCostingDO.getId());
        });
    }

    private ErpCostingDO validateCostingExists(Long id) {
        if (costingMapper.selectById(id) == null) {
            throw exception(COSTING_NOT_EXISTS);
        }
        return costingMapper.selectById(id);
    }

    @Override
    public ErpCostingDO getCosting(Long id) {
        return costingMapper.selectById(id);
    }

    @Override
    public PageResult<ErpCostingDO> getCostingPage(ErpCostingPageReqVO pageReqVO) {
        return costingMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（成本核算项） ====================

    @Override
    public List<ErpCostItemDO> getCostItemListByCostId(Long costId) {
        return costItemMapper.selectListByCostId(costId);
    }

    @Override
    public PageResult<ErpCostItemDO> selectPage(ErpCostingPageReqVO reqVO) {
        return costItemMapper.selectPage(reqVO);
    }


    private void validateProject(Long id) {
        Long projectId = Long.valueOf(id);
        List<ErpAiluoProjectDO> activeProjects = projectsService.getActiveProjects();
        AtomicBoolean a = new AtomicBoolean(false);
        activeProjects.forEach( o->{
            if (o.getId().equals(projectId)){
                a.set(true);
            }
        });
        if (!a.get()){
            throw exception(COSTING_PROJECT_NOT_EXISTS);
        }
    }

    private void updateCostItemList(Long costId, List<ErpCostItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpCostItemDO> oldList = costItemMapper.selectListByCostId(costId);
        // id 不同，就认为是不同的记录
        List<List<ErpCostItemDO>> diffList = CollectionUtils.diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));
        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setCostId(costId));
            costItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            costItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            costItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpCostItemDO::getId));
        }
    }

    private void deleteCostItemByCostId(Long costId) {
        costItemMapper.deleteByCostId(costId);
    }

}