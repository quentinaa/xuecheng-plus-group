package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程计划信息模型类
 * @date 2023/7/4 15:18:58
 */
@Data
public class TeachplanDto extends Teachplan {
    //小章节列表
    private List<TeachplanDto> teachPlanTreeNodes;
    //于媒资关联信息
    private TeachplanMedia teachplanMedia;
}
