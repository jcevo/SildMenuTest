package com.alvin.api.bean;

public class ShareCompanyInfo extends BasicBean {

    public int getImgPath() {
        return imgPath;
    }
    public void setImgPath(int imgPath) {
        this.imgPath = imgPath;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

 
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }


    int imgPath;
    String text,company;
    boolean isSelected;
}
