package com.wnw.attendance.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;
import com.wnw.attendance.bean.Leave;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by wnw on 2018/4/5.
 */

public class LeaveActivity extends AppCompatActivity{
    private Switch leaveSw;
    private TextView  leaveStatusTv;
    private Leave leave;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        Intent intent = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        leave = new Leave();
        leave.setsId(sharedPreferences.getString("id", ""));
        leave.setName(sharedPreferences.getString("name", ""));
        leave.setAttendanceId(intent.getStringExtra("attendanceId"));
        initView();
    }

    /**
     * 初始化一个View
     * */
    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中，请稍等...");
        leaveSw = (Switch)findViewById(R.id.sw_leave);
        leaveStatusTv = (TextView)findViewById(R.id.tv_status);
        leaveSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    insertLeave();
                }
            }
        });
        findLeave();
    }

    /**
     * 插入一条请假记录
     * */
    private void insertLeave(){
        progressDialog.show();
        leave.setStatus("待审批");
        leave.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                progressDialog.dismiss();
                if (e == null){
                    Toast.makeText(LeaveActivity.this, "请假已经提交,等待审批", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }else {
                    e.printStackTrace();
                    Toast.makeText(LeaveActivity.this, "请假失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 查找请假
     * */
    private void findLeave(){
        progressDialog.show();
        BmobQuery<Leave> query = new BmobQuery<Leave>();
        query.addWhereEqualTo("attendanceId", leave.getAttendanceId());
        query.addWhereEqualTo("sId", leave.getsId());
        query.findObjects(new FindListener<Leave>() {
            @Override
            public void done(List<Leave> list, BmobException e) {
                progressDialog.dismiss();
                if (e == null && list.size() > 0){
                    //已经请假了
                    Log.e("Attendance", "已经请假了");
                    final Leave leave = list.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leaveSw.setVisibility(View.GONE);
                            leaveStatusTv.setVisibility(View.VISIBLE);
                            leaveStatusTv.setText("您已经请假：" + leave.getStatus());
                        }
                    });
                }else{
                    //还没有请假
                    e.printStackTrace();
                    Log.e("Attendance", "还没有请假");
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
