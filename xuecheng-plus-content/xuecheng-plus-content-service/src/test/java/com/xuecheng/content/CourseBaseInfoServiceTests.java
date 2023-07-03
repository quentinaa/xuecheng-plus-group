package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/3 16:45:08
 */
@SpringBootTest
public class CourseBaseInfoServiceTests {
    @Autowired
    private CourseBaseService courseBaseService;

    @Test
    void queryCourseBaseList(){
        QueryCourseParamsDto queryCourseParamsDto=new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        PageParams pageParams=new PageParams();
        PageResult<CourseBase> courseBasePageResult= courseBaseService.queryCourseBaseList(pageParams,queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }
}
