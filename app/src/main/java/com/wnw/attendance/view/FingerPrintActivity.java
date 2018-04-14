package com.wnw.attendance.view;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Event;
import com.wnw.attendance.bean.Record;
import com.wnw.attendance.bean.Wifi;
import com.wnw.attendance.util.NetWorkUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by wnw on 2018/4/1.
 */

public class FingerPrintActivity extends AppCompatActivity{
    FingerprintManager manager;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private final static String TAG = "FingerTest";

    private Event event;
    private Record mRecord;
    private String sid;

    private String wifiMac;

    private ImageView statusIv;
    private TextView startTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        manager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        event = (Event) getIntent().getSerializableExtra("event");
        initView();
    }

    private void initView(){
        statusIv = (ImageView)findViewById(R.id.ig_status);
        startTv = (TextView)findViewById(R.id.tv_start);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始打卡
                if (isArrivalTime()){
                    if (isFinger()){
                        findWifiMac();
                    }
                }else {
                    Toast.makeText(FingerPrintActivity.this, "还没有到达考勤时间", Toast.LENGTH_SHORT).show();
                }
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        sid = sharedPreferences.getString("id", "");

        mRecord = new Record();
        mRecord.setsId(sid);
        mRecord.setAddress(event.getAddress());
        mRecord.setAttendanceId(event.getAttendanceId());
    }

    /**
     * 是否到达考勤时间
     * */
    private boolean isArrivalTime(){
        long currentTime = System.currentTimeMillis();
        if(currentTime < event.getStartTime()){
             return false;
        }
        return true;
    }

    /**
     * 是否支持指纹
     * */
    public boolean isFinger() {
        if (!manager.isHardwareDetected()) {
            Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mKeyManager.isKeyguardSecure()) {
            Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!manager.hasEnrolledFingerprints()) {
            Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled      = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
            Toast.makeText(FingerPrintActivity.this, errString, Toast.LENGTH_SHORT).show();
            showAuthenticationScreen();
        }
        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

            Toast.makeText(FingerPrintActivity.this, helpString, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Toast.makeText(FingerPrintActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
            statusIv.setImageResource(R.drawable.finger_green);
            uploadRecord();
        }
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(FingerPrintActivity.this, "指纹识别失败,请重新识别", Toast.LENGTH_SHORT).show();
            statusIv.setImageResource(R.drawable.finger_red);
        }
    };

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
    }

    private void showAuthenticationScreen() {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "识别成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //上传指纹考勤信息
    private void uploadRecord(){
        long time = System.currentTimeMillis();
        mRecord.setRecordTime(time);
        if (time > event.getEndTime()){
            mRecord.setResult("迟到");
        }else{
            mRecord.setResult("正常");
        }
        mRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Toast.makeText(FingerPrintActivity.this, "打卡成功：" + mRecord.getResult(), Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }else{
                    Toast.makeText(FingerPrintActivity.this, "打卡异常,请重新进入打卡", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
    }

    private void findWifiMac(){
        BmobQuery<Wifi> query = new BmobQuery<Wifi>();
        Log.e("wnw",event.getWifiId());
        query.addWhereEqualTo("wifiId", event.getWifiId());
        query.getObject(event.getWifiId(), new QueryListener<Wifi>() {
            @Override
            public void done(Wifi wifi, BmobException e) {
                if (e == null){
                    wifiMac = wifi.getMac();
                    isSameWifi();
                }else{
                    e.printStackTrace();
                    Toast.makeText(FingerPrintActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 是否是要打卡的wifi
     *
     */
    private void isSameWifi(){
        WifiManager wifi_service = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo    = wifi_service.getConnectionInfo();
        Log.e("wnw", "wifi" + wifiInfo.getBSSID());
        if (wifiMac.equals(wifiInfo.getBSSID())){
            startListening(null);
            Toast.makeText(FingerPrintActivity.this, "请进行指纹识别", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(FingerPrintActivity.this, "请连接到指定wifi再打卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
