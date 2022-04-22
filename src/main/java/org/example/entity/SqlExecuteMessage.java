package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/22 11:26
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SqlExecuteMessage {

    /**
     * 操作实体
     */
    private UserEntity user;

    /**
     * 父表名
     */
    private String parentTableName;
}
