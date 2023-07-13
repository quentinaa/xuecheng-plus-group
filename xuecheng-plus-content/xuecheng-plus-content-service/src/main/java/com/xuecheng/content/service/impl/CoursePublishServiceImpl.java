package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.*;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseTeacherService courseTeacherService;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private MediaServiceClient mediaServiceClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public CoursePreviewDto getCoursePreview(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        List<CourseTeacher> courseTeacherList = courseTeacherService.selectTeacherInfo(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanTree);
        coursePreviewDto.setTeachers(courseTeacherList);
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.getCourseBaseInfo(courseId);
        if (courseBaseInfoDto == null) {
            XueChengPlusException.cast("找不到课程");
        }
        //课程审核状态
        String auditStatus = courseBaseInfoDto.getAuditStatus();
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if (!courseBaseInfoDto.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其它机构的课程。");
        }
        //课程图片是否填写
        if (StringUtils.isEmpty(courseBaseInfoDto.getPic())) {
            XueChengPlusException.cast("提交失败，请上传课程图片");
        }
        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //课程基本信息加部分营销信息
        BeanUtils.copyProperties(courseBaseInfoDto, coursePublishPre);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);
        //查询课程计划信息
        List<TeachplanDto> teachplanDtoList = teachplanService.findTeachplanTree(courseId);
        if (teachplanDtoList.size() == 0) {
            XueChengPlusException.cast("提交失败，还没有添加课程计划");
        }
        //转json
        String teachplanJson = JSON.toJSONString(teachplanDtoList);
        coursePublishPre.setTeachplan(teachplanJson);
        //查询课程教师
        List<CourseTeacher> courseTeacherList = courseTeacherService.selectTeacherInfo(courseId);
        if (courseTeacherList.size() == 0) {
            XueChengPlusException.cast("提交失败，还没有添加课程教师");
        }
        //转json
        String courseTeacherJson = JSON.toJSONString(courseTeacherList);
        coursePublishPre.setTeachers(courseTeacherJson);
        //设置预发布记录状态,已提交
        coursePublishPre.setStatus("202003");
        //教学机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本信息表的审核状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);

    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {

        //查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            System.err.println(coursePublishPre.getCompanyId() + "---" + companyId);
            XueChengPlusException.cast("不允许提交其它机构的课程。");
        }
        //审核状态
        String status = coursePublishPre.getStatus();
        //审核通过方可发布
        if (!"202004".equals(status)) {
            XueChengPlusException.cast("操作失败，课程审核通过方可发布。");
        }
        //保存课程发布信息
        saveCoursePublish(courseId, coursePublishPre);
        //保存消息表
        saveCoursePublishMessage(courseId);
        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) {

        Configuration configuration = new Configuration(Configuration.getVersion());
        File htmlFile = null;
        try {
            //拿到classpath路径
            String classPath = this.getClass().getResource("/").getPath();
            //指定模板的目录
            configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));
            //指定编码
            configuration.setDefaultEncoding("utf-8");
            //得到模板
            Template template = configuration.getTemplate("course_template.ftl");
            CoursePreviewDto coursePreviewDto = this.getCoursePreview(courseId);
            Map<String, CoursePreviewDto> map = new HashMap<>();
            map.put("model", coursePreviewDto);
            //Template template模板, Object model数据
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            //创建静态化文件
            htmlFile = File.createTempFile("course", ".html");
            log.debug("课程静态化，生成静态文件:{}", htmlFile.getAbsolutePath());
            //输出文件
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            //使用流将html写入文件
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("页面静态化出现问题，课程id：{}", courseId, e);
            XueChengPlusException.cast("课程静态化异常");
        }

        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.upload(multipartFile, "course/" + courseId + ".html");
        if (course == null) {
            XueChengPlusException.cast("上传静态文件异常");
        }
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        return coursePublishMapper.selectById(courseId);
    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {

        Object jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
        if (jsonObj != null) {
            String jsonString = jsonObj.toString();
            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            synchronized (this) {
                //再查讯缓存防止高并发下后面线程再次查询数据库
                jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
                if (jsonObj != null) {
                    String jsonString = jsonObj.toString();
                    CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
                    return coursePublish;
                }
                //数据库查
                CoursePublish coursePublish = getCoursePublish(courseId);
                redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSON(coursePublish), 30 + new Random().nextInt(100), TimeUnit.SECONDS);
                return coursePublish;
            }
        }


    }

    /**
     * 保存消息表记录，稍后实现
     *
     * @param courseId
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }

    /**
     * 保存课程发布信息
     *
     * @param courseId
     */
    private void saveCoursePublish(Long courseId, CoursePublishPre coursePublishPre) {
        CoursePublish coursePublish = new CoursePublish();
        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }
}
