package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> selectTeacherInfo(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher saveCourseTeacher(SaveTeacherDto saveTeacherDto) {

        if (StringUtils.isBlank(saveTeacherDto.getTeacherName())){
            XueChengPlusException.cast("教师姓名不能为空");
        }
        if (StringUtils.isBlank(saveTeacherDto.getPosition())){
            XueChengPlusException.cast("教师职位不能为空");
        }
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(saveTeacherDto.getId());
        if (courseTeacher==null){
            courseTeacher=new CourseTeacher();
            BeanUtils.copyProperties(saveTeacherDto,courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert<=0){
                XueChengPlusException.cast("添加教师信息失败");
            }
        }else {
            BeanUtils.copyProperties(saveTeacherDto,courseTeacher);
            int insert = courseTeacherMapper.updateById(courseTeacher);
            if (insert<=0){
                XueChengPlusException.cast("修改教师信息失败");
            }
        }

        return courseTeacherMapper.selectById(courseTeacher.getId());
    }


    @Override
    public void delCourseTeacher(Long courseId, Long id) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper=queryWrapper.eq(CourseTeacher::getCourseId,courseId).eq(CourseTeacher::getId,id);
        courseTeacherMapper.delete(queryWrapper);

    }

}
