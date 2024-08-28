package cn.iocoder.yudao.module.erp.service.purqualitycontrol;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.erp.controller.admin.purqualitycontrol.vo.ErpPurQualitycontrolRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purqualitycontrol.ErpPurQualitycontrolDO;

import java.util.Collection;
import java.util.List;

public interface ErpPurQualitycontrolService {

    void createErpPurQualitycontrol(ErpPurQualitycontrolRespVO createReqVO);

    void updateErpPurQualitycontrol(ErpPurQualitycontrolRespVO updateReqVO);

    ErpPurQualitycontrolDO getErpPurQualitycontrol(Long id);

    List<ErpPurQualitycontrolDO> slectErpPurQualitycontrolList(ErpPurQualitycontrolRespVO erpPurQualitycontrolRespVO);
}
