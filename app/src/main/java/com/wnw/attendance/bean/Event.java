package com.wnw.attendance.bean;

/**
 * Created by wnw on 2018/3/24.
 */

public class Event {
    private long time;
    private String address;
    private String result;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
