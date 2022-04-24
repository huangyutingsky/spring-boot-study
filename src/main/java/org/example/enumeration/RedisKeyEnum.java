package org.example.enumeration;

import lombok.Data;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/24 19:06
 */
public enum RedisKeyEnum {
    CREATE_TABLE_LOCK("createTableLock:%s", "创建表锁"),
    TABLE_ROWS_RECORD("table:rows:%s:%s", "记录表行数"),
    TABLE_UUID("table:uuid:%s", "表唯一ID计数器");

    RedisKeyEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    /**
     * sql
     */
    private String key;
    /**
     * 描述信息
     */
    private String desc;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
