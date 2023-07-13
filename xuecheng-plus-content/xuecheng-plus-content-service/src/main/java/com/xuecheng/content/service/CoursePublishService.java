package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-02-11
 */
public interface CoursePublishService extends IService<CoursePublish> {
    /**
     * 获取课程预览信息
     * @param courseId
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    CoursePreviewDto getCoursePreview(Long courseId);

    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     */
    void commitAudit(Long companyId,Long courseId);

    /**
     *
     * @param companyId
     * @param courseId
     */
    void  publish(Long companyId,Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    void  uploadCourseHtml(Long courseId,File file);

    /**
     * 根据课程id查询课程发布信息
     * @param courseId
     * @return
     */
    CoursePublish getCoursePublish(Long courseId);

    /**
     * 根据课程id查询课程发布信息
     * @param courseId
     * @return
     */
    CoursePublish getCoursePublishCache(Long courseId);
}
