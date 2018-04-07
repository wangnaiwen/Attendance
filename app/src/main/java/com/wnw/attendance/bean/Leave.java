package com.wnw.attendance.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wnw on 2018/4/5.
 */

public class Leave extends BmobObject{
    private String sId;
    private String name;
    private String attendanceId;
    private String status;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
