package cn.iocoder.yudao.module.erp.service.material;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.requisition.RequisitionProductMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInItemMapper;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 领料出库单 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpPickingInServiceImpl implements ErpPickingInService {

    @Resource
    private ErpPickingInMapper pickingInMapper;
    @Resource
    private ErpPickingInItemMapper pickingInItemMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;
    @Resource
    private ErpProductMapper productMapper;
    @Resource
    private RequisitionProductMapper requisitionProductMapper;
    @Resource
    private ErpProductBatchMapper productBatchMapper;
    @Resource
    private ErpProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ErpStockRecordService stockRecordService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPickingIn(ErpPickingInSaveReqVO createReqVO) {
        // 1.1 校验出库项的有效性
        List<ErpPickingInItemDO> erpPickingInItemDOS = validateErpPickingInItems(createReqVO.getItems());
        // 1.3 生成出库单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.PICKING_IN_NO_PREFIX);
        if (pickingInMapper.selectByNo(no) != null) {
            throw exception(PICKING_IN_NOT_NO_EXISTS);
        }
        //1.4 校验领料是否大于批次数量
        erpPickingInItemDOS.forEach( o->{
            ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(o.getAssociatedBatchId());
            if (o.getCount().compareTo(erpProductBatchDO.getInventoryQuantity()) >0){
                throw exception(PICKING_IN_NOT_COUNT_EXISTS);
            }
        });
        // 2.1 插入出库单
        ErpPickingInDO erpPickingInDO = BeanUtils.toBean(createReqVO, ErpPickingInDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus())
                .setTotalCount(getSumValue(erpPickingInItemDOS, ErpPickingInItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(erpPickingInItemDOS, ErpPickingInItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        // 插入
        ErpPickingInDO pickingIn = BeanUtils.toBean(createReqVO, ErpPickingInDO.class);
        pickingInMapper.insert(erpPickingInDO);
        // 插入子表
        erpPickingInItemDOS.forEach(o -> o.setInId(pickingIn.getId()));
        pickingInItemMapper.insertBatch(erpPickingInItemDOS);
        // 返回
        return pickingIn.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePickingIn(ErpPickingInSaveReqVO updateReqVO) {
        // 校验存在
        validatePickingInExists(updateReqVO.getId());

        List<ErpPickingInItemDO> erpPickingInItemDOS = validateErpPickingInItems(updateReqVO.getItems());
        //1.4 校验领料是否大于批次数量
        erpPickingInItemDOS.forEach( o->{
            ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(o.getAssociatedBatchId());
            if (o.getCount().compareTo(erpProductBatchDO.getInventoryQuantity()) >0){
                throw exception(PICKING_IN_NOT_COUNT_EXISTS);
            }
        });
        //更新
        ErpPickingInDO erpPickingInDO = BeanUtils.toBean(updateReqVO, ErpPickingInDO.class, in -> in
                .setTotalCount(getSumValue(erpPickingInItemDOS, ErpPickingInItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(erpPickingInItemDOS, ErpPickingInItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        pickingInMapper.updateById(erpPickingInDO);
        // 更新子表
        updatePickingInItemList(updateReqVO.getId(), erpPickingInItemDOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePickingInStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpPickingInDO stockOut = validatePickingInExists(id);
        // 1.2 校验状态
        Integer bizType = approve ? ErpStockRecordBizTypeEnum.MATERIAL_REQUISITION_OUTBOUND.getType()
                : ErpStockRecordBizTypeEnum.MATERIAL_REQUISITION_OUTBOUND_CANCEL.getType();
        if (stockOut != null){
            if (stockOut.getStatus().equals(status)) {
                throw exception(approve ? PICKING_IN_APPROVE_FAIL : PICKING_IN_PROCESS_FAIL);
            }
            // 2. 更新状态
            int updateCount = pickingInMapper.updateByIdAndStatus(id, stockOut.getStatus(),
                    new ErpPickingInDO().setStatus(status));
            if (updateCount == 0) {
                throw exception(approve ? PICKING_IN_APPROVE_FAIL : PICKING_IN_PROCESS_FAIL);
            }
        }
        if (approve){
            // 3. 变更库存
            List<ErpPickingInItemDO> pickingInItemDOList = pickingInItemMapper.selectListByInId(id);
            pickingInItemDOList.forEach(item -> {
                if (item.getAssociationRequisitionProductId() != null) {
                    RequisitionProductDO requisitionProductDO = requisitionProductMapper.selectById(item.getAssociationRequisitionProductId());
                    requisitionProductMapper.updateById(requisitionProductDO.setOutCount(requisitionProductDO.getOutCount().add(item.getCount())));
                }
                //扣除对应批次库存数量
                ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(item.getAssociatedBatchId());
                productBatchMapper.updateById(new ErpProductBatchDO().setInventoryQuantity(erpProductBatchDO.getInventoryQuantity().subtract(item.getCount())));
                BigDecimal count = approve ? item.getCount().negate() : item.getCount();
                if (stockOut != null) {
                    //生成出入库明细且扣除库存
                    stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                            item.getProductId(), item.getWarehouseId(), count,
                            bizType, item.getInId(), item.getId(), stockOut.getNo()));
                }
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePickingIn(List<String> ids) {
        // 校验存在
        List<ErpPickingInDO> picking = pickingInMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        picking.forEach(o -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(o.getStatus())) {
                throw exception(PICKING_IN_NOT_NO_EXISTS_APPROVE, o.getNo());
            }
        });
        // 2. 遍历删除，并记录操作日志
        picking.forEach(stockOut -> {
            // 2.1 删除出库单
            pickingInMapper.deleteById(stockOut.getId());
            // 2.2 删除出库单项
            pickingInItemMapper.deleteByInId(stockOut.getId());
        });
    }

    private ErpPickingInDO validatePickingInExists(Long id) {
        if (pickingInMapper.selectById(id) == null) {
            throw exception(PICKING_IN_NOT_EXISTS);
        }
        return null;
    }

    @Override
    public ErpPickingInDO getPickingIn(Long id) {
        return pickingInMapper.selectById(id);
    }

    @Override
    public PageResult<ErpPickingInDO> getPickingInPage(ErpPickingInPageReqVO pageReqVO) {
        return pickingInMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（ERP 领料入库单项） ====================

    @Override
    public List<ErpPickingInItemDO> getPickingInItemListByInId(Long inId) {
        return pickingInItemMapper.selectListByInId(inId);
    }

    private void updatePickingInItemList(Long inId, List<ErpPickingInItemDO> list) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpPickingInItemDO> oldList = pickingInItemMapper.selectListByInId(inId);
        // id 不同，就认为是不同的记录
        List<List<ErpPickingInItemDO>> diffList = diffList(oldList, list,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setInId(inId));
            pickingInItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            pickingInItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            pickingInItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpPickingInItemDO::getId));
        }
    }

    private void deletePickingInItemByInId(Long inId) {
        pickingInItemMapper.deleteByInId(inId);
    }
    private List<ErpPickingInItemDO> validateErpPickingInItems(List<ErpPickingInSaveReqVO.Item> list) {
        // 1.1 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpPickingInSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 1.2 校验仓库存在
        warehouseService.validWarehouseList(convertSet(list, ErpPickingInSaveReqVO.Item::getWarehouseId));
        // 2. 转化为 ErpStockOutItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpPickingInItemDO.class, item -> item
                .setProductUnitId(productMap.get(item.getProductId()).getUnitId())
                .setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()))));
    }
}