package com.tg.dippermerchant;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.tg.dippermerchant.application.ManagementApplication;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.fragment.FragmentMine;
import com.tg.dippermerchant.fragment.FragmentHome;
import com.tg.dippermerchant.fragment.FragmentMessage;
import com.tg.dippermerchant.fragment.FragmentNews;
import com.tg.dippermerchant.info.AdvInfo;
import com.tg.dippermerchant.info.HomeInfo;
import com.tg.dippermerchant.info.NewsTypeInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.log.Logger;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.net.MessageHandler.ResponseListener;
import com.tg.dippermerchant.serice.MessageService;
import com.tg.dippermerchant.updateapk.ApkInfo;
import com.tg.dippermerchant.updateapk.UpdateManager;
import com.tg.dippermerchant.util.ExampleUtil;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ResponseListener, OnTabChangeListener {
	private static final String TAG = "JPush";
	public static final String ACTION_FRESH_USERINFO = "com.tg.dippermerchant.ACTION_FRESH_USERINFO";
	public static final String ACTION_GET_USERINFO = "com.tg.dippermerchant.ACTION_GET_USERINFO";
	public static final String ACTION_UPDATE_PUSHINFO = "com.tg.coloursteward.ACTION_UPDATE_PUSHINFO";
	public static final String KEY_NEDD_FRESH = "need_fresh";
	private String firToken = "4f3679044ea0219891240d11ec4909fa";// fir.im 的用户
	public static final String KEY_EXTRAS = "extras";
	private TabHost mTabHost;
	private boolean exit = false;//是否退出
	private boolean needGetUserInfo = true;
	public static int CODE_IMAGE_WIDTH;
    private ArrayList<AdvInfo> list = new ArrayList<AdvInfo>();
    private ArrayList<NewsTypeInfo> listNews = new ArrayList<NewsTypeInfo>();
    private ArrayList<HomeInfo> listhome = new ArrayList<HomeInfo>();
	private String extras;
	private Fragment fragments[] = { new FragmentHome(), new FragmentNews(), new FragmentMessage(), new FragmentMine() };
	private String tabTexts[] = { "主页", "消息", "留言","我的" };//商家
	private MessageHandler msgHand;
	private ImageView iv_unreadnum;//未读消息红点
	private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_FRESH_USERINFO)) {
				if(UserInfo.admintype == 10){
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment1 = frgManager.findFragmentByTag(tabTexts[3]);
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentHome elehomeFrag = (FragmentHome) fragment;
						//elehomeFrag.freshUI();
					}
					if (fragment1 != null) {
						FragmentMine eleMineFrag = (FragmentMine) fragment1;
						eleMineFrag.freshUI();
					}
				}
			}else if(action.equals(ACTION_UPDATE_PUSHINFO)){//更新推送首页消息列表
				getNewsInfo();
			}
		}
	};

	private Runnable getUserInfoRunnable = new Runnable() {
		public void run() {
			getUserInfo();
		}
	};
    private Runnable getAdRunnable = new Runnable() {
        public void run() {
            getAdInfo();
        }
    };
    private Runnable getHomeRunnable = new Runnable() {
        public void run() {
			getHomeInfo();
        }
    };
	private Runnable getShoppingInfoRunnable = new Runnable() {
		public void run() {
			getShoppingInfo();
		}
	};
	private Handler hand = new Handler() {
		public void handleMessage(Message msg) {

		}
	};
	
	public Handler getHandler() {
		return msgHand.getHandler();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 版本检测更新
		 */
		FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
			@Override
			public void onSuccess(String versionJson) {
				try {
					JSONObject obj = new JSONObject(versionJson);
					String apkVersion = obj.getString("versionShort");
					int apkCode = obj.getInt("version");
					String apkSize = obj.getString("updated_at");
					String apkName = obj.getString("name");
					String downloadUrl = obj.getString("installUrl");
					String apkLog = obj.getString("changelog");
					ApkInfo apkinfo = new ApkInfo(downloadUrl, apkVersion,apkSize, apkCode, apkName, apkLog);
					if (apkinfo != null) {
						SharedPreferences mySharedPreferences= getSharedPreferences("versions",0);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putString("versionShort", apkVersion);
						editor.commit(); 
						UpdateManager manager = new UpdateManager(MainActivity.this,true);
						// 检查软件更新
						manager.checkUpdate(apkinfo);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		/**
		 * 开启服务
		 */
		//startService(new Intent(MainActivity.this, MessageService.class));
		/**
		 * 设置极光推送别名与标签
		 */
		Boolean tags = Tools.getBooleanValue(MainActivity.this,Contants.storage.Tags);
		Boolean alias = Tools.getBooleanValue(MainActivity.this,Contants.storage.ALIAS);
		setAlias();
		if(tags == false){
			//setTag();
		}
		if(alias == false){
			//setAlias();

		}
		ManagementApplication.addActivity(this);
		msgHand = new MessageHandler(this);
		msgHand.setResponseListener(this);
		setContentView(R.layout.activity_main);
		Tools.setMainStatus(MainActivity.this,true);
		//透明导航栏
		//	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		windowPermission();
		Intent data = getIntent();
		if (data != null) {
			needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
			extras  = data.getStringExtra(KEY_EXTRAS);
		}
		if(UserInfo.admintype == 10 ){
			getShoppingInfo();
		}
		initView();
        getAdInfo();
		//getHomeInfo();
		getNewsInfo();
		if (needGetUserInfo) {
			hand.postDelayed(getUserInfoRunnable, 3000);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FRESH_USERINFO);
		filter.addAction(ACTION_GET_USERINFO);
		filter.addAction(ACTION_UPDATE_PUSHINFO);
		registerReceiver(freshReceiver, filter);
		//推送跳转详情
		pushDetail();
	}
	private void pushDetail() {
		Log.d("printLog","pushDetail()  extras="+extras);
		/*if (extras != null) {
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String client_code = jsonObject.getString("client_code");
				String msgid = jsonObject.getString("msgid");
				String auth_type = jsonObject.getString("auth_type");
				String msgtype = jsonObject.getString("msgtype");
				String url = jsonObject.getString("url");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String extras = intent.getStringExtra(KEY_EXTRAS);
		Log.d("printLog","onNewIntent   extras="+extras);
		/*if (extras != null) {
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String client_code = jsonObject.getString("client_code");
				String msgid = jsonObject.getString("msgid");
				String auth_type = jsonObject.getString("auth_type");
				String msgtype = jsonObject.getString("msgtype");
				String url = jsonObject.getString("url");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
	}
	public void windowPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (!Settings.canDrawOverlays(getApplicationContext())) {
				//启动Activity让用户授权
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
				if (intent != null){
					intent.setData(Uri.parse("package:" + getPackageName()));
					startActivity(intent);
				}
				return;
			} else {
				//执行6.0以上绘制代码
			}
		} else {
			//执行6.0以下绘制代码
		}

		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION}, Activity.RESULT_FIRST_USER);
		}

	}
    private void getAdInfo() {
			HttpTools.httpGet(Contants.URl.URl_3011,"/ad/app", new RequestConfig(this,
					HttpTools.GET_AD_LIST, null), new RequestParams("Positionalias","shopindex"));
    }
    private void getHomeInfo() {
        if(ShoppingInfo.id !=  -1){
            HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/getmerchanthome", new RequestConfig(this,
                    HttpTools.GET_HOME_INFO, null), new RequestParams("uid", ShoppingInfo.id));
        }else{
			hand.removeCallbacks(getShoppingInfoRunnable);
			hand.post(getShoppingInfoRunnable);
        }
    }
	private void getUserInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO,null);
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.uid);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/"+UserInfo.uid, config, params);
	}
	private void getShoppingInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_SHOPPING_INFO,null);
		RequestParams params = new RequestParams();
		params.put("adminId", UserInfo.uid);
		params.put("page", 1);
		params.put("pagesize", 1);
		HttpTools.httpGet(Contants.URl.URl_3013,"/merchant", config, params);
	}
	private void getNewsInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_NEWS_INFO,null);
		RequestParams params = new RequestParams();
		params.put("uid", ShoppingInfo.id);
		params.put("type", 2);//1员工，2商家
		params.put("page", 1);
		params.put("pagesize", 10);
		HttpTools.httpGet(Contants.URl.URl_3011, "/homepush",config, params);
	}

    /**
     * 更新首页轮播图
     */
    public void freshAdInfo() {
        hand.removeCallbacks(getAdRunnable);
        hand.post(getAdRunnable);
    }
    /**
     * 更新首页信息
     */
    public void freshHomeInfo() {
        hand.removeCallbacks(getHomeRunnable);
        hand.post(getHomeRunnable);
    }
	private void initView() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.getTabWidget().setDividerDrawable(android.R.color.transparent);
		TabHost.TabSpec tab;
		int[] resIDs = { R.drawable.tab1_selector,R.drawable.tab2_selector,
					R.drawable.tab3_selector, R.drawable.tab4_selector };
			for (int i = 0; i < tabTexts.length; i++) {
				if(i == 1){
					tab = mTabHost.newTabSpec(tabTexts[i]).setIndicator(getTabViewDot(resIDs[i], tabTexts[i])).setContent(android.R.id.tabcontent);
				}else{
					tab = mTabHost.newTabSpec(tabTexts[i]).setIndicator(getTabView(resIDs[i], tabTexts[i])).setContent(android.R.id.tabcontent);
				}
				mTabHost.addTab(tab);
			}
			mTabHost.setOnTabChangedListener(this);
			mTabHost.setCurrentTabByTag(tabTexts[0]);
			showFragment(tabTexts[0]);
	}
	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_AD_LIST) {// 广告
            JSONObject objectString = HttpTools.getContentJSONObject(jsonString);
            hand.removeCallbacks(getAdRunnable);
            hand.postDelayed(getAdRunnable, 60 * 1000 * 60 * 2);
            if (objectString == null || objectString.length() == 0) {
                return;
            }
            list.clear();
            try {
                int weight = objectString.getInt("weight");
                int height = objectString.getInt("height");
                JSONArray jarray = objectString.getJSONArray("data");
                ResponseData data = HttpTools.parseJsonArray(jarray);
                AdvInfo adInfo;
                for (int i = 0; i < data.length; i++) {
                    adInfo = new AdvInfo();
                    adInfo.weight = weight;
                    adInfo.height = height;
                    adInfo.pid = data.getInt(i, "pid");
                    adInfo.pName = data.getString(i, "pname");
                    adInfo.imgUrl = data.getString(i, "imgurl");
                    adInfo.url = data.getString(i, "url");
                    adInfo.body = data.getString(i, "body");
                    list.add(adInfo);
                }
                setAdvData();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if (msg.arg1 == HttpTools.GET_USER_INFO) {
            String response = HttpTools.getContentString(jsonString);
            ResponseData data = HttpTools.getResponseData(response);
			Tools.loadUserInfo(data,jsonString);
			sendBroadcast(new Intent(ACTION_FRESH_USERINFO));
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 10 * 60 * 1000);
		} else if(msg.arg1 == HttpTools.GET_SHOPPING_INFO){//商家信息
            String response = HttpTools.getContentString(jsonString);
            ResponseData data = HttpTools.getResponseData(response);
			if(code == 0){
				Tools.loadShoppingInfo(data, jsonString);
				if(listhome.size() == 0){
					freshHomeInfo();
				}
				hand.removeCallbacks(getShoppingInfoRunnable);
				hand.postDelayed(getShoppingInfoRunnable, 10 * 60 * 1000);
			}
		}else if(msg.arg1 == HttpTools.GET_HOME_INFO){//首页信息
			hand.removeCallbacks(getHomeRunnable);
			hand.postDelayed(getHomeRunnable, 60 * 1000 * 60 * 2);
			if(code == 0){
				listhome.clear();
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				if(content != null){
					HomeInfo info = new HomeInfo();
					try {
						info.orders = content.getInt("orders");
						info.shipments = content.getInt("shipments");
						info.money = content.getDouble("money");
						listhome.add(info);
						setHomeData();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				ToastFactory.showToast(MainActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_NEWS_INFO){//推送消息
			if(code == 0){
				String response = HttpTools.getContentString(jsonString);
				if (response != null) {
					ResponseData data = HttpTools.getResponseData(response);
					NewsTypeInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new NewsTypeInfo();
						item.id = data.getInt(i, "id");
						item.homePushPeople = data.getInt(i, "homePushPeople");
						item.showType = data.getInt(i, "showType");
						item.isHTML5url = data.getInt(i, "isHTML5url");
						item.isPC = data.getInt(i, "isPC");
						item.notread = data.getInt(i, "notread");
						item.content = data.getString(i, "content");
						item.icon = data.getString(i, "icon");
						item.homePushUrl = data.getString(i,"homePushUrl");
						item.homePushTime = data.getString(i, "homePushTime");
						item.weiappcode = data.getString(i, "weiappcode");
						item.weiappname = data.getString(i, "weiappname");
						item.HTML5url = data.getString(i, "HTML5url");
						item.PCurl = data.getString(i, "PCurl");
						item.secretKey = data.getString(i, "secretKey");
						item.tookiy = data.getString(i, "tookiy");
						item.keystr = data.getString(i, "keystr");
						listNews.add(item);
					}
				}
				getNotReadNum();
			}else{
				ToastFactory.showToast(MainActivity.this,message);
			}
		}
	}
	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		if (msg.arg1 == HttpTools.GET_USER_INFO) {
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 60 * 1000);
		}
	}
	/**
	 * 获取未读消息
	 */
	private void getNotReadNum(){
		int notNum = 0;
		for (int i = 0; i < listNews.size(); i++){ //外循环是循环的次数
			notNum += listNews.get(i).notread;
		}
		/**
		 * 未读消息数量更新
		 */
		UpdataUnreadNum(notNum);
	}
    /**
     * 首页轮播图
     */
    public void setAdvData() {
        if (list.size() == 0) {
            return;
        }
        FragmentManager frgManager = getSupportFragmentManager();
        Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
        if (fragment != null) {
            FragmentHome homeFrag = (FragmentHome) fragment;
            homeFrag.setAdvList(list);
        }
    }
	/**
	 * 首页数据（订单，金额，待发货）
	 */
	public void setHomeData() {
		if (listhome.size() == 0) {
			return;
		}
		FragmentManager frgManager = getSupportFragmentManager();
		Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
		if (fragment != null) {
			FragmentHome homeFrag = (FragmentHome) fragment;
			homeFrag.setHomeList(listhome);
		}
	}
	public View getTabViewDot(int resId, String tab) {
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.tab_layout, null);
		ImageView img = (ImageView) v.findViewById(R.id.tab_img);
		iv_unreadnum = (ImageView) v.findViewById(R.id.tv_new_xinxi);
		TextView tabText = (TextView) v.findViewById(R.id.tab_text);
		iv_unreadnum.setVisibility(View.GONE);
		tabText.setText(tab);
		img.setImageResource(resId);
		return v;
	}
	public View getTabView(int resId, String tab) {
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.tab_layout, null);
		ImageView img = (ImageView) v.findViewById(R.id.tab_img);
		TextView tabText = (TextView) v.findViewById(R.id.tab_text);
		tabText.setText(tab);
		img.setImageResource(resId);
		return v;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		showFragment(tabId);
	}
	private void showFragment(String tabId) {
		Fragment fragment;
		FragmentManager frgManager = getSupportFragmentManager();
		FragmentTransaction transaction = frgManager.beginTransaction();
		if(UserInfo.admintype == 10){
			for (int i = 0; i < tabTexts.length; i++) {
				fragment = frgManager.findFragmentByTag(tabTexts[i]);
				if (tabId.equals(tabTexts[i])) {
					if (fragment == null) {
						transaction.add(R.id.contentLayout, fragments[i],tabTexts[i]);
						fragment = fragments[i];
					}
					transaction.show(fragment);
				} else {
					if (fragment != null) {
						transaction.hide(fragment);
					}
				}
			}
			transaction.commit();	
		}
	}

	public void UpdataUnreadNum(int UnreadNum) {
		if (iv_unreadnum != null) {
			if (UnreadNum <= 0) {
				iv_unreadnum.setVisibility(View.GONE);
			} else {
				iv_unreadnum.setVisibility(View.VISIBLE);
			}
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		int tabId = mTabHost.getCurrentTab();
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	
	Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			exit = false;
		}
	};
	
	/**
	 * 退出程序
	 */
	private void backPress() {
		if (exit) {
			Intent intent = new Intent();
			Tools.setMainStatus(MainActivity.this,false);
	        // 关闭该Service
			stopService(new Intent(this, MessageService.class));
			hand.removeCallbacksAndMessages(null);
			ManagementApplication.exitApp(this);
		} else {
			exit = true;
			ToastFactory.showBottomToast(this, "再按一次退出程序");
			hand.postDelayed(run, 2500);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case Activity.RESULT_FIRST_USER:
				break;
		}
	}
	/**
	 * 极光推送设置
	 */
	private void setTag(){

		Set<String> set = new HashSet<String>();
		String s1 = "merchant";
		set.add(s1);
		//调用JPush API设置Tag
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, set));

	}

	private void setAlias(){
		String alias  = ""+ShoppingInfo.id;
		//调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					Logger.logd(TAG,"alias   设置成功   code="+code);
					Tools.setBooleanValue(MainActivity.this,Contants.storage.ALIAS,true);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}
		}

	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					Logger.logd(TAG,"tag   设置成功   code="+code);
					Tools.setBooleanValue(MainActivity.this,Contants.storage.Tags,true);
					break;
				case 6002:
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}
		}

	};
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;



	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
					break;

				case MSG_SET_TAGS:
					JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
					break;

				default:
			}
		}
	};
}
