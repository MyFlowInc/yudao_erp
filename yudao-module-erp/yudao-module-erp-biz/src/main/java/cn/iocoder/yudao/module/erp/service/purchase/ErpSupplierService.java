package cn.iocoder.yudao.module.erp.service.purchase;

import java.util.*;

import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierListReqVO;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.ErpSupplierSaveReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;

import javax.validation.Valid;

/**
 * ERP 供应商 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpSupplierService {

    /**
     * 创建ERP 供应商
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSupplier(@Valid ErpSupplierSaveReqVO createReqVO);

    /**
     * 更新ERP 供应商
     *
     * @param updateReqVO 更新信息
     */
    void updateSupplier(@Valid ErpSupplierSaveReqVO updateReqVO);

    /**
     * 删除ERP 供应商
     *
     * @param id 编号
     */
    void deleteSupplier(Long id);

    /**
     * 获得ERP 供应商
     *
     * @param id 编号
     * @return ERP 供应商
     */
    ErpSupplierDO getSupplier(Long id);

    /**
     * 获得ERP 供应商列表
     *
     * @param listReqVO 查询条件
     * @return ERP 供应商列表
     */
    List<ErpSupplierDO> getSupplierList(@Valid ErpSupplierListReqVO listReqVO);

}