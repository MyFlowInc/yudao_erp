package cn.iocoder.yudao.module.erp.dal.dataobject.erpailuoproject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author 15276
 */
@Data
@TableName("pur_requisition")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPurRequisitionDO{
    @TableId
    private Long id;

    /** 请购类型 */

    private String type;

    /** 请购单状态 */

    private String status;

    /** 关联项目名称 */

    private String projectName;

    /** 请购人 */

    private String requestor;

    /** 申请时间 */

    private String applicationDate;

    /** 规格 */

    private String specifications;

    /** 唯一标识 */

    private String uuid;

    /** 编号 */

    private String code;

    /** 来料检完成时间 */

    private String checkCompletiontime;

    /** 预计交期 */

    private String expectedDeliverytime;

    /** 重要事件 */

    private String important;

    /** 关联项目id */

    private Long relationProject;

    /** 关联车间id */

    private Long relationWorkshop;

    private String remark;
}
