package com.alvin.api.bean;

public class WeiboInfo extends BasicBean {
    
    public String accessToken;
    public String tokenSercet;
    public String weiboType;
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getTokenSercet() {
        return tokenSercet;
    }
    public void setTokenSercet(String tokenSercet) {
        this.tokenSercet = tokenSercet;
    }
    public String getWeiboType() {
        return weiboType;
    }
    public void setWeiboType(String weiboType) {
        this.weiboType = weiboType;
    }
    
    

}
