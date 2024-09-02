package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurItemDO;
import cn.iocoder.yudao.module.erp.dal.mysql.erpailuoproject.ErpPurItemMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.annotation.Resource;
import java.util.List;
/**
 * @author 15276
 */
@Validated
@Service
@DS("ailuo")
public class ErpPurItemServiceImpl implements ErpPurItemService {
    @Resource
    private ErpPurItemMapper erpPurItemMapper;

    @Override
    public void updateErpPurItem(ErpPurItemDO updateReqDTO) {
        erpPurItemMapper.updateById(updateReqDTO);
    }
    @Override
    public ErpPurItemDO getErpPurItem(Integer id) {
        return erpPurItemMapper.selectById(id);
    }
    @Override
    public List<ErpPurItemDO> selectErpPurItemList(ErpPurItemDO updateReqDTO) {
        return erpPurItemMapper.selectErpPurItemList(updateReqDTO);
    }

}
