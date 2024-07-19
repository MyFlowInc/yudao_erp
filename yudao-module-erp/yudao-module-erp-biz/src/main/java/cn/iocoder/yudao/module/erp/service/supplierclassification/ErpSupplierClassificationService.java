package cn.iocoder.yudao.module.erp.service.supplierclassification;

import java.util.*;
import cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification.ErpSupplierClassificationDO;
import javax.validation.Valid;

/**
 * 供应商分类 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpSupplierClassificationService {

    /**
     * 创建供应商分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSupplierClassification(@Valid ErpSupplierClassificationSaveReqVO createReqVO);

    /**
     * 更新供应商分类
     *
     * @param updateReqVO 更新信息
     */
    void updateSupplierClassification(@Valid ErpSupplierClassificationSaveReqVO updateReqVO);

    /**
     * 删除供应商分类
     *
     * @param id 编号
     */
    void deleteSupplierClassification(Long id);

    /**
     * 获得供应商分类
     *
     * @param id 编号
     * @return 供应商分类
     */
    ErpSupplierClassificationDO getSupplierClassification(Long id);

    /**
     * 获得供应商分类列表
     *
     * @param listReqVO 查询条件
     * @return 供应商分类列表
     */
    List<ErpSupplierClassificationDO> getSupplierClassificationList(ErpSupplierClassificationListReqVO listReqVO);

}