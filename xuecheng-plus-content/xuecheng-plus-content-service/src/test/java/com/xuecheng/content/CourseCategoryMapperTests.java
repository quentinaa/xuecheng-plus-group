package com.xuecheng.content;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程分类测试类
 * @date 2023/7/3 20:52:07
 */
@SpringBootTest
public class CourseCategoryMapperTests {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryMapper(){
        List<CourseCategoryTreeDto> selectedTreeNodes=courseCategoryMapper.selectTreeNodes("1");
        System.out.println(selectedTreeNodes);
    }
}
