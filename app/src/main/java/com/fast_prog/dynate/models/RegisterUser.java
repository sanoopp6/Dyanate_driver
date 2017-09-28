package com.fast_prog.dynate.models;

import java.io.Serializable;

/**
 * Created by sarathk on 11/9/16.
 */

public class RegisterUser implements Serializable {

    //public String vTypeId;
    //public String vTypeName;

    public String name;
    public String nameArabic;
    public String mobile;
    public String mail;
    public String address;
    public String username;
    public String password;
    public String latitude;
    public String longitude;
    public String loginMethod;
    public String vModelId;
    public String vModelName;
    public String vCompId;
    public String vCompName;
    public String licenseNo;
    public String licenseNoArabic;

    public boolean withGlass;
}
