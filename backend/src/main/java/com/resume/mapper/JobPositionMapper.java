package com.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.resume.entity.JobPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位 Mapper
 */
@Mapper
public interface JobPositionMapper extends BaseMapper<JobPosition> {
}
