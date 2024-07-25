package cn.iocoder.yudao.module.erp.service.purchase;

import javax.validation.*;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpSupplierDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

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
     * 获得供应商列表
     *
     * @param ids 编号列表
     * @return 供应商列表
     */
    List<ErpSupplierDO> getSupplierList(Collection<Long> ids);
    /**
     * 获得供应商 Map
     *
     * @param ids 编号列表
     * @return 供应商 Map
     */
    default Map<Long, ErpSupplierDO> getSupplierMap(Collection<Long> ids) {
        return convertMap(getSupplierList(ids), ErpSupplierDO::getId);
    }
    List<ErpSupplierDO> getSupplierListByStatus(Integer status);
    /**
     * 获得ERP 供应商分页
     *
     * @param pageReqVO 分页查询
     * @return ERP 供应商分页
     */
    PageResult<ErpSupplierDO> getSupplierPage(ErpSupplierPageReqVO pageReqVO);

    /**
     * 获得供应商已开启列表
     *
     * @param status 编号状态
     * @return 供应商列表
     */
    List<ErpSupplierDO> selectListByStatus(Integer status);

}