package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.module.erp.controller.admin.erpailuoproject.vo.ErpPurQualitycontrolRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurQualitycontrolDO;

import java.util.List;

/**
 * @author 15276
 */
public interface ErpPurQualitycontrolService {

    void createErpPurQualitycontrol(ErpPurQualitycontrolRespVO createReqVO);

    void updateErpPurQualitycontrol(ErpPurQualitycontrolRespVO updateReqVO);

    ErpPurQualitycontrolDO getErpPurQualitycontrol(Long id);

    List<ErpPurQualitycontrolDO> slectErpPurQualitycontrolList(ErpPurQualitycontrolRespVO erpPurQualitycontrolRespVO);
}
