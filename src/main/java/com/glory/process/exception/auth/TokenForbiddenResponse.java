package com.glory.process.exception.auth;

import com.glory.process.constants.CommonConstants;
import com.glory.process.util.BaseResponse;


/**
 * Created by cdy on 2017/8/25.
 */
public class TokenForbiddenResponse extends BaseResponse {
    public TokenForbiddenResponse(String message) {
        super(CommonConstants.TOKEN_FORBIDDEN_CODE, message);
    }
}
