package com.alvin.activity.service;

/**
 *
 */
public class UPCService {
    public static String INTERFACE_HEAD = "http://i1.3conline.com/images/upload/upc/face/";
    public static String INTERFACE_UPLOAD_IMAGE;
    public static String INTERFACE_UPLOAD_HEAD;
    public static String INTERFACE_UPLOAD_HEAD_PROCESS;
    public static Long CURRENT_TIME = (long) 0;
    /**
     * 获取用户头像URL
     * @param id 用户的id
     * @param suffix 要获取头像的像素  "50x50","100x100","150x150"
     * @return
     */
    public static String getFaceUrl(String id, String suffix){
        String newId = "";
        newId = id.replaceAll("(\\d\\d)", "$1/");
        if(id.length()%2==0){
            newId +=id;
        }else{
            newId+=("/"+id);
        }
        String url = "";
        if(PassportService.getPassportId().equals(id)){
            if(CURRENT_TIME==0){
                CURRENT_TIME = System.currentTimeMillis();
            }
            url = UPCService.INTERFACE_HEAD + newId + "_" + suffix+"?"+CURRENT_TIME;
        }else{
            url = UPCService.INTERFACE_HEAD + newId + "_" + suffix;
        }
        
        return url;
    }
}
