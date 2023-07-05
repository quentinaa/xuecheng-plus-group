package com.xuecheng.content;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程分类service测试
 * @date 2023/7/3 21:52:09
 */
@SpringBootTest
public class CourseCategoryServiceTests {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void testQueryTreeNodes(){
        List<CourseCategoryTreeDto> categoryTreeDtos=courseCategoryService.queryTreeNodes("1");
        System.out.println(categoryTreeDtos);
    }
}
