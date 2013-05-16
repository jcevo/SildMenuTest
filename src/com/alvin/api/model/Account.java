package com.alvin.api.model;

import android.database.Cursor;

/**
 * 用户信息
 */
public class Account {
    private int type = 0 ;// 登陆类型
    private String username = "";// 用户名
    private String password = "";// 密码
    private String sessionId = "";// sessionid
    private String displayName = "";// 昵称
    private String userId = "";// 用户id
    private String photoUrl = "";// 用户头像
    private String description = "";// 个人简介
    private long loginTime = -1;//登陆时间
    private int defaults = 0;//

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getDefaults() {
        return defaults;
    }

    public void setDefaults(int defaults) {
        this.defaults = defaults;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

}
