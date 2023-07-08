package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程预览数据模型
 * @date 2023/7/7 21:43:28
 */
@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息，营销信息
    private CourseBaseInfoDto courseBase;

    //课程计划信息
   private List<TeachplanDto> teachplans;
}
