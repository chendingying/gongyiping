package com.glory.process.biz;

import com.glory.process.exception.auth.UserInvalidException;
import com.glory.process.jwt.JWTInfo;
import com.glory.process.jwt.JwtAuthenticationRequest;
import com.glory.process.jwt.JwtTokenUtil;
import com.glory.process.mapper.admin.UserMapper;
import com.glory.process.model.User;
import com.glory.process.model.UserInfo;
import com.glory.process.util.BaseBiz;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * Created by CDZ on 2018/11/27.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthBiz extends BaseBiz<UserMapper,User> {
    @Autowired
    private UserBiz usersBiz;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String login(JwtAuthenticationRequest authenticationRequest, HttpSession session) throws Exception {
        // 获取验证码的代码
        if (session.getAttribute("image") == null) {
            throw new UserInvalidException("重新获取验证码!");
        } else {
            if (session.getAttribute("image").toString().equalsIgnoreCase(authenticationRequest.getCode())) {
                UserInfo info = new UserInfo();
                User user = usersBiz.getUserByUsername(authenticationRequest.getUsername());
                if(user == null){
                    throw new UserInvalidException("用户不存在或账户密码错误!");
                }
                if(user.getStatus() == 0){
                    throw new UserInvalidException("用户被禁用，请联系管理员！");
                }
                if (encoder.matches(authenticationRequest
                        .getPassword(), user.getPassword())) {
                    BeanUtils.copyProperties(user, info);
                    info.setId(user.getId().toString());
                }
                if (!StringUtils.isEmpty(info.getId())) {
                    return jwtTokenUtil.generateToken(new JWTInfo(info.getUsername(), info.getId() + "", info.getName()));
                }

            }else{
                throw new UserInvalidException("输入验证码错误!");
            }
        }

//        UserInfo info = new UserInfo();
//        User user = usersBiz.getUserByUsername(authenticationRequest.getUsername());
//        if (encoder.matches(authenticationRequest
//                .getPassword(), user.getPassword())) {
//            BeanUtils.copyProperties(user, info);
//            info.setId(user.getId().toString());
//        }
//        if (!StringUtils.isEmpty(info.getId())) {
//            return jwtTokenUtil.generateToken(new JWTInfo(info.getUsername(), info.getId() + "", info.getName()));
//        }
        throw new UserInvalidException("用户不存在或账户密码错误!");
    }
}
