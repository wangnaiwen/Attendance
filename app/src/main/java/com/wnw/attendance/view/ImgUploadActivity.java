package com.wnw.attendance.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.wnw.attendance.R;
import com.wnw.attendance.bean.User;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by wxx on 2017/3/24.
 */

public class ImgUploadActivity extends Activity implements View.OnClickListener{
    private static final int PICK_CODE =1;

    private CircleImageView userImg;
    private ImageView back;
    private TextView upload;
    private String ImagePath=null;
    private Bitmap myBitmapImage;

    private User user;
    private String url = null;
    private String imgName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_img_upload);
        initView();
    }

    //初始化View
    private void initView(){
        userImg = (CircleImageView) findViewById(R.id.img_user);
        back = (ImageView)findViewById(R.id.back_upload);
        upload = (TextView)findViewById(R.id.upload);

        userImg.setOnClickListener(this);
        back.setOnClickListener(this);
        upload.setOnClickListener(this);

        user = new User();
        SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
        user.setsId(preferences.getString("sid",""));
        //user.setNickName(preferences.getString("nickname",""));
        user.setPassword(preferences.getString("password", ""));
        //user.setPhone(preferences.getString("phone",""));
        url = preferences.getString("url", "");
        imgName = preferences.getString("imgName","");

        //调用Glide这个框架去加载网络图片
        Glide.with(this).load(url).error(R.drawable.error).into(userImg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload:
                uploadUserImg();
                break;
            case R.id.back_upload:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.img_user:
                //获取系统选择图片intent
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //开启选择图片功能响应码为PICK_CODE
                startActivityForResult(intent,PICK_CODE);
                break;
            default:
                break;
        }
    }


    //设置响应intent请求
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==PICK_CODE)
        {
            if(intent!=null)
            {
                //获取图片路径
                //获取所有图片资源
                Uri uri=intent.getData();
                //设置指针获得一个ContentResolver的实例
                Cursor cursor=getContentResolver().query(uri,null,null,null,null);
                cursor.moveToFirst();
                //返回索引项位置
                int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                //返回索引项路径
                ImagePath=cursor.getString(index);
                Log.d("image", ImagePath);
                cursor.close();
                //这个jar包要求请求的图片大小不得超过3m所以要进行一个压缩图片操作
                resizePhoto();
                userImg.setImageBitmap(myBitmapImage);

            }
        }
    }
    //压缩图片
    private void resizePhoto() {
        //得到BitmapFactory的操作权
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 如果设置为 true ，不获取图片，不分配内存，但会返回图片的高宽度信息。
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(ImagePath,options);
        //计算宽高要尽可能小于1024
        double ratio=Math.max(options.outWidth*1.0d/1024f,options.outHeight*1.0d/1024f);
        //设置图片缩放的倍数。假如设为 4 ，则宽和高都为原来的 1/4 ，则图是原来的 1/16 。
        options.inSampleSize=(int)Math.ceil(ratio);
        //我们这里并想让他显示图片所以这里要置为false
        options.inJustDecodeBounds=false;
        //利用Options的这些值就可以高效的得到一幅缩略图。
        myBitmapImage= BitmapFactory.decodeFile(ImagePath,options);
    }

    private void uploadUserImg(){
        if(ImagePath == null){
            Toast.makeText(this, "没有需要上传的图片", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "开始上传", Toast.LENGTH_LONG).show();
            final BmobFile bmobFile = new BmobFile(new File(ImagePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        user.setImage(bmobFile);
                        user.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Toast.makeText(ImgUploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                                    //将修改过的数据保存SharePreference中
                                    SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("url", user.getImage().getFileUrl());
                                    editor.putString("imgName", user.getImage().getFilename());
                                    editor.apply();

                                    //返回数据到上一个Activity
                                    Intent intent = new Intent();
                                    intent.putExtra("url", user.getImage().getFileUrl());
                                    intent.putExtra("imgName", user.getImage().getFilename());
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }else {
                                    e.printStackTrace();
                                    Toast.makeText(ImgUploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        e.printStackTrace();
                        Toast.makeText(ImgUploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    if(value == 80){
                        Toast.makeText(ImgUploadActivity.this, "正在努力上传中...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
