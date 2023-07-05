package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-02-11
 */
public interface CourseTeacherService extends IService<CourseTeacher> {


    /**
     * 查询相关课程的教师
     * @param courseId
     * @return
     */
    List<CourseTeacher> selectTeacherInfo(Long courseId);

    /**
     * 添加或修改教师信息
     * @param saveTeacherDto
     * @return教师信息
     */
    CourseTeacher saveCourseTeacher(SaveTeacherDto saveTeacherDto);


    /**
     * 删除教师
     * @param courseId
     * @param id
     */
    void delCourseTeacher(Long courseId, Long id);
}
