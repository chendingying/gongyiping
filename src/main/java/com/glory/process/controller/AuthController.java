package com.glory.process.controller;

import com.glory.process.biz.AuthBiz;
import com.glory.process.jwt.JwtAuthenticationRequest;
import com.glory.process.model.User;
import com.glory.process.model.UserInfo;
import com.glory.process.service.AuthService;
import com.glory.process.service.impl.PermissionService;
import com.glory.process.util.BaseController;
import com.glory.process.util.ObjectRestResponse;
import com.glory.process.util.RandomValidateCodeUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;

/**
 * 权限管理
 * Created by CDZ on 2018/11/27.
 */
@RestController
@RequestMapping("api/auth/jwt")
public class AuthController extends BaseController<AuthBiz,User> {
    @Autowired
    PermissionService permissionService;


    Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * 登录
     * @param authenticationRequest
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ObjectRestResponse<String> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpSession session) throws Exception {
        log.info(authenticationRequest.getUsername()+" require logging...");
        final String token = baseBiz.login(authenticationRequest,session);
        return new ObjectRestResponse<>().data(token);
    }

    /**
     * 生成验证码
     * @param response
     * @param request
     * @throws Exception
     */
    @GetMapping("/getcode")
    public void getCode(HttpServletResponse response, HttpServletRequest request) throws Exception{
        HttpSession session=request.getSession();
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = RandomValidateCodeUtil.createImage();
        //将验证码存入Session
        session.setAttribute("image",objs[0]);
        //自己把SessionID保存在cookie中
        Cookie cookie=new Cookie("JSESSIONID", session.getId());
        //设置cookie保存时间
        cookie.setMaxAge(60*20);
        //被创建的cookie返回浏览器
        response.addCookie(cookie);
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
    }

}
