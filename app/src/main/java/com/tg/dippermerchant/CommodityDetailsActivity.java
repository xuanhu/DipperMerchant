package com.tg.dippermerchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.CommodityInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.object.ImageParams;
import com.tg.dippermerchant.object.SlideItemObj;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.CameraView;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.CameraView.STATE;
import com.tg.dippermerchant.view.MessageArrowView.ItemClickListener;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.color;
import com.tg.dippermerchant.R.drawable;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 商品详情页面
 * 
 * @author Administrator
 * 
 */
public class CommodityDetailsActivity extends BaseActivity implements ItemClickListener, OnClickListener {
	public static  final String COMMODITY = "commodity" ;
	private CommodityInfo info;
	private boolean needPostImage = false;
	private RelativeLayout rlAffirmPutaway;
	private MessageArrowView messageView;
	private ArrayList<ViewConfig> list = new ArrayList<ViewConfig>();
	private String headImgPath;
	private String imgUrl = "";
	private String name = "";
	private String describe = "";
	private String originalPrice ;
	private String sellingprice ;
	private String amount ;
	private TextView tvSubmit;
	private EditText etName, etOriginalPrice,etSellingPrice,etAmount,etDescribe;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent  != null){
			info = (CommodityInfo) intent.getSerializableExtra(COMMODITY);
		}
		if(info == null){
			ToastFactory.showToast(this, "参数有误");
			return ;
		}
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		etName = (EditText) findViewById(R.id.et_name);
		etOriginalPrice = (EditText) findViewById(R.id.et_originalprice);
		etSellingPrice = (EditText) findViewById(R.id.et_sellingprice);
		etAmount = (EditText) findViewById(R.id.et_amount);
		etDescribe = (EditText) findViewById(R.id.et_describe);

		imgUrl = info.imgUrl;
		name = info.name;
		describe = info.describe;
		originalPrice = String.valueOf(info.originalPrice);
		sellingprice = String.valueOf(info.sellingprice);
		amount = String.valueOf(info.amount);
		if(StringUtils.isNotEmpty(name)){//名称
			etName.setText(name);
		}
		if(StringUtils.isNotEmpty(originalPrice)){//供货价
			etOriginalPrice.setText(originalPrice);
		}
		if(StringUtils.isNotEmpty(sellingprice)){//零售价
			etSellingPrice.setText(sellingprice);
		}
		if(StringUtils.isNotEmpty(amount)){//库存
			etAmount.setText(amount);
		}
		if(StringUtils.isNotEmpty(describe)){//简介
			etDescribe.setText(describe);
		}
		rlAffirmPutaway = (RelativeLayout) findViewById(R.id.rl_affirm_putaway);
		tvSubmit = (TextView) findViewById(R.id.tv_submit);
		rlAffirmPutaway.setOnClickListener(this);
		if(info.state == 0){
			tvSubmit.setText("确认上架");
		}else {
			tvSubmit.setText("确认下架");
		}

		messageView = (MessageArrowView) findViewById(R.id.messageView);
		messageView.setItemClickListener(this);
		messageView.setEditable(true);

		int size = (int)(50 * Tools.getDisplayMetrics(this).density);
		list.clear();
		ViewConfig config = new ViewConfig("商品主图","",false);
		config.leftTextColor = R.color.text_color1;
		config.rightDrawable = getResources().getDrawable(R.drawable.zhanwei_commodity);
		config.rightImgWidth = size;
		config.rightImgHeight = size;
		config.rightImgScaleType = ImageView.ScaleType.CENTER_CROP;
		list.add(config);
		messageView.setData(list);
		if(!TextUtils.isEmpty(imgUrl)){
			VolleyUtils.getImageLoader(this).get(imgUrl, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg0.getBitmap() != null){
						list.get(0).rightDrawable = new BitmapDrawable(arg0.getBitmap());
						messageView.freshView(0);
					}
				}
			}, size, size);
		}
	}
	
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1 == HttpTools.SET_COMMODITY_INFO){
			int code = HttpTools.getCode(jsonString);
			if(code == 0 ){
				ToastFactory.showToast(this,"上架成功");
				finish();
			}else{
				ToastFactory.showToast(this,"上架失败，请稍后重试。");
			}
		}else if(msg.arg1 == HttpTools.POST_IMAG){
			needPostImage = false;
			imgUrl = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
			submitUserInfo();
		}
	}
	@Override
	public void returnData(CameraView cv, STATE state, int groupPosition,
			int childPosition, int position, Bitmap bitmap, String path) {
		super.returnData(cv, state, groupPosition, childPosition, position,
				bitmap, path);
		needPostImage = true;
		list.get(0).rightDrawable = new BitmapDrawable(bitmap);
		messageView.freshView(0);
	}
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		if (mv == messageView) {
			if (position == 0) {
				if (headImgPath == null) {
					headImgPath = getFilesDir().getAbsolutePath() + "/"+ "head.jpg";
				}
				DialogFactory.getInstance().showPhotoSelector(this, null,headImgPath, 0, 0, 0);
			}
		}
	}
	@Override
	public void onClick(View v) {
		if(messageView.isEditable()){
			if(needPostImage){
				ImageParams imgParams = new ImageParams();
				imgParams.fileName = "head.jpg";
				imgParams.path = headImgPath;
				HttpTools.postAnImage(Contants.URl.URl_3020,mHand, imgParams);
			}else{
				submitUserInfo();
			}
		}else{
			messageView.setEditable(true);
			messageView.setEditable(true);
		}
	}

	/**
	 * 提交信息
	 */
	private void submitUserInfo(){
		String url = null;
		if(!TextUtils.isEmpty(imgUrl)){
			Map<String, String> map = new HashMap<String, String>();
			map.put("imgUrl",imgUrl);
			map.put("isMain","1");
			url = mapToJson(map);
		}
		RequestConfig config = new RequestConfig(this, HttpTools.SET_COMMODITY_INFO);
		RequestParams params = new RequestParams();
		params.put("mshopId", ShoppingInfo.id);
		params.put("id", info.id);
		params.put("name", name);
		params.put("isPlatform", 1);
		params.put("amount", amount);
		params.put("describe", describe);
		params.put("originalPrice", originalPrice);
		params.put("sellingprice", sellingprice);
		params.put("type", 0);
		params.put("imgs", url);
		if(info.state == 0){//下架状态
			params.put("state", 1);
			config.hintString = "上架商品";
		}else if(info.state == 1){
			params.put("state", 0);
			config.hintString = "下架商品";
		}
		HttpTools.httpPut(Contants.URl.URl_3026, "/commodity", config,params);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_commodity_details,
				null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "商品详情";
	}

	/**
	 * map to json
	 *
	 * @param map
	 * @return
	 */
	public static String mapToJson(Map<String, String> map) {
		Gson gson = new Gson();
		String json = gson.toJson(map);
		return json;
	}
}
