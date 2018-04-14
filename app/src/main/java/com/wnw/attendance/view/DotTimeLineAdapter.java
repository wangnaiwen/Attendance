package com.wnw.attendance.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivian.timelineitemdecoration.util.Util;
import com.wnw.attendance.R;
import com.wnw.attendance.bean.Event;
import com.wnw.attendance.util.NetWorkUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by wnw on 2018/3/18.
 */

public class  DotTimeLineAdapter extends RecyclerView.Adapter<DotTimeLineAdapter.ViewHolder> {

    Activity mActivity;
    List<Event> mList;

    int[] colors = {0xffFFAD6C, 0xff62f434, 0xffdeda78, 0xff7EDCFF, 0xff58fdea, 0xfffdc75f};//颜色组

    public void setList(List<Event> list) {
        mList = list;
    }

    public DotTimeLineAdapter(Activity activity) {
        mActivity = activity;
    }

    public DotTimeLineAdapter(Activity activity, List<Event> list) {
        mActivity = activity;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (mList.get(position).getTime() == 0){
            Date date = new Date(mList.get(position).getStartTime());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(date);
            holder.timeTv.setText(time);
        }else{
            Date date = new Date(mList.get(position).getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(date);
            holder.timeTv.setText(time);
        }

        holder.statusTv.setText(mList.get(position).getResult());
        holder.addressTv.setText(mList.get(position).getAddress());
        holder.timeTv.setTextColor(colors[1]);
        holder.statusTv.setTextColor(colors[1]);
        holder.addressTv.setTextColor(colors[1]);
        holder.itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mList.get(position).getResult().equals("迟到")){
                    //Toast.makeText(mActivity, "你已经迟到", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mContext, "你已经迟到", Toast.LENGTH_SHORT).show();
                    //跳转到指纹考勤页面
                    startFingerPrintActivity(position);
                }else if (mList.get(position).getResult().equals("待打卡")){
                    //跳转到指纹识别页面
                    startFingerPrintActivity(position);
                }else{
                    //正常，请假等
                    Toast.makeText(mActivity, "已打卡：" + mList.get(position).getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLl;
        TextView statusTv;
        TextView timeTv;
        TextView addressTv;

        public ViewHolder(View view) {
            super(view);
            itemLl = (LinearLayout)view.findViewById(R.id.ll_item);
            statusTv = (TextView) view.findViewById(R.id.tv_status);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            addressTv = (TextView) view.findViewById(R.id.tv_address);
        }
    }

    private void startFingerPrintActivity(int position){
        int i = NetWorkUtils.getAPNType(mActivity);
        Log.e("wnw", i+"");
        if (i == 1){
            if(mList.get(position).getTime() == 0){
                Intent intent = new Intent(mActivity, FingerPrintActivity.class);
                intent.putExtra("event", mList.get(position));
                mActivity.startActivityForResult(intent, 1001);
            }else{
                Toast.makeText(mActivity, "已打卡："+mList.get(position).getResult(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(mActivity, "请你连接到Wifi", Toast.LENGTH_SHORT).show();
        }
    }
}
