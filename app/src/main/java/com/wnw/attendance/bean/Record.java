package com.wnw.attendance.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wnw on 2018/3/13.
 */

public class Record extends BmobObject{
    private String sId;
    private String recordTime;
    private String attendanceId;
    private String result;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
