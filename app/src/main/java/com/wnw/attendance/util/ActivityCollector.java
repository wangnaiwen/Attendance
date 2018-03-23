package com.wnw.attendance.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxx on 2017/3/27.
 *这个ActivityCollector主要是收集用户在注册过程中产生的三个Activity
 * 包括：RegisterEditNumAty, LRegisterGetCodesAty, RegisterSetPasswdAty三个Activity
 * 在用户注册完成后，应该调用这里的finishAllActivity();并且在每个Activity中，都要调用这个类的
 * addActivity方法，将该Activity添加进来，在用户的onDestroy()方法中调用removeActivity()方法
 * 将该Activity从ActivityCollector中删除
 *
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    /**添加一个Activity到Activities中*/
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    /**从activities中删除一个Activity*/
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    /**将这个activities中的所有Activity全部删除，这用户完成注册后调用这个方法*/
    public static void finishAllActivity(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
