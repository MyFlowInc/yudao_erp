package cn.iocoder.yudao.module.erp.dal.dataobject.requisition;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 新增请购 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_purchase_requisition")
@KeySequence("erp_purchase_requisition_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequisitionDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 请购编号
     */
    private String requisitionCode;
    /**
     * 请购类型
     */
    private String requisitionType;
    /**
     * 请购单时间
     */
    private LocalDateTime requisitionTime;
    /**
     * 预计交期
     */
    private LocalDateTime estimatedTime;
    /**
     * 关联项目
     */
    private String associationProject;
    /**
     * 附件
     */
    private String annex;
    /**
     * 审核类型
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}