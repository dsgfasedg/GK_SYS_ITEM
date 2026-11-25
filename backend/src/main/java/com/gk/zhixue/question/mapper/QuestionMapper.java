package com.gk.zhixue.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.zhixue.question.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
