package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/9 21:31:50
 */
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

        /**
         * @description 根据账号查询用户信息
         * @param s  账号
         * @return org.springframework.security.core.userdetails.UserDetails
         * @author Mr.M
         * @date 2022/9/28 18:30
         */
        @Override
        public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

            XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, s));
            if(user==null){
                //返回空表示用户不存在
                return null;
            }
            //取出数据库存储的正确密码
            String password  =user.getPassword();
            //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
            String[] authorities= {"test"};
            //创建UserDetails对象,权限信息待实现授权功能时再向UserDetail中加入
            UserDetails userDetails = User.withUsername(user.getUsername()).password(password).authorities(authorities).build();

            return userDetails;
        }
}
