package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurRequisitionDO;
import cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject.ErpPurRequisitionMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 15276
 */
@Service
@Validated
@DS("ailuo")
public class ErpPurRequisitionServiceImpl implements ErpPurRequisitionService {
    @Resource
    private ErpPurRequisitionMapper erpPurRequisitionMapper;

    @Override
    public void createErpPurRequisition(ErpPurRequisitionDO createReqVO) {
        erpPurRequisitionMapper.insert(createReqVO);
    }
    @Override
    public void updateErpPurRequisition(ErpPurRequisitionDO updateReqVO) {
        erpPurRequisitionMapper.updateById(updateReqVO);
    }
    @Override
    public ErpPurRequisitionDO getErpPurRequisition(Long id) {
        return erpPurRequisitionMapper.selectById(id);
    }
    @Override
    public List<ErpPurRequisitionDO> slectErpPurRequisitionList(ErpPurRequisitionDO erpPurRequisitionRespVO) {
        return erpPurRequisitionMapper.selectErpPurRequisitionList(erpPurRequisitionRespVO);
    }
}
