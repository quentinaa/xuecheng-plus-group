package com.xuecheng.checkcode.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author xiong
 * @version 1.0
 * @description 邮件工具类
 * @date 2023/7/10 21:19:28
 */
public class MailUtil {
    public static void main(String[] args) throws MessagingException {
        //可以在这里直接测试方法，填自己的邮箱即可
        sendTestMail("359704157@qq.com", new MailUtil().achieveCode());
    }
    /**
     * 发送邮件
     * @param email 收件邮箱号
     * @param code  验证码
     * @throws MessagingException
     */
    public static void sendTestMail(String email,String code) throws MessagingException{
        //创建 Properties类用于记录邮箱的一些属性
        Properties properties=new Properties();
        //表示SMTP发送邮件，必须进行身份验证
        properties.put("mail.smtp.auth","true");
        //此处填写SMTP服务器
        properties.put("mail.smtp.host","smtp.qq.com");
        //端口号，QQ邮箱端口587
        properties.put("mail.smtp.port", "587");
        // 此处填写，写信人的账号
        properties.put("mail.user", "359704157@qq.com");
        // 此处填写16位STMP口令
        properties.put("mail.password", "lxfqboqgfpyrbijh");
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator=new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = properties.getProperty("mail.user");
                String password = properties.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession=Session.getInstance(properties,authenticator);
        // 创建邮件消息
        MimeMessage mimeMessage = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress from = new InternetAddress(properties.getProperty("mail.user"));
        mimeMessage.setFrom(from);
        // 设置收件人的邮箱
        InternetAddress to=new InternetAddress(email);
        mimeMessage.setRecipient(Message.RecipientType.TO,to);
        // 设置邮件标题
        mimeMessage.setSubject("Kyle's Blog 邮件测试");
        // 设置邮件的内容体
        mimeMessage.setContent("尊敬的用户:你好!\n注册验证码为:" + code + "(有效期为一分钟,请勿告知他人)", "text/html;charset=UTF-8");
        // 最后当然就是发送邮件啦
        Transport.send(mimeMessage);
    }
    /**
     *  生成验证码
     * @return
     */
    public static String achieveCode(){
        String[] beforeShuffle = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb=new StringBuilder();
        for (String s:list){
            sb.append(s);
        }
        return sb.substring(3,8);
    }
}
