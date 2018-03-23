package com.wnw.attendance.view;

import android.content.Intent;
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

import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;
import com.wnw.attendance.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * 用户姓名，学号以及头像
     * */
    private TextView ssidTv;
    private TextView nameTv;
    private CircleImageView userIcon;

    RecyclerView mRecyclerView;
    List<Event> mList = new ArrayList<>();
    DotTimeLineAdapter mAdapter;
    DotItemDecoration mItemDecoration;

    long[] times = {
            1497229200,
            1497240000,
            1497243600,
            1497247200,
            1497249000,
            1497252600
    };
    String[] events = new String[]{
            "去小北门拿快递",
            "跟同事一起聚餐",
            "写文档",
            "和产品开会",
            "整理开会内容",
            "提交代码到git上"
    };


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        mRecyclerView.addItemDecoration(mItemDecoration);

        for (int i = 0; i < times.length; i++) {
            Event event = new Event();
            event.setTime(times[i]);
            event.setEvent(events[i]);
            mList.add(event);
        }

        mAdapter = new DotTimeLineAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);

        ssidTv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_ssid);
        nameTv = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        userIcon = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.icon_user);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //头像点击
                Intent intent = new Intent(MainActivity.this, ImgUploadActivity.class);
                //intent.putExtra("user", user);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4){
            //头像
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

        }else if (id == R.id.nav_attendance){

        }
        return true;
    }
}
