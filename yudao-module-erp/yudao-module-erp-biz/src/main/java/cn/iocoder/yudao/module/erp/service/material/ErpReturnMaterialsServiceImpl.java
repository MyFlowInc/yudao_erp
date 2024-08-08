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
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.requisition.RequisitionProductMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import cn.iocoder.yudao.module.erp.enums.ErpAuditStatus;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.returnmaterialsitem.ErpReturnMaterialsItemDO;
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
//        //1.4 校验领料是否大于批次数量
//        erpReturnMaterialsItemDOS.forEach( o->{
//            ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(o.getAssociatedBatchId());
//            if (o.getCount().compareTo(erpProductBatchDO.getInventoryQuantity()) >0){
//                throw exception(PICKING_IN_NOT_COUNT_EXISTS);
//            }
//        });
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

        List<ErpReturnMaterialsItemDO> erpPickingInItemDOS = validateErpReturnMaterialsItems(updateReqVO.getItems());
        //1.4 校验还料是否大于批次数量
//        erpPickingInItemDOS.forEach( o->{
//            ErpProductBatchDO erpProductBatchDO = productBatchMapper.selectById(o.getAssociatedBatchId());
//            if (o.getCount().compareTo(erpProductBatchDO.getInventoryQuantity()) >0){
//                throw exception(PICKING_IN_NOT_COUNT_EXISTS);
//            }
//        });
        // 更新
        ErpReturnMaterialsDO updateObj = BeanUtils.toBean(updateReqVO, ErpReturnMaterialsDO.class);
        returnMaterialsMapper.updateById(updateObj);

        // 更新子表
        updateReturnMaterialsItemList(updateReqVO.getId(), erpPickingInItemDOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReturnMaterialsStatus(Long id, Integer status) {


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

    private void validateReturnMaterialsExists(Long id) {
        if (returnMaterialsMapper.selectById(id) == null) {
            throw exception(RETURN_MATERIALS_NOT_EXISTS);
        }
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

    private void deleteReturnMaterialsItemByReturnId(Long returnId) {
        returnMaterialsItemMapper.deleteByReturnId(returnId);
    }

}