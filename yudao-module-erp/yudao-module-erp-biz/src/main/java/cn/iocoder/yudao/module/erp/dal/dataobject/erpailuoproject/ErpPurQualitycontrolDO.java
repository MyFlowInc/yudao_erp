package cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 15276
 */
@Data
@TableName("pur_qualitycontrol")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPurQualitycontrolDO{
    @TableId
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

    private Long relatedRequisition;

    private String remark;
}
