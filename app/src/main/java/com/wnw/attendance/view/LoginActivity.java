package com.wnw.attendance.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.User;
import com.wnw.attendance.config.KeyInfo;


import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wnw on 2017/4/21.
 */

public class LoginActivity extends Activity implements
        View.OnClickListener{

    private EditText sid;        //学号输入框
    private EditText password;   //密码输入框
    private Button login;        //登录按钮
    private User mUser;           //用户

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, KeyInfo.applicationID);
        //如果已经登录过，并且没有退出账号，默认登录，直接跳转到MainActivity
        SharedPreferences sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        if(sharedPreferences != null){
            String id = sharedPreferences.getString("id", "");
            if(!id.equals("")){  //说明已经登录
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
        initView();
    }

    //初始化View
    private void initView(){
        sid = (EditText)findViewById(R.id.login_et_sid);
        password = (EditText)findViewById(R.id.login_et_password);
        login = (Button)findViewById(R.id.login_btn_login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn_login:
                if(validateEditText()){
                    Toast.makeText(this, "手机和密码都不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "正在拼命登录中...",Toast.LENGTH_SHORT).show();
                    //验证密码
                    User user = new User();
                    user.setsId(sid.getText().toString().trim());
                    user.setPassword(password.getText().toString().trim());
                    //开始获得数据
                    findStore(user);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 验证两个EditText是否都已经不为空了
     * */
    private boolean validateEditText(){
        return sid.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty();
    }

    //登录云端，查找账号，返回长度为0代表不成功，大于零则成功
    private void findStore(User user){
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("sId",user.getsId());
        query.addWhereEqualTo("password", user.getPassword());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list != null){
                        int length = list.size();
                        if(length > 0){
                            mUser = list.get(0);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            openMainAty();
                        }else {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //登录成功后，打开MainActivity
    private void openMainAty(){
        saveAccount();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",mUser);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //登录成功，保存登录账号信息到本地
    private void saveAccount(){
        SharedPreferences.Editor editor = getSharedPreferences("account",
                MODE_PRIVATE).edit();
        editor.clear();
        editor.putString("id", mUser.getsId());
        editor.apply();
    }
}
