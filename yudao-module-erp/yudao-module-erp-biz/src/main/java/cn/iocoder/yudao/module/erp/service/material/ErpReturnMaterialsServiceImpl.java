package cn.iocoder.yudao.module.erp.service.material;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.in.ErpPickingInSaveReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsPageReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.material.vo.out.ErpReturnMaterialsSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInItemMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpPickingInMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.requisition.RequisitionProductMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.enums.stock.ErpStockRecordBizTypeEnum;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.purchase.ErpPurchaseInService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsItemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpReturnMaterialsMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.material.ErpReturnMaterialsItemMapper;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.*;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 还料入库单 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpReturnMaterialsServiceImpl implements ErpReturnMaterialsService {

    @Resource
    private ErpReturnMaterialsMapper returnMaterialsMapper;
    @Resource
    private ErpReturnMaterialsItemMapper returnMaterialsItemMapper;
    @Resource
    private ErpPickingInItemMapper pickingInItemMapper;
    @Resource
    private ErpPurchaseInService purchaseInService;
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
    public Long createReturnMaterials(ErpReturnMaterialsSaveReqVO createReqVO) {
        // 1.1 校验出库项的有效性
        List<ErpReturnMaterialsItemDO> erpReturnMaterialsItemDOS = validateErpReturnMaterialsItems(createReqVO.getItems());
        // 1.3 生成出库单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.RETURN_MATERIALS_NO_PREFIX);

        if (returnMaterialsMapper.selectByNo(no) != null) {
            throw exception(RETURN_MATERIALS_NOT_EXISTS);
        }
        //还料数量是否>领料数量-还料数量
        validateReturnMaterialsList(erpReturnMaterialsItemDOS);
        // 2.1 插入出库单
        ErpReturnMaterialsDO erpPickingInDO = BeanUtils.toBean(createReqVO, ErpReturnMaterialsDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus())
                .setTotalCount(getSumValue(erpReturnMaterialsItemDOS, ErpReturnMaterialsItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(erpReturnMaterialsItemDOS, ErpReturnMaterialsItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        // 插入
        returnMaterialsMapper.insert(erpPickingInDO);
        // 插入子表
        erpReturnMaterialsItemDOS.forEach(o -> o.setReturnId(erpPickingInDO.getId()));
        returnMaterialsItemMapper.insertBatch(erpReturnMaterialsItemDOS);
        // 返回
        return erpPickingInDO.getId();
    }

    private List<ErpReturnMaterialsItemDO> validateErpReturnMaterialsItems(List<ErpReturnMaterialsSaveReqVO.Item> list) {
        // 1.1 校验产品存在
        List<ErpProductDO> productList = productService.validProductList(
                convertSet(list, ErpReturnMaterialsSaveReqVO.Item::getProductId));
        Map<Long, ErpProductDO> productMap = convertMap(productList, ErpProductDO::getId);
        // 1.2 校验仓库存在
        warehouseService.validWarehouseList(convertSet(list, ErpReturnMaterialsSaveReqVO.Item::getWarehouseId));
        // 2. 转化为 ErpStockOutItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpReturnMaterialsItemDO.class, item -> item
                .setProductUnitId(productMap.get(item.getProductId()).getUnitId())
                .setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()))));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReturnMaterials(ErpReturnMaterialsSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnMaterialsExists(updateReqVO.getId());

        List<ErpReturnMaterialsItemDO> erpReturnMaterialsItemDOS = validateErpReturnMaterialsItems(updateReqVO.getItems());
        //1.4 还料数量是否>领料数量-还料数量
        validateReturnMaterialsList(erpReturnMaterialsItemDOS);
        // 更新
        ErpReturnMaterialsDO updateObj = BeanUtils.toBean(updateReqVO, ErpReturnMaterialsDO.class);
        returnMaterialsMapper.updateById(updateObj);
        // 更新子表
        updateReturnMaterialsItemList(updateReqVO.getId(), erpReturnMaterialsItemDOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReturnMaterialsStatus(Long id, Integer status) {

        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpReturnMaterialsDO erpReturnMaterialsDO = validateReturnMaterialsExists(id);
        // 1.2 校验状态
        Integer bizType = approve ? ErpStockRecordBizTypeEnum.RETURN_MATERIALS_TO_WAREHOUSE.getType()
                : ErpStockRecordBizTypeEnum.RETURN_MATERIALS_TO_WAREHOUSE_CANCEL.getType();
            if (erpReturnMaterialsDO.getStatus().equals(status)) {
                throw exception(approve ? RETURN_MATERIALS_APPROVE_FAIL : RETURN_MATERIALS_PROCESS_FAIL);
            }
            // 2. 更新状态
            int updateCount = returnMaterialsMapper.updateReturnMaterialsStatus(id, erpReturnMaterialsDO.getStatus(),
                    new ErpReturnMaterialsDO().setStatus(status));
            if (updateCount == 0) {
                throw exception(approve ? RETURN_MATERIALS_APPROVE_FAIL : RETURN_MATERIALS_PROCESS_FAIL);
            }
            //处理还料子项
        List<ErpReturnMaterialsItemDO> erpReturnMaterialsItemDOS = returnMaterialsItemMapper.selectListByReturnId(id);
            if (erpReturnMaterialsItemDOS != null) {
                erpReturnMaterialsItemDOS.forEach(o->{
                    //关联领料项
                    ErpPickingInItemDO erpPickingInItemDO = pickingInItemMapper.selectById(o.getAssociatedPickingItemId());
                    //所有关联这一领料项的还料项集合
                    List<ErpReturnMaterialsItemDO> erpReturnMaterialsItemDOS1 = returnMaterialsItemMapper.selectListByPickingItemId(o.getAssociatedPickingItemId());
                    erpReturnMaterialsItemDOS1 = erpReturnMaterialsItemDOS1.stream()
                            .filter(item -> !item.getDeleted())
                            .collect(Collectors.toList());
                    Iterator<ErpReturnMaterialsItemDO> iterator = erpReturnMaterialsItemDOS1.iterator();
                    while (iterator.hasNext()) {
                        // 使用迭代器的去除为审核的数据
                        ErpReturnMaterialsItemDO items = iterator.next();
                        ErpReturnMaterialsDO returnMaterialsDO = returnMaterialsMapper.selectById(items.getReturnId());
                        if (!Objects.equals(returnMaterialsDO.getStatus(), ErpAuditStatus.APPROVE.getStatus())) {
                            iterator.remove();
                        }
                    }
                    //集合总数量
                    BigDecimal totalCount = erpReturnMaterialsItemDOS1.stream()
                            .map(ErpReturnMaterialsItemDO::getCount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalCount = totalCount.subtract(erpPickingInItemDO.getReturnMaterialsCount());
                    if (totalCount.compareTo(erpPickingInItemDO.getCount())>0) {
                        throw exception(RETURN_MATERIALSNOTCOUNT_EXISTS);
                    }
                    if (o.getAssociationRequisitionProductId()!=null){
                        //变更请购项已领料数量
                        RequisitionProductDO requisitionProductDO = requisitionProductMapper.selectById(o.getAssociationRequisitionProductId());
                        requisitionProductMapper.updateById(requisitionProductDO.setOutCount(requisitionProductDO.getOutCount().subtract(o.getCount())));
                    }
                    //变更领料单还料数量
                    pickingInItemMapper.updateById(erpPickingInItemDO.setReturnMaterialsCount(erpPickingInItemDO.getReturnMaterialsCount().add(o.getCount())));
                    //变更批次库存
                    purchaseInService.updateBatchQuantity(o.getAssociatedBatchId(),o.getCount(),10);

                });
            }
            if(erpReturnMaterialsItemDOS != null) {
            erpReturnMaterialsItemDOS.forEach(erpReturnMaterialsItemDO -> {
                BigDecimal count = approve ? erpReturnMaterialsItemDO.getCount() : erpReturnMaterialsItemDO.getCount().negate();
                stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                        erpReturnMaterialsItemDO.getProductId(), erpReturnMaterialsItemDO.getWarehouseId(), count,
                        bizType, erpReturnMaterialsItemDO.getReturnId(), erpReturnMaterialsItemDO.getId(), erpReturnMaterialsDO.getNo()));
            });
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReturnMaterials(List<Long> ids) {
        // 校验存在
        List<ErpReturnMaterialsDO> picking = returnMaterialsMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        picking.forEach(o -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(o.getStatus())) {
                throw exception(RETURN_MATERIALS_NOT_NO_EXISTS_APPROVE, o.getNo());
            }
        });
        // 2. 遍历删除，并记录操作日志
        picking.forEach(stockOut -> {
            // 2.1 删除出库单
            returnMaterialsMapper.deleteById(stockOut.getId());
            // 2.2 删除出库单项
            returnMaterialsItemMapper.deleteByReturnId(stockOut.getId());
        });
    }

    private ErpReturnMaterialsDO validateReturnMaterialsExists(Long id) {
        if (returnMaterialsMapper.selectById(id) == null) {
            throw exception(RETURN_MATERIALS_NOT_EXISTS);
        }
        return returnMaterialsMapper.selectById(id);
    }

    @Override
    public ErpReturnMaterialsDO getReturnMaterials(Long id) {
        return returnMaterialsMapper.selectById(id);
    }

    @Override
    public PageResult<ErpReturnMaterialsDO> getReturnMaterialsPage(ErpReturnMaterialsPageReqVO pageReqVO) {
        return returnMaterialsMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（ERP 还料入库单项） ====================

    @Override
    public List<ErpReturnMaterialsItemDO> getReturnMaterialsItemListByReturnId(Long returnId) {
        return returnMaterialsItemMapper.selectListByReturnId(returnId);
    }

    @Override
    public List<ErpReturnMaterialsItemDO> selectListByPickingItemId(Long id) {
        return returnMaterialsItemMapper.selectListByPickingItemId(id);
    }

    private void updateReturnMaterialsItemList(Long returnId, List<ErpReturnMaterialsItemDO> list) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpReturnMaterialsItemDO> oldList = returnMaterialsItemMapper.selectListByReturnId(returnId);
        // id 不同，就认为是不同的记录
        List<List<ErpReturnMaterialsItemDO>> diffList = diffList(oldList, list,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setReturnId(returnId));
            returnMaterialsItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            returnMaterialsItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            returnMaterialsItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpReturnMaterialsItemDO::getId));
        }
    }
    public void validateReturnMaterialsList (List<ErpReturnMaterialsItemDO> list){
        //1.4 还料数量是否>领料数量-还料数量
        list.forEach( o->{
            //关联领料项
            ErpPickingInItemDO erpPickingInItemDO = pickingInItemMapper.selectById(o.getAssociatedPickingItemId());
            //所有关联这一领料项的还料项集合
            List<ErpReturnMaterialsItemDO> erpReturnMaterialsItemDOS1 = returnMaterialsItemMapper.selectListByPickingItemId(o.getAssociatedPickingItemId());
            erpReturnMaterialsItemDOS1 = erpReturnMaterialsItemDOS1.stream()
                    .filter(item -> !item.getDeleted())
                    .collect(Collectors.toList());
            //集合总数量
            BigDecimal totalCount = erpReturnMaterialsItemDOS1.stream()
                    .map(ErpReturnMaterialsItemDO::getCount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            //对比换领料单数量
            if (totalCount.compareTo(erpPickingInItemDO.getCount().subtract(erpPickingInItemDO.getReturnMaterialsCount())) >0){
                throw exception(RETURN_MATERIALSNOTCOUNT_EXISTS);
            }
        });
    }
}