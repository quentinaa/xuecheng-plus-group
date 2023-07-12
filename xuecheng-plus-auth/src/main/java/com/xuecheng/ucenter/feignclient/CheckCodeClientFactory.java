package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/10 14:35:29
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable cause) {
        return (key, code) -> {
            log.debug("调用验证码服务熔断异常:{}", cause.getMessage());
            return null;
        };
    }
}
