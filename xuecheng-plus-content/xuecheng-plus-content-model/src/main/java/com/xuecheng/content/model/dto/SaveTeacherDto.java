package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author xiong
 * @version 1.0
 * @description 添加课程教师模型类
 * @date 2023/7/4 21:14:35
 */
@Data
public class SaveTeacherDto {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "课程id")
    private Long courseId;

    @NotEmpty(message = "新增教师名称不能为空",groups = {ValidationGroups.Inster.class})
    @NotEmpty(message = "修改教师名称不能为空",groups = {ValidationGroups.Update.class})
    @ApiModelProperty(value = "教师名称")
    private String teacherName;

    @NotEmpty(message = "新增教师职位不能为空",groups = {ValidationGroups.Inster.class})
    @NotEmpty(message = "修改教师职位不能为空",groups = {ValidationGroups.Update.class})
    @ApiModelProperty(value = "教师职位")
    private String position;

    @ApiModelProperty(value = "教师简介")
    private String introduction;

    @ApiModelProperty(value = "教师照片")
    private String photograph;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;
}
