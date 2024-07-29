package cn.iocoder.yudao.module.erp.dal.dataobject.requisition;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 请购产品 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_requisition_product")
@KeySequence("erp_requisition_product_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionProductDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否被选中yes？no
     */
    private String selected;

    /**
     * 用途
     */
    private String useTo;
    /**
     * 关联请购单
     */
    private Long associationRequisition;

    /**
     * 备注
     */
    private String remark;

}