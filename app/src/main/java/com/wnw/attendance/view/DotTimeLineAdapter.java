package com.wnw.attendance.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivian.timelineitemdecoration.util.Util;
import com.wnw.attendance.R;
import com.wnw.attendance.bean.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by wnw on 2018/3/18.
 */

public class  DotTimeLineAdapter extends RecyclerView.Adapter<DotTimeLineAdapter.ViewHolder> {

    Context mContext;
    List<Event> mList;

    int[] colors = {0xffFFAD6C, 0xff62f434, 0xffdeda78, 0xff7EDCFF, 0xff58fdea, 0xfffdc75f};//颜色组

    public void setList(List<Event> list) {
        mList = list;
    }

    public DotTimeLineAdapter(Context context) {
        mContext = context;
    }

    public DotTimeLineAdapter(Context context, List<Event> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Date date = new Date(mList.get(position).getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(date);
        holder.timeTv.setText(time);
        holder.statusTv.setText(mList.get(position).getResult());
        holder.addressTv.setText(mList.get(position).getAddress());
        holder.timeTv.setTextColor(colors[1]);
        holder.statusTv.setTextColor(colors[1]);
        holder.addressTv.setTextColor(colors[1]);
        holder.itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "点击事件", Toast.LENGTH_SHORT).show();
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
}
