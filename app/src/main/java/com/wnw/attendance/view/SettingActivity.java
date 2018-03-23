package com.wnw.attendance.view;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wnw.attendance.R;
import com.wnw.attendance.util.ActivityCollector;


/**
 * Created by wxx on 2016/12/22.
 */

public class SettingActivity extends Activity implements View.OnClickListener{

    private TextView checkNewVersion;
    private TextView aboutContacts;
    private TextView exitContacts;
    private ImageView backSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView(){
        checkNewVersion = (TextView)findViewById(R.id.check_new_version);
        aboutContacts = (TextView)findViewById(R.id.about_contacts);
        exitContacts = (TextView)findViewById(R.id.exit_contacts);
        backSetting = (ImageView)findViewById(R.id.back_setting);

        checkNewVersion.setOnClickListener(this);
        aboutContacts.setOnClickListener(this);
        exitContacts.setOnClickListener(this);
        backSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.check_new_version:
                Toast.makeText(this, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_contacts:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.exit_contacts:
                showExitDialog();
                break;
            case R.id.back_setting:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            default:
                break;
        }
    }


    /**
     * show the dialog of delete the address
     * */
    private void showExitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("退出账号");
        builder.setMessage("是否退成当前账号？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishLogin();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    //退出登录，清除保存登录账号的信息
    public void finishLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ActivityCollector.finishAllActivity();
    }

}
