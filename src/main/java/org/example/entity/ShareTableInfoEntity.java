package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分片表信息
 *
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("share_table_info")
public class ShareTableInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Integer incrNum;

    /**
     * 父表ID
     */
    private Integer parentTableId;
    /**
     * 分片表名
     */
    private String name;
    /**
     * 分片表行数
     */
    private Integer rowNum;
    /**
     * 分片表行数最大限制数
     */
    private Integer maxRows;
    /**
     * 服务ID
     */
    private Integer serverId;

    private Integer begin;

    private Integer end;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private ServerEntity serverEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShareTableInfoEntity that = (ShareTableInfoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(parentTableId, that.parentTableId) && Objects.equals(name, that.name) && Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentTableId, name, serverId);
    }
}
