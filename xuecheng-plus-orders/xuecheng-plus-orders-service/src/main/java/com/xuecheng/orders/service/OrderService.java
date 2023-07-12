package com.xuecheng.orders.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

public interface OrderService {
    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付交易记录(包括二维码)
     * @author xiong
     */
     PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 查询支付交易记录
     * @param payNo  交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @author xiong
     * @date 2022/10/20 23:38
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 支付结果通知
     * @param message
     */
    void notifyPayResult(MqMessage message);
    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息
     * @return void
     */
    void saveAliPayStatus(PayStatusDto payStatusDto) ;
    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    PayRecordDto queryPayResult(String payNo);

}
