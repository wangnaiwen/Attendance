package com.wnw.attendance.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wnw on 2018/3/13.
 */

public class User extends BmobObject{
    private String sId;
    private String gender;
    private String name;
    private String password;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
