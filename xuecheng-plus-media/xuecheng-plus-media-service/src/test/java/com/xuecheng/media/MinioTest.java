package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            if (source_md5.equals(local_md5)) {
                System.out.println("下载成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将分块文件上传到minio
    @Test
    void uploadChunk() throws Exception {
        for (int i = 0; i <= 8; i++) {
            //上传文件的参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    // .object("kun.mp4")//对象名
                    .filename("D:\\chunk\\" + i)//指定本地文件路径
                    .object("chunk/" + i)
                    .build();
            //上传文件
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传文件" + i + "成功");
        }
    }

    //调用minio接口合并文件
    @Test
    void testMerge() throws Exception{
       /* List<ComposeSource> sources = null;
        //指定分块文件的信息
        for (int i = 0; i < 44; i++) {
            ComposeSource source = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
            sources.add(source);
        }*/
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(9).
                map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/" + i).
                        build()).collect(Collectors.toList());
        //指定合并后的objectName等信息
        ComposeObjectArgs objectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)
                .build();
        //上传文件
        minioClient.composeObject(objectArgs);
    }

    //清除分块文件
    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件删除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i).limit(9).map(i ->
                        new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());
        RemoveObjectsArgs removeObjectsArgs=RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError=null;
            try {
                deleteError=r.get();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
