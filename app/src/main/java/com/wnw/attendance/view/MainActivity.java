package com.wnw.attendance.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;
import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;
import com.wnw.attendance.bean.Event;
import com.wnw.attendance.bean.Record;
import com.wnw.attendance.bean.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * 用户姓名，学号以及头像
     * */
    private TextView ssidTv;
    private TextView nameTv;
    private CircleImageView userIcon;

    private TextView nothingTv;

    RecyclerView mRecyclerView;
    DotTimeLineAdapter mAdapter;
    DotItemDecoration mItemDecoration;

    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    /**
     * 初始化控件
     * */
    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中，请稍等...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.home_rv_timeline);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mItemDecoration = new DotItemDecoration
                .Builder(this)
                .setOrientation(DotItemDecoration.VERTICAL)//if you want a horizontal item decoration,remember to set horizontal orientation to your LayoutManager
                .setItemStyle(DotItemDecoration.STYLE_DRAW)
                .setTopDistance(20)//dp
                .setItemInterVal(10)//dp
                .setItemPaddingLeft(20)//default value equals to item interval value
                .setItemPaddingRight(20)//default value equals to item interval value
                .setDotColor(Color.WHITE)
                .setDotRadius(2)
                .setTopDistance(30)
                .setLineColor(Color.WHITE)
                .setLineWidth(1)//dp
                .setEndText("END")
                .setTextColor(Color.WHITE)
                .setTextSize(10)//sp
                .setDotPaddingText(10)//dp.The distance between the last dot and the end text
                .setBottomDistance(40)//you can add a distance to make bottom line longer
                .create();
        mItemDecoration.setSpanIndexListener(new SpanIndexListener() {
            @Override
            public void onSpanIndexChange(View view, int spanIndex) {
                Log.i("Info","view:"+view+"  span:"+spanIndex);
                view.setBackgroundResource(spanIndex == 0 ? R.drawable.pop_left : R.drawable.pop_right);
            }
        });
        mAdapter = new DotTimeLineAdapter(this, eventList);
        mRecyclerView.setAdapter(mAdapter);

        nothingTv = (TextView)findViewById(R.id.tv_nothing);
        nothingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findTodayAttendance();
            }
        });

        ssidTv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_ssid);
        nameTv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        userIcon = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.icon_user);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //头像点击
                /*Intent intent = new Intent(MainActivity.this, ImgUploadActivity.class);
                //intent.putExtra("user", user);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
            }
        });

        findTodayAttendance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001){
            //指纹识别打卡回来
            findTodayAttendance();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 菜单选中监听
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if (id == R.id.nav_record){
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if (id == R.id.nav_attendance){
            Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return true;
    }


    //查找今天打卡计划以及打开记录
    List<Attendance> mAttendanceList;
    private void findTodayAttendance(){
        progressDialog.show();
        Log.e("AttendanceTAG", "find today attendance");
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        Log.e("AttendanceTAG", "year = " + year +" month = " + month + " day=" + day);
        BmobQuery<Attendance> query = new BmobQuery<Attendance>();
        query.addWhereEqualTo("startYear", year);
        query.addWhereEqualTo("startMonth", month);
        query.addWhereEqualTo("startDay", day);
        query.findObjects(new FindListener<Attendance>() {
            @Override
            public void done(List<Attendance> list, BmobException e) {
                if(e==null && list != null){
                    if (list.size() == 0){
                        Log.e("AttendanceTAG", "无打卡计划");
                        mRecyclerView.setVisibility(View.GONE);
                        nothingTv.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }else {
                        Log.e("AttendanceTAG", "有打卡计划");
                        mRecyclerView.setVisibility(View.VISIBLE);
                        nothingTv.setVisibility(View.GONE);
                        mAttendanceList = list;
                        findTodayRecord();
                    }
                }else{
                    //e.printStackTrace();
                    mRecyclerView.setVisibility(View.GONE);
                    nothingTv.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    Log.i("AttendanceTAG","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private List<Event> eventList = new ArrayList<>();
    //查找今天的打卡记录
    private void findTodayRecord(){
        Log.e("AttendanceTAG", "find today record");
        final int length = mAttendanceList.size();
        eventList = new ArrayList<>(length);
        Log.e("AttendanceTAG", "计划长度" + length);
        for (int i = 0; i < length; i ++){
            Event event = new Event();
            Attendance attendance = mAttendanceList.get(i);
            event.setAddress(attendance.getAddress());
            //event.setTime(attendance.getStartTime());
            event.setEndTime(attendance.getEndTime());
            event.setAttendanceId(attendance.getObjectId());
            event.setWifiId(attendance.getWifiId());
            event.setStartTime(attendance.getStartTime());
            event.setResult("待打卡");
            eventList.add(event);
            Log.e("AttendanceTAG", mAttendanceList.get(i).getObjectId() + " " + mAttendanceList.get(i).getAddress());
        }
        for(int i = 0; i < length; i++){
            Log.e("AttendanceTAG", "正在查找今天的记录" + i);
            SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            String sid = sharedPreferences.getString("id", "");
            BmobQuery<Record> query = new BmobQuery<Record>();
            Log.e("AttendanceTAG", mAttendanceList.get(i).getObjectId() +" " +sid);
            query.addWhereEqualTo("attendanceId", mAttendanceList.get(i).getObjectId());
            query.addWhereEqualTo("sId", sid);
            final int finalI = i;
            query.findObjects(new FindListener<Record>() {
                @Override
                public void done(List<Record> list, BmobException e) {
                    if (e == null && list.size() > 0){
                        Log.e("AttendanceTAG", "已经有记录");
                        eventList.get(finalI).setResult(list.get(0).getResult());
                        eventList.get(finalI).setTime(list.get(0).getRecordTime());
                        if (finalI == length-1){
                            Log.e("AttendanceTAG", "刷新视图 ");
                            flushView();
                            progressDialog.dismiss();
                        }
                    }else{
                        Log.e("AttendanceTAG", "没有记录");
                        eventList.get(finalI).setResult("待打卡");
                        if (finalI == length-1){
                            Log.e("AttendanceTAG", "刷新视图 ");
                            flushView();
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    //刷新视图
    private void flushView(){
        if (eventList.size() >= 2){
            mRecyclerView.removeItemDecoration(mItemDecoration);
            mRecyclerView.addItemDecoration(mItemDecoration);
        }

        mAdapter.setList(eventList);
        mAdapter.notifyDataSetChanged();
    }
}
