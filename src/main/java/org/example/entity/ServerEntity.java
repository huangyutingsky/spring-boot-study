package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@Data
@TableName("server")
public class ServerEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 服务IP
	 */
	private String ip;
	/**
	 * 服务端口
	 */
	private Integer port;
	/**
	 * 是否是master
	 */
	private Boolean ismaster;
	/**
	 * 是否可读
	 */
	private Boolean canRead;
	/**
	 * 是否可写
	 */
	private Boolean canWrite;
	/**
	 * 服务状态
	 */
	private Integer state;
	/**
	 * 负载服务ID列表
	 */
	private String loadServerIds;
	/**
	 * 数据源名称
	 */
	private String dsName;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
