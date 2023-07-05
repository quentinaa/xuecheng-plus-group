package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程教师相关接口
 * @date 2023/7/4 20:51:41
 */
@Api(value = "课程教师管理接口",tags = "课程教师管理接口")
@RestController
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;

    //查询教师信息
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryTeacherInfo(@PathVariable Long courseId){
       return courseTeacherService.selectTeacherInfo(courseId);
    }

    //添加教师信息
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody SaveTeacherDto saveTeacherDto){
       return courseTeacherService.saveCourseTeacher(saveTeacherDto);
    }
    //删除教师信息
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void delCourseTeacher(@PathVariable Long courseId,@PathVariable Long id){
        courseTeacherService.delCourseTeacher(courseId,id);
    }
}
