package org.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.ParentTableInfoEntity;

/**
 * 
 * 
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@Mapper
public interface ParentTableInfoDao extends BaseMapper<ParentTableInfoEntity> {
 	ParentTableInfoEntity selectByName(@Param("name") String tableName);

	void updateIncrNumMax();
}
