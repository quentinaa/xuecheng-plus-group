package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/9 21:31:50
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private XcMenuMapper xcMenuMapper;

        /**
         * @description 根据账号查询用户信息
         * @param s  账号
         * @return org.springframework.security.core.userdetails.UserDetails
         * @author xiong
         * @date 2022/9/28 18:30
         */
        @Override
        public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
            //将传入的json转成AuthParamsDto对象
            AuthParamsDto authParamsDto;
            try {
                authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
            } catch (Exception e) {
                throw new RuntimeException("请求认证参数不符合要求");
            }

            //认证类型，有password，wx。。。
            String authType = authParamsDto.getAuthType();


            //根据认证类型从spring容器取出指定的bean
            String beanName = authType+"_authservice";
            AuthService authService = applicationContext.getBean(beanName, AuthService.class);
            //调用统一execute方法完成认证
            XcUserExt xcUserExt = authService.execute(authParamsDto);
            //封装xcUserExt用户信息为UserDetails
            //根据UserDetails对象生成令牌
            UserDetails userPrincipal = getUserPrincipal(xcUserExt);

            return userPrincipal;
        }

    /**
     * @description 查询用户信息
     * @param user  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     */
    private UserDetails getUserPrincipal(XcUserExt user) {
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities={"p1"};
        //根据用户id查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        if (xcMenus.size()>0){
            List<String> permissions=new ArrayList<>();
            xcMenus.forEach(m->{
                permissions.add(m.getCode());
            });
            //permissions转成数组
            authorities= permissions.toArray(new String[0]);
        }


        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }
}
