package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.MedicalFileJsonContentEntity;
import org.springframework.context.annotation.Primary;

/**
 * 报告文件json内容（json可转换成报告文件）
 *
 * @author huang
 * @date 2022-01-04 11:01:48
 */
@Primary
@Mapper
public interface MedicalFileJsonContentMapper extends BaseMapper<MedicalFileJsonContentEntity> {

}
