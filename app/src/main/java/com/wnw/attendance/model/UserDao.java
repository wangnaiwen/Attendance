package com.wnw.attendance.model;

import com.wnw.attendance.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wnw on 2018/3/13.
 */

public class UserDao {
    /**
     * 登录
     * */
    private static UserDao mInstance;
    private UserDao(){

    }

    public static UserDao getInstance(){
        if (mInstance == null){
            mInstance = new UserDao();
        }
        return mInstance;
    }


    boolean login(String sId, String password){
        final boolean[] result = {false};
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("sId", sId);
        query.addWhereEqualTo("password", password);
        query.setLimit(1);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    if(list != null && list.size() > 0){
                        result[0] = true;
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
        return result[0];
    }

}
