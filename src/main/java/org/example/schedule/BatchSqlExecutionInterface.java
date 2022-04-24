package org.example.schedule;

import java.util.List;

/**
 * @author huangyuting
 * @Description: 批量操作SQL方法接口
 * @date 2022/4/11 14:49
 */
public interface BatchSqlExecutionInterface {

    /**
     * 批量执行的sql方法
     *
     * @param messages 批量执行的sql方法需要的参数列表
     */
    void sqlExecution(List<Object> messages);
}
