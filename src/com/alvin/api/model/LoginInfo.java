package com.alvin.api.model;

public class LoginInfo {
    private int status;// 状态   
    private String message;// 信息
    private String userId;
    private String sessionId;
    
    public final static int STATUS_SUCCESS = 0;//0、成功；
    public final static int STATUS_USERNAME_OR_PASSWORD_EMPTY = 1;//1、用户名和密码必须输入；
    public final static int STATUS_USERNAME_NOT_EXIST = 2;//2、用户不存在；
    public final static int STATUS_PASSWORD_ERROR = 3;//3、密码错
    public final static int STATUS_MUST_VERIFICATION_CODE = 4;//必须输入验证码
    public final static int STATUS_VERIFICATION_CODE_NOT_USE = 5;//验证码失败（您可能禁用了浏览器的cookie）
    public final static int STATUS_VERIFICATION_CODE_OUTTIME = 6;//验证码失败（验证码超过15分钟会失效）
    public final static int STATUS_VERIFICATION_CODE_ERROR = 7;//验证码不正确
    

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
