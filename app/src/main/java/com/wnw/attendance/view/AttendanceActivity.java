package com.wnw.attendance.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created by wnw on 2018/3/23.
 */

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AttendanceAdapter attendanceAdapter;

    private List<Attendance> attendanceList = new ArrayList<>();

    private TextView nothingTv;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        initView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 菜单选中监听
        int id = item.getItemId();
        if (id == R.id.action_pick_date) {
            DatePicker datePicker = new DatePicker(this);
            datePicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    try {
                        Log.e("AttendanceWnw", year+"-"+month+"-"+day );
                        Date date = converToDate(year+"-"+month+"-"+day);
                        long l = 24*60*60*1000; //每天的毫秒数
                        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
                        //减8个小时的毫秒值是为了解决时区的问题。
                        Log.e("AttendanceWnw", date.getTime()+"");
                        final long startTime = date.getTime();
                        //final long startTime = (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
                        final long endTime = startTime + 24 * 60 * 60 * 1000;
                        Log.e("AttendanceWnw", startTime + " " +endTime);
                        findAttendance(startTime, endTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            datePicker.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        nothingTv = (TextView)findViewById(R.id.tv_nothing);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中，请稍等...");

        Date date = new Date();
        long l = 24*60*60*1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。

        final long startTime = (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
        final long endTime = startTime + 24 * 60 * 60 * 1000;

        Log.e("AttendanceWnw", startTime + " " +endTime);
        findAttendance(startTime, endTime);

        nothingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findAttendance(startTime, endTime);
            }
        });

        attendanceAdapter = new AttendanceAdapter(this, attendanceList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(attendanceAdapter);
    }

    //把字符串转为日期
    public static Date converToDate(String strDate) throws Exception
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    /**
     * 查找对应日期的考勤计划
     * */
    private void findAttendance(long startTime, long endTime){
        progressDialog.show();
        BmobQuery<Attendance> query = new BmobQuery<Attendance>();
        query.addWhereLessThan("startTime", endTime);
        query.addWhereGreaterThan("startTime", startTime);
        query.findObjects(new FindListener<Attendance>() {
            @Override
            public void done(List<Attendance> list, BmobException e) {
                if (e == null && list.size() > 0){
                    //刷新视图
                    progressDialog.dismiss();
                    Log.e("AttendanceTAG", " " + list.size());
                    attendanceList = list;
                    recyclerView.setVisibility(View.VISIBLE);
                    nothingTv.setVisibility(View.GONE);
                    attendanceAdapter.setAttendanceList(attendanceList);
                    attendanceAdapter.notifyDataSetChanged();
                }else{
                    progressDialog.dismiss();
                    e.printStackTrace();
                    recyclerView.setVisibility(View.GONE);
                    nothingTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
