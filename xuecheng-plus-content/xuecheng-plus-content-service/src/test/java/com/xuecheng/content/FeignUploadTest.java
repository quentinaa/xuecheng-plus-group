package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author xiong
 * @version 1.0
 * @description 测试远程调用媒资服务
 * @date 2023/7/8 18:48:22
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    private MediaServiceClient mediaServiceClient;
    @Test
    void test(){
        //将file类型转成MultipartFile类型
        File file=new File("D:\\134.html");
        MultipartFile multipartFile= MultipartSupportConfig.getMultipartFile(file);
        mediaServiceClient.upload(multipartFile,"course/134.html");
    }
}
