package org.example.dao;

import org.example.entity.UserEntity;
import org.example.enumeration.RedisKeyEnum;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/4/24 18:45
 */
@Component
public class UserDao extends BaseDao<UserEntity> {

    public UserDao() {
        this.parentTable = "user";
        this.sqlQueue = "queue:insertUserQueue";
    }

    @Override
    void parseObjToPreparedStatement(PreparedStatement preStatement, UserEntity data) throws SQLException {
        // TODO: 2022/4/24 优化
        final String key = String.format(RedisKeyEnum.TABLE_UUID.getKey(), this.parentTable);
        String uuid = Objects.requireNonNull(redisTemplate.opsForValue().increment(key)).toString();
        preStatement.setInt(1, Integer.parseInt(uuid));
        preStatement.setString(2, data.getUserName());
        preStatement.setString(3, data.getNickName());
        preStatement.setString(4, data.getPasswd());
        preStatement.setString(5, data.getDepartment());
        preStatement.setString(6, data.getPhone());
        preStatement.setString(7, data.getName());
        preStatement.setInt(8, data.getState());
        preStatement.setTimestamp(9, new Timestamp(data.getLastTime().getTime()));
        preStatement.setTimestamp(10, new Timestamp(data.getUpdatedTime().getTime()));
        preStatement.setTimestamp(11, new Timestamp(data.getCreatedTime().getTime()));
        preStatement.setString(12, data.getSalt());
        preStatement.setBoolean(13, data.getPwdIsChanged());
    }
}
