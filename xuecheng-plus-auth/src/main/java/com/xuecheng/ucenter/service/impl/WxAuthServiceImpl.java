package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.RegisterDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiong
 * @version 1.0
 * @description 微信授权码认证
 * @date 2023/7/10 10:37:53
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.secret}")
    private String secret;
    @Autowired
    private RestTemplate template;
    @Autowired
    private WxAuthServiceImpl currentProxy;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //得到账号
        String username = authParamsDto.getUsername();
        //查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(xcUser == null){
            throw new RuntimeException("用户不存在");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);


        return xcUserExt;
    }


    @Override
    public XcUser wxAuth(String code) {
        //请求微信申请令牌
        Map<String, String> accessTokenMap = getAccess_token(code);
        //访问令牌
        String accessToken = accessTokenMap.get("access_token");
        String openid = accessTokenMap.get("openid");
        // 拿到令牌查询用户信息
        Map<String, String> userinfo = getUserinfo(accessToken, openid);
        if(userinfo==null){
            return null;
        }
        // 将用户信息写入数据库
        XcUser xcUser = currentProxy.addWxUser(userinfo);
        return xcUser;
    }


    /**
     * 申请访问令牌,响应示例
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getAccess_token(String code) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //最终请求路径
        String url = String.format(wxUrl_template, appid, secret, code);
        //远程调用url
        ResponseEntity<String> exchange = template.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应结果
        String result = exchange.getBody();
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    /**
     * 获取用户信息，示例如下：
     * {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYV
     * Y0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getUserinfo(String access_token, String openid) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(wxUrl_template, access_token, openid);
        ResponseEntity<String> exchange = template.exchange(url, HttpMethod.GET, null, String.class);
        //获取响应结果
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    @Transactional
    public XcUser addWxUser(Map userInfo_map) {
        String unionId = userInfo_map.get("unionid").toString();
        //根据unionId查询用户信息
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionId));
        if (xcUser != null) {
            return xcUser;
        }
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);//主键
        xcUser.setWxUnionid(unionId);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionId);
        xcUser.setPassword(unionId);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
