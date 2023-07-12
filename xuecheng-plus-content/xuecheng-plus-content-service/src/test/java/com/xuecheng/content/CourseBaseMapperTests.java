package com.xuecheng.content;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 9:24
 */
@SpringBootTest
public class CourseBaseMapperTests {
    @Resource
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);
        //查询条件
        QueryCourseParamsDto courseParamsDto=new QueryCourseParamsDto();
        courseParamsDto.setCourseName("java");
        //拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper=new LambdaQueryWrapper<>();
        //根据名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),CourseBase::getName,courseParamsDto.getCourseName());
        //根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,courseParamsDto.getAuditStatus());
        //定义分页参数
        PageParams pageParams=new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);
        //创建page分页参数对象
        Page<CourseBase> page = new Page<>(1,2);
        //开始进行分页查询
        Page<CourseBase> pageResult=courseBaseMapper.selectPage(page,queryWrapper);
        //数据列表
        List<CourseBase> items = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println(courseBasePageResult);
    }
}
