package com.glory.process.exception.auth;


import com.glory.process.constants.CommonConstants;
import com.glory.process.exception.BaseException;


/**
 * Created by cdy on 2017/9/8.
 */
public class UserTokenException extends BaseException {
    public UserTokenException(String message) {
        super(message, CommonConstants.EX_USER_INVALID_CODE);
    }
}
