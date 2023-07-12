package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CourseIndex;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author xiong
 * @version 1.0
 * @description 课程发布的任务类
 * @date 2023/7/8 15:56:36
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;
    @Autowired
    private SearchServiceClient searchServiceClient;
    @Autowired
    private CoursePublishMapper coursePublishMapper;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数
        //调用抽象类的方法执行
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    //执行课程发布任务的逻辑
    @Override
    public boolean execute(MqMessage mqMessage) {

        //课程id
        long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        //课程静态化上传到minio
        generateCourseHtml(mqMessage, courseId);

        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage, courseId);

        //向redis写缓存

        //表示任务完成
        return true;
    }

    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            log.debug("课程索引信息已完成，无需处理");
        }
        //查询课程信息，调用搜索服务添加索引
        //从课程发布表查询信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex=new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //远程调用
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add){
            XueChengPlusException.cast("添加索引失败");
        }
        //完成本阶段任务
        mqMessageService.completedStageTwo(courseId);
    }

    private void generateCourseHtml(MqMessage mqMessage, long courseId) {

        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne == 1){
            log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
            return ;
        }

        //生成静态化页面
        File file = coursePublishService.generateCourseHtml(courseId);
        //上传静态化页面
        if(file!=null){
            coursePublishService.uploadCourseHtml(courseId,file);
        }
        //保存第一阶段状态
        mqMessageService.completedStageOne(id);
    }
}
