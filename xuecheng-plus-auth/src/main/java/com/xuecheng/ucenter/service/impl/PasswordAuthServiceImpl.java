package com.xuecheng.ucenter.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author xiong
 * @version 1.0
 * @description 账号密码认证
 * @date 2023/7/10 10:31:50
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StringUtils.isBlank(checkcodekey)||StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify){
            throw new RuntimeException("验证码输入错误");
        }
        //账号
        String username = authParamsDto.getUsername();

        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(user==null){
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt=new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        //校验密码
        //取出数据库存储的正确密码
        String passwordDB  =user.getPassword();
        String passwordFrom = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordFrom, passwordDB);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        return xcUserExt;
    }
}
