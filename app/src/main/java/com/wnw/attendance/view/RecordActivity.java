package com.wnw.attendance.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;
import com.wnw.attendance.bean.Record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wnw on 2018/3/23.
 */

public class RecordActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;

    private List<Record> recordList = new ArrayList<>();

    private TextView nothingTv;

    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
    }

    /**
     * 获取下一天
     * */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    private void initView(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        nothingTv = (TextView)findViewById(R.id.tv_nothing);
        progressDialog = new ProgressDialog(this);
        Date date = new Date();
        long l = 24*60*60*1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。

        final long startTime = (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
        final long endTime = startTime + 24 * 60 * 60 * 1000;

        Log.e("AttendanceWnw", startTime + " " +endTime);
        findRecord(startTime, endTime);

        nothingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findRecord(startTime, endTime);
            }
        });

        recordAdapter = new RecordAdapter(this, recordList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordAdapter);
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
    private void findRecord(long startTime, long endTime){
        progressDialog.show();
        BmobQuery<Record> query = new BmobQuery<Record>();
        query.addWhereLessThan("recordTime", endTime);
        query.addWhereGreaterThan("recordTime", startTime);
        query.findObjects(new FindListener<Record>() {
            @Override
            public void done(List<Record> list, BmobException e) {
                if (e == null && list.size() > 0){
                    //刷新视图
                    progressDialog.dismiss();
                    Log.e("AttendanceTAG", " " + list.size());
                    recordList = list;
                    recyclerView.setVisibility(View.VISIBLE);
                    nothingTv.setVisibility(View.GONE);
                    recordAdapter.setRecordList(recordList);
                    recordAdapter.notifyDataSetChanged();
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
