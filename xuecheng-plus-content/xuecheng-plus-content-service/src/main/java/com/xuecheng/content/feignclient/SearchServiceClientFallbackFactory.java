package com.xuecheng.content.feignclient;

import com.xuecheng.content.model.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/9 16:09:47
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return courseIndex -> {
            log.error("添加课程索引发生熔断，索引信息:{},熔断信息:{}", courseIndex, cause.toString(), cause);
            return false;
        };
    }
}
