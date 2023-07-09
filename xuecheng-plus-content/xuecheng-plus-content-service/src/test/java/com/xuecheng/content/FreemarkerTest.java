package com.xuecheng.content;

import com.alibaba.nacos.common.utils.IoUtils;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiong
 * @version 1.0
 * @description freemarker测试
 * @date 2023/7/8 16:45:48
 */
@SpringBootTest
public class FreemarkerTest {
    @Autowired
    CoursePublishService coursePublishService;

    //测试页面静态化
    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {

        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到classpath路径
        String classPath = this.getClass().getResource("/").getPath();
        //指定模板的目录
        configuration.setDirectoryForTemplateLoading(new File(classPath+"/templates"));
        //指定编码
        configuration.setDefaultEncoding("utf-8");
        //得到模板
        Template template = configuration.getTemplate("course_template.ftl");
        CoursePreviewDto coursePreviewDto= coursePublishService.getCoursePreview(134L);
        Map<String,Object> map=new HashMap<>();
        map.put("model",coursePreviewDto);
        //Template template模板, Object model数据
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //输入流
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        //输出文件
        FileOutputStream outputStream=new FileOutputStream("D:\\134.html");
        //使用流将html写入文件
        IOUtils.copy(inputStream,outputStream);
    }
}
