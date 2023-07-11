package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.po.XcUser;

//微信扫码接口
public interface WxAuthService {

    /**
     *
     * @param code
     * @return
     */
    XcUser wxAuth(String code);
}
