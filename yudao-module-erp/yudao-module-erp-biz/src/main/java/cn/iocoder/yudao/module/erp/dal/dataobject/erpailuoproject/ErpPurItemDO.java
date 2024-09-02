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
@TableName("pur_item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPurItemDO{

    @TableId
    private Long id;

    /** 序号 */

    private String number;

    /** 物料名称 */

    private String name;

    /** 类型 */

    private String type;

    /** 状态 */

    private String status;

    /** 描述 */

    private String desc;

    /** 规格 */

    private String specifications;

    /** 材质/品牌 */

    private String brand;

    /** 单位 */

    private String unit;

    /** 采购数量 */

    private String quantity;

    /** 订单/使用部门 */

    private String orderDepartment;

    /** 用途 */

    private String purpose;

    /** 关联项目 */

    private Long relationProject;

    /** 关联请购 */

    private Long relationRequisition;

    /** 来料检完成时间 */

    private String incomingCompletiontime;

    /** 入库完成时间 */

    private String warehousingCompletiontime;

    /** 是否入库？yes/or */

    private String warehousing;

    private String remark;

}
