package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/8 19:47:02
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable cause) {
        return (filedata, objectName) -> {
            //降级方法
            log.error("调用媒资管理服务上传文件时发生熔断，异常信息:{}",cause.toString(),cause);
            return null;
        };
    }
}
