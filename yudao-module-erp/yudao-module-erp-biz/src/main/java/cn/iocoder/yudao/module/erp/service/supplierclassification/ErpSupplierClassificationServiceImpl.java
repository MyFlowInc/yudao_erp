package cn.iocoder.yudao.module.erp.service.supplierclassification;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification.ErpSupplierClassificationDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.supplierclassification.ErpSupplierClassificationMapper;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static com.fhs.common.constant.Constant.DISABLE;
import static com.fhs.common.constant.Constant.ENABLED;

/**
 * 供应商分类 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpSupplierClassificationServiceImpl implements ErpSupplierClassificationService {

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private ErpSupplierClassificationMapper supplierClassificationMapper;

    @Override
    public Long createSupplierClassification(ErpSupplierClassificationSaveReqVO createReqVO) {
        // 校验父id的有效性
        validateParentSupplierClassification(null, createReqVO.getParentId());
        // 校验分类名称的唯一性
        validateSupplierClassificationNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入
        ErpSupplierClassificationDO supplierClassification = BeanUtils.toBean(createReqVO, ErpSupplierClassificationDO.class);
        supplierClassificationMapper.insert(supplierClassification);
        // 返回
        return supplierClassification.getId();
    }

    @Override
    public void updateSupplierClassification(ErpSupplierClassificationSaveReqVO updateReqVO) {
        // 校验存在
        validateSupplierClassificationExists(updateReqVO.getId());
        // 校验父id的有效性
        validateParentSupplierClassification(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验分类名称的唯一性
        validateSupplierClassificationNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());
        // 更新

        if (Objects.equals(updateReqVO.getStatus(), ENABLED)) {
            // 查询并递归更新所有子节点状态为关闭
            recursiveUpdateStatus(updateReqVO.getId());
        }
        ErpSupplierClassificationDO updateObj = BeanUtils.toBean(updateReqVO, ErpSupplierClassificationDO.class);
        supplierClassificationMapper.updateById(updateObj);
    }
    /**
     * 递归更新所有子节点及其后代节点的状态为1
     * @param id 当前节点ID
     */
    private void recursiveUpdateStatus(Long id) {
        // 查询当前节点的子节点
        ErpSupplierClassificationListReqVO queryVO = new ErpSupplierClassificationListReqVO();
        queryVO.setParentId(id);
        List<ErpSupplierClassificationDO> children = supplierClassificationMapper.selectList(queryVO);
        if (children != null && !children.isEmpty()) {
            // 递归更新每个子节点及其子节点的状态为关闭
            for (ErpSupplierClassificationDO child : children) {
                if (child != null) {
                    // 设置状态为关闭
                    child.setStatus(String.valueOf(ENABLED));
                    // 更新子节点状态为全部关闭
                    supplierClassificationMapper.updateById(child);
                    // 递归更新子节点的子节点
                    recursiveUpdateStatus(child.getId());
                }
            }
        }
    }
    @Override
    public void deleteSupplierClassification(Long id) {
        // 校验存在
        validateSupplierClassificationExists(id);
        // 校验是否有子供应商分类
        if (supplierClassificationMapper.selectCountByParentId(id) > 0) {
            throw exception(SUPPLIER_CLASSIFICATION_EXITS_CHILDREN);
        }
        // 删除
        supplierClassificationMapper.deleteById(id);
    }

    private void validateSupplierClassificationExists(Long id) {
        if (supplierClassificationMapper.selectById(id) == null) {
            throw exception(SUPPLIER_CLASSIFICATION_NOT_EXISTS);
        }
    }

    private void validateParentSupplierClassification(Long id, Long parentId) {
        if (parentId == null || ErpSupplierClassificationDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父供应商分类
        if (Objects.equals(id, parentId)) {
            throw exception(SUPPLIER_CLASSIFICATION_PARENT_ERROR);
        }
        // 2. 父供应商分类不存在
        ErpSupplierClassificationDO parentSupplierClassification = supplierClassificationMapper.selectById(parentId);
        if (parentSupplierClassification == null) {
            throw exception(SUPPLIER_CLASSIFICATION_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父供应商分类，如果父供应商分类是自己的子供应商分类，则报错，避免形成环路
        // id 为空，说明新增，不需要考虑环路
        if (id == null) {
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentSupplierClassification.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(SUPPLIER_CLASSIFICATION_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父供应商分类
            if (parentId == null || ErpSupplierClassificationDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentSupplierClassification = supplierClassificationMapper.selectById(parentId);
            if (parentSupplierClassification == null) {
                break;
            }
        }
    }

    private void validateSupplierClassificationNameUnique(Long id, Long parentId, String name) {
        ErpSupplierClassificationDO supplierClassification = supplierClassificationMapper.selectByParentIdAndName(parentId, name);
        if (supplierClassification == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的供应商分类
        if (id == null) {
            throw exception(SUPPLIER_CLASSIFICATION_NAME_DUPLICATE);
        }
        if (!Objects.equals(supplierClassification.getId(), id)) {
            throw exception(SUPPLIER_CLASSIFICATION_NAME_DUPLICATE);
        }
    }

    @Override
    public ErpSupplierClassificationDO getSupplierClassification(Long id) {
        return supplierClassificationMapper.selectById(id);
    }

    @Override
    public List<ErpSupplierClassificationDO> getSupplierClassificationList(ErpSupplierClassificationListReqVO listReqVO) {
        return supplierClassificationMapper.selectList(listReqVO);
    }

    @Override
    public List<ErpSupplierClassificationDO> selecStatusIsZeroList(ErpSupplierClassificationListReqVO listReqVO) {
        return supplierClassificationMapper.selecStatusIsZeroList(listReqVO);
    }

}