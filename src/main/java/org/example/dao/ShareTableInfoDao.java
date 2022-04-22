package org.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.ServerEntity;
import org.example.entity.ShareTableInfoEntity;

import java.util.List;

/**
 * 分片表信息
 * 
 * @author huangyuting
 * @email ${email}
 * @date 2022-04-20 18:32:01
 */
@Mapper
public interface ShareTableInfoDao extends BaseMapper<ShareTableInfoEntity> {
	List<ShareTableInfoEntity> getShareTableInfoByParentTableName(@Param("parentTableName") String parentTableName);

    ShareTableInfoEntity getLatestShareTableInfoByParentTableName(String parentTable);

    void incrRowNumById(Integer id, Integer increment);

    Integer countShareTableNum(@Param("parentTableName") String parentTableName);
}
