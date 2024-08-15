package cn.iocoder.yudao.module.erp.service.costing;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.erp.dal.dataobject.project.ErpAiluoProjectDO;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.service.project.ErpAiluoProjectsService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
        // 2. 更新状态
        int updateCount = costingMapper.updateByIdAndStatus(id, erpCostingDO.getStatus(),
                new ErpCostingDO().setStatus(Integer.valueOf(String.valueOf(status))));

        if (updateCount == 0) {
            throw exception(approve ? REQUISITION_ORDER_UPDATE_FAIL_APPROVE : REQUISITION_ORDER__PROCESS_FAIL);
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