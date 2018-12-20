package com.glory.process.constants;

/**
 * Created by cdy on 2017/8/29.
 */
public class CommonConstants {
    public final static int ROOT = -1;
    public final static int DEFAULT_GROUP_TYPE = 0;
    public final static String RESOURCE_TYPE_MENU = "menu";
    public final static String RESOURCE_TYPE_BTN = "button";
    /**
     * 权限关联类型
     */
    public final static String AUTHORITY_TYPE_GROUP = "group";
    // 用户token异常
    public static final Integer EX_USER_INVALID_CODE = 40101;
    public static final Integer EX_USER_PASS_INVALID_CODE = 40001;
    // 客户端token异常
    public static final Integer EX_CLIENT_INVALID_CODE = 40301;
    public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;
    public static final Integer EX_OTHER_CODE = 500;
    public static final String CONTEXT_KEY_USER_ID = "currentUserId";
    public static final String CONTEXT_KEY_USERNAME = "currentUserName";
    public static final String CONTEXT_KEY_USER_NAME = "currentUser";
    public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
    public static final String JWT_KEY_USER_ID = "userId";
    public static final String JWT_KEY_NAME = "name";
    public final static String RESOURCE_ACTION_VISIT = "访问";
    public final static String RESOURCE_REQUEST_METHOD_GET = "GET";
    public final static String RESOURCE_REQUEST_METHOD_PUT = "PUT";
    public final static String RESOURCE_REQUEST_METHOD_DELETE = "DELETE";
    public final static String RESOURCE_REQUEST_METHOD_POST = "POST";
    public final static Integer BOOLEAN_NUMBER_FALSE = 0;
    public final static Integer BOOLEAN_NUMBER_TRUE = 1;
    public final static Integer PW_ENCORDER_SALT = 12;

    public static final int TOKEN_ERROR_CODE = 40101;
    public static final int TOKEN_FORBIDDEN_CODE = 40301;
}
