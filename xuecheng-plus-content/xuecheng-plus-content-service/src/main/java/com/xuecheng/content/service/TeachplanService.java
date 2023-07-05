package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-02-11
 */
public interface TeachplanService extends IService<Teachplan> {

    /**
     * 查询课程计划信息
     * @param courseId
     * @return 课程计划详细信息
     */
     List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 保存或修改课程计划
     * @param saveTeachplanDto
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachPlanId
     */
    void delTeachPlan(Long teachPlanId);

    /**
     * 移动课程计划
     * @param move
     * @param teachPlanId
     */
    void moveTeachPlan(String move, Long teachPlanId);
}
