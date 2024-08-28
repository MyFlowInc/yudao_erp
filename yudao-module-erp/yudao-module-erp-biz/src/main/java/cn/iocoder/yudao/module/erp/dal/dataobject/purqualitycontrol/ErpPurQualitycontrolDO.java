package cn.iocoder.yudao.module.erp.dal.dataobject.purqualitycontrol;

import lombok.Data;

@Data
public class ErpPurQualitycontrolDO {

    private Long id;

    /** 节点名称 */

    private String nodeName;

    /** 检验项名称 */

    private String name;

    /** 类型 */

    private String type;

    /** 检验结果 */

    private String status;

    /** 关联采购入库单 */

    private String relatedRequisition;


}
