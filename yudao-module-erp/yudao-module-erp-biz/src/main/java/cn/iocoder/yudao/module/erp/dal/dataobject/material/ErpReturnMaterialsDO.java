package cn.iocoder.yudao.module.erp.dal.dataobject.material;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * ERP 还料入库单 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_return_materials")
@KeySequence("erp_return_materials_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpReturnMaterialsDO extends BaseDO {

    /**
     * 还料单编号
     */
    @TableId
    private Long id;
    /**
     * 还料单号
     */
    private String no;
    /**
     * 供应商编号
     */
    private Long supplierId;
    /**
     * 关联请购单
     */
    private Long associationRequisitionId;
    /**
     * 关联项目id
     */
    private Long associationProjectId;
    /**
     * 关联领料单
     */
    private Long associationPickingId;
    /**
     * 入库时间
     */
    private LocalDateTime inTime;
    /**
     * 合计数量
     */
    private BigDecimal totalCount;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件 URL
     */
//    private String fileUrl;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fileUrl;
}