package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.erp.controller.admin.erpailuoproject.vo.ErpPurQualitycontrolRespVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurQualitycontrolDO;
import cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject.ErpPurQualitycontrolMapper;
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
public class ErpPurQualitycontrolServiceImpl implements ErpPurQualitycontrolService {
    @Resource
    private ErpPurQualitycontrolMapper erpPurQualitycontrolMapper;
    @Override
    public void createErpPurQualitycontrol(ErpPurQualitycontrolRespVO createReqVO) {
        erpPurQualitycontrolMapper.insert(BeanUtils.toBean(createReqVO, ErpPurQualitycontrolDO.class));
    }

    @Override
    public void updateErpPurQualitycontrol(ErpPurQualitycontrolRespVO updateReqVO) {
        erpPurQualitycontrolMapper.updateById(BeanUtils.toBean(updateReqVO, ErpPurQualitycontrolDO.class));
    }
    @Override
    public ErpPurQualitycontrolDO getErpPurQualitycontrol(Long id) {
        return erpPurQualitycontrolMapper.selectById(id);
    }
    @Override
    public List<ErpPurQualitycontrolDO> slectErpPurQualitycontrolList(ErpPurQualitycontrolRespVO erpPurQualitycontrolRespVO) {
        return erpPurQualitycontrolMapper.selectPurQualitycontrolList(BeanUtils.toBean(erpPurQualitycontrolRespVO, ErpPurQualitycontrolDO.class));
    }

}
