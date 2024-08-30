package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurRequisitionDO;

import java.util.List;

/**
 * @author 15276
 */
public interface ErpPurRequisitionService {
    void createErpPurRequisition(ErpPurRequisitionDO createReqVO);

    void updateErpPurRequisition(ErpPurRequisitionDO updateReqVO);

    ErpPurRequisitionDO getErpPurRequisition(Long id);

    List<ErpPurRequisitionDO> slectErpPurRequisitionList(ErpPurRequisitionDO erpPurRequisitionRespVO);
}
