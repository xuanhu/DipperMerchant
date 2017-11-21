package com.tg.dippermerchant;

import java.util.ArrayList;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.object.ImageParams;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.CameraView;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.CameraView.STATE;
import com.tg.dippermerchant.view.MessageArrowView.ItemClickListener;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R.color;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class UserInfoActivity extends BaseActivity implements ItemClickListener, OnClickListener {
	private MessageArrowView messageView1;
	private MessageArrowView messageView2;
	private ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
	private ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
	private boolean needPostImage = false;
	private String userUrl = "";
	private String name = "";
	private String cname = "";
	private String address = "";
	private String phone = "";
	private String linkman = "";
	private String Mobile = "";
	private String headImgPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageView1 = (MessageArrowView) findViewById(R.id.messageView1);
		messageView2 = (MessageArrowView) findViewById(R.id.messageView2);
		messageView1.setItemClickListener(this);
		messageView2.setItemClickListener(this);
		messageView1.setEditable(true);
		messageView2.setEditable(true);
		initView();
		RequestParams params = new RequestParams();
		params.put("adminId", UserInfo.uid);
		params.put("page", 1);
		params.put("pagesize", 1);
		HttpTools.httpGet(Contants.URl.URl_3013,"/merchant", 
				new RequestConfig(this, HttpTools.GET_SHOPPING_INFO), params);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		userUrl = ShoppingInfo.imgs;
		name=ShoppingInfo.name;
		cname=ShoppingInfo.cname;
		address=ShoppingInfo.address;
		phone=ShoppingInfo.phone;
		linkman=ShoppingInfo.linkman;
		Mobile=ShoppingInfo.Mobile;

		int size = (int)(50 * Tools.getDisplayMetrics(this).density);
		list1.clear();
		ViewConfig config = new ViewConfig("头像","",false);
		config.rightDrawable = getResources().getDrawable(R.drawable.moren_xinxiguanli);
		config.rightImgWidth = size;
		config.rightImgHeight = size;
		config.rightImgScaleType = ImageView.ScaleType.CENTER_CROP;
		list1.add(config);
		messageView1.setData(list1);
		
		
		list2.clear();
		config = new ViewConfig("商家名称",name,false);
		config.rightEditable = false;
		config.enable = false;
		list2.add(config);

		config = new ViewConfig("商家用户名",cname,false);
		config.rightEditable = false;
		config.enable = false;
		list2.add(config);

		config = new ViewConfig("商家地址",address,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);

		config = new ViewConfig("联系电话",phone,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);

		config = new ViewConfig("商家联系人",linkman,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);

		config = new ViewConfig("联系手机",Mobile,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);

		messageView2.setData(list2);
		if(!TextUtils.isEmpty(ShoppingInfo.imgs)){
			VolleyUtils.getImageLoader(this).get(ShoppingInfo.imgs, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg0.getBitmap() != null){
						list1.get(0).rightDrawable = new BitmapDrawable(arg0.getBitmap());
						messageView1.freshView(0);
					}
				}
			}, size, size);
		}
	}
	private void updateView(){
		list2.get(0).rightText = ShoppingInfo.name;
		list2.get(1).rightText = ShoppingInfo.cname;
		list2.get(2).rightText = ShoppingInfo.address;
		list2.get(3).rightText = ShoppingInfo.phone;
		list2.get(4).rightText = ShoppingInfo.linkman;
		list2.get(5).rightText = ShoppingInfo.Mobile;
		messageView2.freshAll();
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.SET_USER_INFO){
			headView.setRightText("保存");
			messageView1.setEditable(true);
			messageView2.setEditable(true);
			setUserInfo();
			updateView();
			ToastFactory.showToast(this, hintString);
			sendBroadcast(new Intent(MainActivity.ACTION_FRESH_USERINFO));
			finish();
		}else if(msg.arg1 == HttpTools.GET_SHOPPING_INFO){
			Log.d("print","jsonString="+jsonString);
			String response = HttpTools.getContentString(jsonString);
			ResponseData data = HttpTools.getResponseData(response);
			if(code == 0){
				if(Tools.loadShoppingInfo(data,jsonString)){
					updateView();
				}
			}
		}else if(msg.arg1 == HttpTools.POST_IMAG){
			needPostImage = false;
			userUrl = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
			submitUserInfo();
		}
	}
	
	private void setUserInfo(){
		ShoppingInfo.imgs = userUrl;
		ShoppingInfo.phone = phone;
		Tools.saveUserInfo(this);
	}
	
	@Override
	public void returnData(CameraView cv, STATE state, int groupPosition,
			int childPosition, int position, Bitmap bitmap, String path) {
		// TODO Auto-generated method stub
		super.returnData(cv, state, groupPosition, childPosition, position, bitmap,
				path);
		needPostImage = true;
		list1.get(0).rightDrawable = new BitmapDrawable(bitmap);
		messageView1.freshView(0);
	}
	
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		// TODO Auto-generated method stub
		if (mv == messageView1) {
			if (position == 0) {
				if (headImgPath == null) {
					headImgPath = getFilesDir().getAbsolutePath() + "/"+ "head.jpg";
				}
				DialogFactory.getInstance().showPhotoSelector(this, null,headImgPath, 0, 0, 0);
			}
		}else if(position == 1){

		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.back_layout){
			if(hasChanged()){//已经修改过信息
				DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}, null, "信息还没保存，确定要返回吗？", null, null);
			}else{
				finish();
			}
		}else {
			if(messageView1.isEditable()){
				if(needPostImage){
					ImageParams imgParams = new ImageParams();
					imgParams.fileName = "head.jpg";
					imgParams.path = headImgPath;
					HttpTools.postAnImage(Contants.URl.URl_3020,mHand, imgParams);
				}else{
					submitUserInfo();
				}
				
			}else{
				headView.setRightText("保存");
				messageView1.setEditable(true);
				messageView2.setEditable(true);
			}
		}
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	
	protected void backPress() {
		super.onDestroy();
		if(hasChanged()){//已经修改过信息
			DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			}, null, "信息还没保存，确定要返回吗？", null, null);
		}else{
			finish();
		}
	}
	private void submitUserInfo(){
		if(!hasChanged()){
			headView.setRightText("保存");
			messageView1.setEditable(true);
			messageView2.setEditable(true);
			return;
		}
		RequestConfig config = new RequestConfig(this, HttpTools.SET_USER_INFO);
		RequestParams params = new RequestParams
				("uid", Integer.toString(ShoppingInfo.id)).
				put("Icon",userUrl).
				put("realname",name).
				put("username",cname).
				put("address",address).
				put("tel",phone).
				put("linkman",linkman).
				put("Mobile",Mobile);
		config.hintString = "修改个人信息";
		HttpTools.httpPut(Contants.URl.URl_3013, "/administrator/merchant", config,params);
	}
	
	private boolean hasChanged(){
		name=messageView2.getRightTextString(0);
		cname=messageView2.getRightTextString(1);
		address=messageView2.getRightTextString(2);
		phone=messageView2.getRightTextString(3);
		linkman=messageView2.getRightTextString(4);
		Mobile=messageView2.getRightTextString(5);
		if(!TextUtils.equals(userUrl, ShoppingInfo.imgs)){
			return true;
		}
		if(!TextUtils.equals(name, ShoppingInfo.name)){
			return true;
		}if(!TextUtils.equals(cname, ShoppingInfo.cname)){
			return true;
		}if(!TextUtils.equals(address, ShoppingInfo.address)){
			return true;
		}
		if(!TextUtils.equals(phone, ShoppingInfo.phone)){
			return true;
		}
		if(!TextUtils.equals(linkman, ShoppingInfo.linkman)){
			return true;
		}
		if(!TextUtils.equals(Mobile, ShoppingInfo.Mobile)){
			return true;
		}
		return false;
	}
	
	
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_user_info, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("保存");
		headView.setRightTextColor(getResources().getColor(color.white));
		headView.setListenerRight(this);
		headView.setListenerBack(this);
		return "商家信息";
	}
}
