package com.glory.process.jwt;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * session信息模型
 *
 * @author yangfan
 */
@Data
public class AuthTokenDetails implements java.io.Serializable {

    private Long id;// 用户ID
    private String name; //用户名
    private String username;// 用户登录名
    private String ip;// 用户IP

    private Date expirationDate;

}
