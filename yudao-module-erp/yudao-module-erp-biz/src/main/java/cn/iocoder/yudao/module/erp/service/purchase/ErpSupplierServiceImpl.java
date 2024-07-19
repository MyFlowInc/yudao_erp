package cn.iocoder.yudao.module.erp.service.purchase;

import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierListReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierRespVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierSaveReqVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.purchase.ErpSupplierMapper;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;

/**
 * ERP 供应商 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpSupplierServiceImpl implements ErpSupplierService {

    @Resource
    private ErpSupplierMapper supplierMapper;

    @Override
    public Long createSupplier(@Valid ErpSupplierSaveReqVO createReqVO) {
        // 校验父级编号的有效性
        validateParentSupplier(null, Long.valueOf(createReqVO.getParentId()));
        // 校验供应商类型的唯一性
        validateSupplierTypeUnique(null, Long.valueOf(createReqVO.getParentId()), createReqVO.getType());

        // 插入
        ErpSupplierDO supplier = BeanUtils.toBean(createReqVO, ErpSupplierDO.class);
        supplierMapper.insert(supplier);
        // 返回
        return supplier.getId();
    }

    @Override
    public void updateSupplier(ErpSupplierSaveReqVO updateReqVO) {
        // 校验存在
        validateSupplierExists(updateReqVO.getId());
        // 校验父级编号的有效性
        validateParentSupplier(updateReqVO.getId(), Long.valueOf(updateReqVO.getParentId()));
        // 校验供应商类型的唯一性
        validateSupplierTypeUnique(updateReqVO.getId(), Long.valueOf(updateReqVO.getParentId()), updateReqVO.getType());

        // 更新
        ErpSupplierDO updateObj = BeanUtils.toBean(updateReqVO, ErpSupplierDO.class);
        supplierMapper.updateById(updateObj);
    }

    @Override
    public void deleteSupplier(Long id) {
        // 校验存在
        validateSupplierExists(id);
        // 校验是否有子ERP 供应商
        if (supplierMapper.selectCountByParentId(String.valueOf(id)) > 0) {
            throw exception(SUPPLIER_EXITS_CHILDREN);
        }
        // 删除
        supplierMapper.deleteById(id);
    }

    private void validateSupplierExists(Long id) {
        if (supplierMapper.selectById(id) == null) {
            throw exception(SUPPLIER_NOT_EXISTS);
        }
    }

    private void validateParentSupplier(Long id, Long parentId) {
        if (parentId == null || ErpSupplierDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父ERP 供应商
        if (Objects.equals(id, parentId)) {
            throw exception(SUPPLIER_PARENT_ERROR);
        }
        // 2. 父ERP 供应商不存在
        ErpSupplierDO parentSupplier = supplierMapper.selectById(parentId);
        if (parentSupplier == null) {
            throw exception(SUPPLIER_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父ERP 供应商，如果父ERP 供应商是自己的子ERP 供应商，则报错，避免形成环路
        // id 为空，说明新增，不需要考虑环路
        if (id == null) {
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = Long.valueOf(parentSupplier.getParentId());
            if (Objects.equals(id, parentId)) {
                throw exception(SUPPLIER_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父ERP 供应商
            if (parentId == null || ErpSupplierDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentSupplier = supplierMapper.selectById(parentId);
            if (parentSupplier == null) {
                break;
            }
        }
    }

    private void validateSupplierTypeUnique(Long id, Long parentId, String type) {
        ErpSupplierDO supplier = supplierMapper.selectByParentIdAndType(parentId, type);
        if (supplier == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的ERP 供应商
        if (id == null) {
            throw exception(SUPPLIER_TYPE_DUPLICATE);
        }
        if (!Objects.equals(supplier.getId(), id)) {
            throw exception(SUPPLIER_TYPE_DUPLICATE);
        }
    }

    @Override
    public ErpSupplierDO getSupplier(Long id) {
        return supplierMapper.selectById(id);
    }

    @Override
    public List<ErpSupplierDO> getSupplierList(ErpSupplierListReqVO listReqVO) {
        return supplierMapper.selectList(listReqVO);
    }

}