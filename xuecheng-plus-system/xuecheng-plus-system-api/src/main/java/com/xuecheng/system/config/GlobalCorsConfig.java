package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author xiong
 * @version 1.0
 * @description跨域过滤器
 * @date 2023/7/3 18:46:33
 */
@Configuration
public class GlobalCorsConfig {
    /**
     * 允许跨域调用的过滤器
     */
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration configuration=new CorsConfiguration();
        //允许白名单域名进行跨域调用
        configuration.addAllowedOrigin("*");
        //允许跨域发送cookie
        configuration.setAllowCredentials(true);
        //放行全部原始头信息
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return new CorsFilter(source);
    }
}
