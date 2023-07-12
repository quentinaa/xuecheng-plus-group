package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import lombok.Data;
import lombok.ToString;

/**
 * @author xiong
 * @version 1.0
 * @description 新增大章节，小章节和修改
 * @date 2023/7/4 16:46:42
 */
@Data
@ToString
public class SaveTeachplanDto extends Teachplan {
    /**
     * 教学计划id
     */
    private Long id;
    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;
}
