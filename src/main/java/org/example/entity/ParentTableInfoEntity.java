package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * 
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@Data
@TableName("parent_table_info")
public class ParentTableInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 父表名
	 */
	private String name;

	private String strategy;

	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 分片表信息列表
	 */
	@TableField(exist = false)
	private List<ShareTableInfoEntity> shareTableInfos;
}
