package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/3 11:19:42
 */
@SpringBootTest
public class CourseBaseMapperTest {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(74L);
        Assertions.assertNotNull(courseBase);

    }
}
