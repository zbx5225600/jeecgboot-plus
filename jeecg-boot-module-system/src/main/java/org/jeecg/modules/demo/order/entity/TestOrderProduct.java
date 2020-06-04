package org.jeecg.modules.demo.order.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 订单产品明细
 * @Author: jeecg-boot
 * @Date:   2020-04-22
 * @Version: V1.0
 */
@ApiModel(value="test_order_main对象", description="测试订单主表")
@Data
@TableName("test_order_product")
public class TestOrderProduct implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "主键")
	private String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 15)
	@ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	@ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 15)
	@ApiModelProperty(value = "更新日期")
	private Date updateTime;
	/**产品名字*/
	@Excel(name = "产品名字", width = 15)
	@ApiModelProperty(value = "产品名字")
	private String productName;
	/**价格*/
	@Excel(name = "价格", width = 15)
	@ApiModelProperty(value = "价格")
	private Double price;
	/**数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private Integer num;
	/**产品类型*/
	@Excel(name = "产品类型", width = 15, dicCode = "sex")
	@ApiModelProperty(value = "产品类型")
	private String proType;
	/**订单外键id*/
	@ApiModelProperty(value = "订单外键id")
	private String orderFkId;
	/**描述*/
	@Excel(name = "描述", width = 15)
	@ApiModelProperty(value = "描述")
	private String descc;
}
