package cn.iocoder.yudao.module.erp.service.erpailuoproject;

import cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject.ErpPurItemDO;

import java.util.List;

/**
 * @author 15276
 */
public interface ErpPurItemService {
    void updateErpPurItem(ErpPurItemDO updateReqDTO);

    ErpPurItemDO getErpPurItem(Integer id);
    List<ErpPurItemDO> selectErpPurItemList(ErpPurItemDO updateReqDTO);
}
