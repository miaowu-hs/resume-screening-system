package com.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.resume.entity.MatchRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 匹配记录 Mapper
 */
@Mapper
public interface MatchRecordMapper extends BaseMapper<MatchRecord> {
}
