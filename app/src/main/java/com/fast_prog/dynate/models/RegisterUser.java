package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 11/9/16.
 */

public class RegisterUser implements Serializable {

    private String name;
    private String nameArabic;
    private String mobile;
    private String mail;
    private String address;
    private String username;
    private String password;
    private String latitide;
    private String longitude;
    private String loginMethod;
    private String vModelId;
    private String vModelName;
    private String vTypeId;
    private String vTypeName;
    private String licenseNo;
    private String licenseNoArabic;
    private boolean withGlass;

    public boolean isWithGlass() {
        return withGlass;
    }

    public void setWithGlass(boolean withGlass) {
        this.withGlass = withGlass;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    public void setNameArabic(String nameArabic) {
        this.nameArabic = nameArabic;
    }

    public String getLatitide() {
        return latitide;
    }

    public void setLatitide(String latitide) {
        this.latitide = latitide;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getvModelId() {
        return vModelId;
    }

    public void setvModelId(String vModelId) {
        this.vModelId = vModelId;
    }

    public String getvModelName() {
        return vModelName;
    }

    public void setvModelName(String vModelName) {
        this.vModelName = vModelName;
    }

    public String getvTypeId() {
        return vTypeId;
    }

    public void setvTypeId(String vTypeId) {
        this.vTypeId = vTypeId;
    }

    public String getvTypeName() {
        return vTypeName;
    }

    public void setvTypeName(String vTypeName) {
        this.vTypeName = vTypeName;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseNoArabic() {
        return licenseNoArabic;
    }

    public void setLicenseNoArabic(String licenseNoArabic) {
        this.licenseNoArabic = licenseNoArabic;
    }

}
