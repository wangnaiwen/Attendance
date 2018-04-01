package com.wnw.attendance.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;
import com.wnw.attendance.bean.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wnw on 2018/4/1.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder>{
    private Context context;
    private List<Record> recordList;

    public RecordAdapter(Context context, List<Record> records) {
        this.context = context;
        this.recordList = records;
    }

    public void setRecordList(List<Record> records) {
        this.recordList = records;
    }

    @Override
    public RecordAdapter.RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        return new RecordAdapter.RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordAdapter.RecordHolder holder, int position) {
        Record record = recordList.get(position);
        Date date = new Date(record.getRecordTime());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        holder.timeTv.setText("考勤时间：" + sdf.format(date));
        holder.resultTv.setText("考勤结果：" + record.getResult());
        holder.addressTv.setText("考勤地点：" + record.getAddress());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class RecordHolder extends RecyclerView.ViewHolder{
        TextView timeTv;
        TextView addressTv;
        TextView resultTv;

        public RecordHolder(View itemView) {
            super(itemView);
            timeTv = (TextView)itemView.findViewById(R.id.time);
            resultTv = (TextView)itemView.findViewById(R.id.result);
            addressTv = (TextView)itemView.findViewById(R.id.address);
        }
    }
}
