package com.glory.process.exception.product;


import com.glory.process.constants.CommonConstants;
import com.glory.process.exception.BaseException;

/**
 * Created by CDZ on 2018/11/17.
 */
public class ProcessInvalidException extends BaseException {
    public ProcessInvalidException(String message) {
        super(message, CommonConstants.EX_OTHER_CODE);
    }
}
