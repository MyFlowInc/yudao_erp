package cn.iocoder.yudao.module.erp.service.productbatch;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * ERP产品批次信息 Service 接口
 *
 * @author 那就这样吧
 */
public interface ErpProductBatchService {

    /**
     * 创建ERP产品批次信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProductBatch(@Valid ErpProductBatchSaveReqVO createReqVO);

    /**
     * 更新ERP产品批次信息
     *
     * @param updateReqVO 更新信息
     */
    void updateProductBatch(@Valid ErpProductBatchSaveReqVO updateReqVO);

    /**
     * 删除ERP产品批次信息
     *
     * @param id 编号
     */
    void deleteProductBatch(Long id);

    /**
     * 获得ERP产品批次信息
     *
     * @param id 编号
     * @return ERP产品批次信息
     */
    ErpProductBatchDO getProductBatch(Long id);

    /**
     * 获得ERP产品批次信息分页
     *
     * @param pageReqVO 分页查询
     * @return ERP产品批次信息分页
     */
    PageResult<ErpProductBatchDO> getProductBatchPage(ErpProductBatchPageReqVO pageReqVO);
}