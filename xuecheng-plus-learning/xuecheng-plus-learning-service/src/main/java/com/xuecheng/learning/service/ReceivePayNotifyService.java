package com.xuecheng.learning.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiong
 * @version 1.0
 * @description 接收支付结果
 * @date 2023/7/12 11:02:24
 */
@Slf4j
@Service
public class ReceivePayNotifyService {

    @Autowired
    MyCourseTableService myCourseTableService;

    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message){
        byte[] body = message.getBody();
        String jsonString = new String(body);
        //转成对象
        MqMessage mqMessage = JSON.parseObject(jsonString, MqMessage.class);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //解析消息内容
        //选课id
        String chooseCourseId = mqMessage.getBusinessKey1();
        //订单类型
        String orderType = mqMessage.getBusinessKey2();
        //消息类型
        String messageType = mqMessage.getMessageType();
        //只需要购买课程类支付订单的结果
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType) && "60201".equals(orderType)) {
            //根据消息内容
            boolean b = myCourseTableService.saveChooseCourseStauts(chooseCourseId);
            if (!b) {
                XueChengPlusException.cast("保存选课记录失败");
            }
        }
    }
}
