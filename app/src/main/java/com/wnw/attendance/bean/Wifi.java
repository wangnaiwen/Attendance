package com.wnw.attendance.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wnw on 2018/3/13.
 */

public class Wifi extends BmobObject{
    private String address;
    private String mac;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
