package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.*;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/5 15:50:15
 */
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws Exception {

        //通过扩展名得到媒体资源类型mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        //上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                // .object("kun.mp4")//对象名
                .object("test/01/kun.mp4")
                .filename("C:\\Users\\xzt\\Videos\\kun.mp4")//指定本地文件路径
                .contentType(mimeType)
                .build();
        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    //删除文件
    @Test
    void delete() throws Exception {
        //上传文件的参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("kun.mp4")
                .build();
        minioClient.removeObject(removeObjectArgs);
    }


    @Test
    public void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test/01/kun.mp4").build();

        try (
                //指定输出流
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream("D:\\1_2.mp4");
                //校验文件的完整性对文件的内容进行MD5

        ) {
            IOUtils.copy(inputStream, outputStream);
            String source_md5 = DigestUtils.md5Hex("C:\\Users\\xzt\\Videos\\kun.mp4");
            String local_md5 = DigestUtils.md5Hex("D:\\1_2.mp4");
            if (source_md5.equals(local_md5)){
                System.out.println("下载成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
