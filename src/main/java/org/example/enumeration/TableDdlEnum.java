package org.example.enumeration;

import lombok.Data;

/**
 * @author huangyuting
 * @Description: 表DDL枚举类
 * @date 2022/4/21 19:41
 */
public enum TableDdlEnum {

    /**
     * 用户表
     */
    USER("CREATE TABLE `{table}` (\n" +
            "  `id` int NOT NULL AUTO_INCREMENT COMMENT 'primary key',\n" +
            "  `user_name` varchar(20) DEFAULT NULL COMMENT '用户名',\n" +
            "  `nick_name` varchar(20) DEFAULT NULL COMMENT '昵称',\n" +
            "  `passwd` varchar(64) DEFAULT NULL,\n" +
            "  `department` varchar(30) DEFAULT NULL COMMENT '部门',\n" +
            "  `phone` varchar(20) DEFAULT NULL COMMENT '电话',\n" +
            "  `name` varchar(20) DEFAULT NULL COMMENT '姓名',\n" +
            "  `state` tinyint DEFAULT NULL COMMENT '账号状态 0-停用 1-使用中',\n" +
            "  `last_time` datetime DEFAULT NULL COMMENT '最后一次登陆时间',\n" +
            "  `created_time` datetime DEFAULT NULL COMMENT 'created time',\n" +
            "  `updated_time` datetime DEFAULT NULL COMMENT 'updated time',\n" +
            "  `salt` varchar(32) DEFAULT NULL COMMENT '盐值',\n" +
            "  `pwd_is_changed` bit(1) DEFAULT b'0' COMMENT '初始密码是否已改',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `user_user_name_uindex` (`user_name`),\n" +
            "  UNIQUE KEY `phone` (`phone`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;", "用户表");

    TableDdlEnum(String ddl, String desc) {
        this.ddl = ddl;
        this.desc = desc;
    }

    /**
     * 生成表DDL语句
     */
    private String ddl;
    /**
     * 描述信息
     */
    private String desc;

    public String getDdl() {
        return ddl;
    }

    public String getDesc() {
        return desc;
    }
}
