package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author xiong
 * @version 1.0
 * @description 学习过程管理service实现
 * @date 2023/7/12 15:53:42
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTableService myCourseTableService;
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    MediaServiceClient mediaServiceClient;
    
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish==null){
            return RestResponse.validfail("课程信息不存在");
        }
        //根据课程发布表判断是否支持试学（is_preview值为1表示支持试学）
        String teachplanJson = coursepublish.getTeachplan();
        List<Teachplan> teachplans = JSON.parseArray(teachplanJson, Teachplan.class);
        for (Teachplan teachplan:teachplans){
            if (teachplan.getId().equals(teachplanId)&&teachplan.getIsPreview().equals("1")){
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }
        //用户已登录
        if (StringUtils.isNotEmpty(userId)){
            //获取学习资格
            XcCourseTablesDto learningStatus = myCourseTableService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if (learnStatus.equals("702002")){
                return RestResponse.validfail("无法学习，因为没有选课或选课后灭有支付");
            }else if (learnStatus.equals("702003")){
                return RestResponse.validfail("已过期需要申请或重新支付");
            }else {
                //有资格学习，远程调用媒资获取视频地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }
        //如果用户没有登录
        //取出课程收费规则
        String charge = coursepublish.getCharge();
        if ("201000".equals(charge)){
            //返回视频地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }

        return RestResponse.validfail("该课程需要购买");
    }
}
