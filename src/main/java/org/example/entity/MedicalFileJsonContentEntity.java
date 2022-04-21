package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 报告文件json内容（json可转换成报告文件）
 * 
 * @author huangyuting
 * @date 2022-03-22 18:40:18
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("medical_file_json_content")
public class MedicalFileJsonContentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 报告编号
	 */
	private Long reportId;
	/**
	 * 报告文件json内容
	 */
	private String fileJson;
	/**
	 * 文件类型，判断json内容转换的文件模板
	 */
	private Integer fileType;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}
