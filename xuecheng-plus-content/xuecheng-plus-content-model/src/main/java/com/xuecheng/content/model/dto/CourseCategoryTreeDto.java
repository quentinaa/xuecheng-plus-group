package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description课程分类树型结点dto
 * @date 2023/7/3 20:11:17
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //子节点
    private List<CourseCategoryTreeDto> childrenTreeNodes;
}
