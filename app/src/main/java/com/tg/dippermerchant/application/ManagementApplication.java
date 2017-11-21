package com.tg.dippermerchant.application;


import im.fir.sdk.FIR;
import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.tg.dippermerchant.database.SharedPreferencesTools;
import com.tg.dippermerchant.log.Logger;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.LoginActivity;
import com.tg.dippermerchant.R;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class ManagementApplication extends Application{
	private List<Activity> mList = new LinkedList<Activity>();
	private static ManagementApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		FIR.init(this);
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
       	JPushInterface.setLatestNotificationNumber(this, 5);
		Logger.logd("WisdomParkApplication onCreate");
		Tools.mContext = getApplicationContext();
		Tools.userHeadSize = getResources().getDimensionPixelSize(R.dimen.margin_80);
		ResponseData data = SharedPreferencesTools.getUserInfo(Tools.mContext);
		Tools.loadUserInfo(data, null);
		ResponseData data1 = SharedPreferencesTools.getShoppingInfo(Tools.mContext);
		Tools.loadShoppingInfo(data1, null);
	}
	 public static ManagementApplication getInstance() {

         if (instance == null)
             instance = new ManagementApplication();

         return instance;
     }
	public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null){
                	activity.finish(); 
                }
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
        	mList.clear();
            System.exit(0); 
        } 
    } 
	
	 public static Activity getCurrentActivity(Context context){
		 ManagementApplication application = (ManagementApplication)context.getApplicationContext();
		 return application.currentActivity();
	 }
	 
	 public static void gotoLoginActivity(Activity activity){
		 ManagementApplication application = (ManagementApplication)activity.getApplication();
		 application.goLoginActivity(activity);
	 }
	 
	 public Activity currentActivity(){
		 if(mList.size() == 0){
			 return null;
		 }
		 return mList.get(mList.size() -1);
	 }
	
	 public void add(Activity activity) { 
	     mList.add(activity); 
	 } 
	 
	 public void remove(Activity activity){
		 mList.remove(activity);
	 }
	 
	 private void finishOtherActivity(Class<? extends Activity> clazs){
		 Activity activity;
		 for (int i = 0 ;i < mList.size(); i ++) { 
			 activity = mList.get(i);
             if (activity != null){
            	 if(i == mList.size() - 1 && activity.getClass() == clazs){
            	 }else{
            		 activity.finish(); 
            	 }
             }
         } 
	 }
	 
	 public void goLoginActivity(Activity activity){
		 activity.startActivity(new Intent(activity,LoginActivity.class));
		 finishOtherActivity(LoginActivity.class);
	 }
	 public void onLowMemory() { 
	     super.onLowMemory();     
	     System.gc(); 
	 }
	 
	 public static void addActivity(Activity activity){
		 ManagementApplication application = (ManagementApplication)activity.getApplication();
		 application.add(activity);
	 }
	 
	 public static void removeActivity(Activity activity){
		 ManagementApplication application = (ManagementApplication)activity.getApplication();
		 application.remove(activity);
	 }
	 
	 public static void exitApp(Context context){
		 ManagementApplication application = (ManagementApplication)context.getApplicationContext();
		 application.exit();
	 }
}
