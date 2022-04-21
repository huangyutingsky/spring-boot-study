package org.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.ServerEntity;

/**
 * 
 * 
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@Mapper
public interface ServerDao extends BaseMapper<ServerEntity> {

    ServerEntity getDefaultServer();
}
