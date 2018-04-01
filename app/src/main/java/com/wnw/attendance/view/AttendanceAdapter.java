package com.wnw.attendance.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wnw.attendance.R;
import com.wnw.attendance.bean.Attendance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wnw on 2018/4/1.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder>{

    private Context context;
    private List<Attendance> attendanceList;

    public AttendanceAdapter(Context context, List<Attendance> attendances) {
        this.context = context;
        this.attendanceList = attendances;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @Override
    public AttendanceAdapter.AttendanceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        Date startDate = new Date(attendance.getStartTime());
        Date endDate = new Date(attendance.getEndTime());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        holder.startTimeTv.setText("考勤开始：" + sdf.format(startDate));
        holder.endTimeTv.setText("考勤结束：" + sdf.format(endDate));
        holder.addressTv.setText("考勤地点：" + attendance.getAddress());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    class AttendanceHolder extends RecyclerView.ViewHolder{
        TextView startTimeTv;
        TextView endTimeTv;
        TextView addressTv;

        public AttendanceHolder(View itemView) {
            super(itemView);
            startTimeTv = (TextView)itemView.findViewById(R.id.start_time);
            endTimeTv = (TextView)itemView.findViewById(R.id.end_time);
            addressTv = (TextView)itemView.findViewById(R.id.address);
        }
    }
}
