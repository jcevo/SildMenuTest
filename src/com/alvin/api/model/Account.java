package com.alvin.api.model;

import android.database.Cursor;

import java.util.Date;

/**
 * 用户信息
 */
public class Account {
    private Long id;
    private String username;
    private String password;
    private Long passportId;
    private String commonSessionId;
    private String personalBrief;
    private String token;
    private String secret;
    private String authType;
    private Long oauthUserId;
    private boolean isPassport;
    private String oauthNickname;
    private long time;

    public final static String AUTH_TYPE_PASSPORT = "passport";
    public final static String AUTH_TYPE_SINA = "sina";
    public final static String AUTH_TYPE_TENCENT = "tencent";

    public static Account parse(Cursor cursor) {
        Account account = new Account();
        account.setId(cursor.getLong(cursor.getColumnIndex("id")));
        account.setUsername(cursor.getString(cursor.getColumnIndex("username")));
        account.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        account.setPassportId(cursor.getLong(cursor.getColumnIndex("passport_id")));
        account.setCommonSessionId(cursor.getString(cursor.getColumnIndex("session_id")));
        account.setPersonalBrief(cursor.getString(cursor.getColumnIndex("personal_brief")));
        account.setToken(cursor.getString(cursor.getColumnIndex("token")));
        account.setSecret(cursor.getString(cursor.getColumnIndex("secret")));
        account.setAuthType(cursor.getString(cursor.getColumnIndex("auth_type")));
        account.setOauthUserId(cursor.getLong(cursor.getColumnIndex("oauth_user_id")));
        account.setOauthNickname(cursor.getString(cursor.getColumnIndex("oauth_nickname")));
        account.setTime(cursor.getLong(cursor.getColumnIndex("time")));
        if(cursor.getInt(cursor.getColumnIndex("is_passport"))==1){
            account.setPassport(true);
        }else{
            account.setPassport(false);
        }

        return account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPassportId() {
        return passportId;
    }

    public void setPassportId(Long passportId) {
        this.passportId = passportId;
    }

    public String getCommonSessionId() {
        return commonSessionId;
    }

    public void setCommonSessionId(String commonSessionId) {
        this.commonSessionId = commonSessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getOauthUserId() {
        return oauthUserId;
    }

    public void setOauthUserId(Long oauthUserId) {
        this.oauthUserId = oauthUserId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public String getPersonalBrief() {
        return personalBrief;
    }

    public void setPersonalBrief(String personalBrief) {
        this.personalBrief = personalBrief;
    }

    public boolean isPassport() {
        return isPassport;
    }

    public void setPassport(boolean passport) {
        isPassport = passport;
    }

    public String getOauthNickname() {
        return oauthNickname;
    }

    public void setOauthNickname(String oauthNickname) {
        this.oauthNickname = oauthNickname;
    }
}
