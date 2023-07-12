package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 测试大文件上传方法
 * @date 2023/7/6 09:56:10
 */
public class BigFileTest {

    //分块测试
    @Test
    void testChunk() throws IOException {
        //源文件
        File sourceFile=new File("C:\\Users\\xzt\\Videos\\test1.mp4");
        //分块文件存储路径
        String chunkFilePath="D:\\chunk\\";
        //分块文件大小
        int chunkSize=1024*1024*5;
        //分块文件个数
        int chunkNum = (int)Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读数据，向分块文件中写数据
        RandomAccessFile raf_r=new RandomAccessFile(sourceFile,"r");
        //缓存区
        byte[] bytes=new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len;
            while ((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes,0,len);
                if (chunkFile.length()>=chunkSize){
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    //将分块文件合并
    @Test
    void testMerge()throws IOException{
        //块文件目录
        File chunkFolder=new File("d:\\chunk");
        //源文件
        File sourceFile=new File("C:\\Users\\xzt\\Videos\\test1.mp4");
        //合并后的源文件
        File mergeFile=new File("C:\\Users\\xzt\\Videos\\test1_copy.mp4");
        //取出所有的分块文件
        File[] files = chunkFolder.listFiles();
        //将数组转成list
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        //向合并文件写的流
        RandomAccessFile raw_rw=new RandomAccessFile(mergeFile,"rw");
        //缓存区
        byte[] bytes=new byte[1024];
        //遍历分块文件，向合并的文件写
        for (File file:fileList){
            //读分块的文件
            RandomAccessFile raf_r=new RandomAccessFile(file,"r");
            int len;
            while ((len=raf_r.read(bytes))!=-1){
                raw_rw.write(bytes,0,len);
            }
            raf_r.close();
        }
        raw_rw.close();
        //合并文件完成后校验文件完整性
        String md5_merge = DigestUtils.md5Hex(new FileInputStream(mergeFile));
        String md5_source = DigestUtils.md5Hex(new FileInputStream(sourceFile));
        if (md5_source.equals(md5_merge)){
            System.out.println("文件合并成功");
        }
    }

}
