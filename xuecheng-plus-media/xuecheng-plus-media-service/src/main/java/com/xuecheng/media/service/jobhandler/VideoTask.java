package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xiong
 * @version 1.0
 * @description 视频处理任务类
 * @date 2023/7/7 11:20:14
 */
@Slf4j
@Component
public class VideoTask {
    @Autowired
    private MediaProcessService mediaProcessService;
    @Autowired
    private MediaFileService mediaFileService;
    //ffmpeg的路径
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpeg_path;


    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        //获取cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();

        //查询待处理任务
        List<MediaProcess> mediaProcessList = mediaProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        //任务数量
        int size = mediaProcessList.size();
        log.debug("取到的任务数:" + size);
        if (size == 0) {
            return;
        }
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {


                    //任务id
                    Long taskId = mediaProcess.getId();
                    //开启任务
                    boolean b = mediaProcessService.startTask(taskId);
                    if (!b) {
                        return;
                    }
                    //桶
                    String bucket = mediaProcess.getBucket();
                    //存储路径
                    String filePath = mediaProcess.getFilePath();
                    //原始视频的md5值
                    String fileId = mediaProcess.getFileId();
                    //原始文件名称
                    String filename = mediaProcess.getFilename();
                    //执行视频转码
                    //下载minio视频到本地
                    File originalFile = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                    if (originalFile == null) {
                        log.debug("下载待处理文件失败,File:{}", bucket.concat(filePath));
                        //保存失败结果
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载待处理文件失败");
                        return;
                    }
                    //源avi视频的路径
                    String video_path = originalFile.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    //先创建一个临时文件，作为转换后的文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.error("创建mp4临时文件失败");
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "创建mp4临时文件失败");
                        return;
                    }
                    //视频处理结果
                    String result = "";
                    try {
                        //开始处理视频
                        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4File.getAbsolutePath());
                        //开始视频转换，成功将返回success
                        result = videoUtil.generateMp4();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                    }
                    if (!result.equals("success")) {
                        //记录错误信息
                        log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                        mediaProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, result);
                        return;
                    }
                    //上传到minio
                    //mp4在minio的存储路径
                    String objectName = getFilePath(fileId, ".mp4");
                    //访问url
                    String url = "/" + bucket + "/" + objectName;
                    try {
                        mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
                        //将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
                        mediaProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, null);
                    } catch (Exception e) {
                        log.error("上传视频失败或入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
                        //最终还是失败了
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "处理后视频上传或入库失败");
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        ////等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}
