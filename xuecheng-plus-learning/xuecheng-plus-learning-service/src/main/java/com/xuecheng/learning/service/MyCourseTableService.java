package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;

public interface MyCourseTableService {

    /**
     * 添加我的课程记录
     * @param userId
     * @param courseId
     * @return
     */
    XcChooseCourseDto addChooseCourse(String userId,Long courseId);

    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);
}
