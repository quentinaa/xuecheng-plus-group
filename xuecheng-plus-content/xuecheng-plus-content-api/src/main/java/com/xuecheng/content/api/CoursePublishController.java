package com.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程发布控制器
 * @date 2023/7/7 20:52:53
 */
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
@Controller
public class CoursePublishController {

    @Autowired
    private CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        ModelAndView modelAndView = new ModelAndView();
        //查询课程信息
        CoursePreviewDto coursePreviewInfo  = coursePublishService.getCoursePreview(courseId);
        modelAndView.addObject("model",coursePreviewInfo );
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);
    }
    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping ("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId){
       return coursePublishService.getCoursePublish(courseId);
    }

    @ApiOperation("获取课程分布相关信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublishWhole(@PathVariable("courseId") Long courseId) {
        //查询课程发布
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        //封装数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        if (coursePublish==null){
            return coursePreviewDto;
        }
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程计划信息
        String teachplanJson = coursePublish.getTeachplan();
        //转成list
        List<TeachplanDto> teachplanDtoList = JSON.parseArray(teachplanJson, TeachplanDto.class);
        //教师信息
        String teachersJson = coursePublish.getTeachers();
        List<CourseTeacher> courseTeacherList = JSON.parseArray(teachersJson, CourseTeacher.class);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachers(courseTeacherList);
        coursePreviewDto.setTeachplans(teachplanDtoList);
        return coursePreviewDto;
    }

}
