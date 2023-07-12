package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/11 15:44
 */
@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseService courseBaseService;


    @ApiOperation("课程分页查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")//指定权限标识符,拥有此权限才可访问
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required=false) QueryCourseParamsDto queryCourseParamsDto) {
        //获取当前用户的身份
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(principal);
        SecurityUtil.XcUser user=SecurityUtil.getUser();
        Long companyId = null;
        if (StringUtils.isNotEmpty(user.getCompanyId())){
            companyId=Long.parseLong(user.getCompanyId());
        }
        return courseBaseService.queryCourseBaseList(companyId,pageParams,queryCourseParamsDto);
    }

    @ApiOperation("课程新增接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;

        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

    @ApiOperation("根据id查询课程接口")
    @GetMapping ("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId ){
        return courseBaseService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("课程修改接口")
    @PutMapping ("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto){
        Long companyId = 1232141425L;
        return courseBaseService.updateCourseBase(companyId,editCourseDto);
    }

    @ApiOperation("课程删除接口")
    @DeleteMapping("/course/{courseId}")
    public void delCourseBase(@PathVariable Long courseId){
        Long companyId = 1232141425L;
        courseBaseService.delCourseBase(companyId,courseId);
    }

}
