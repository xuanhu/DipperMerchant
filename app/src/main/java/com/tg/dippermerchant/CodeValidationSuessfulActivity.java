package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UseCedeInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 劵码验证成功
 * 
 * @author Administrator
 * 
 */
public class CodeValidationSuessfulActivity extends BaseActivity {
	public static final String USE_CEDE = "useCede";
	private TextView tvName,tvMoney,tvUser,tvValidationTime,tvUseCode;
	private Button btnUse;
	private String useCode;
	private ArrayList<UseCedeInfo> list = new ArrayList<UseCedeInfo>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent  = getIntent();
		if(intent != null){
			useCode = intent.getStringExtra(USE_CEDE);
		}
		
		if(useCode.length() == 0){
			ToastFactory.showToast(this, "参数有误");
			return;
		}
		getcodeByid();
		initView();
	}

	private void getcodeByid() {
		RequestParams params = new RequestParams();
		params.put("useCede",useCode);
		HttpTools.httpGet(Contants.URl.URl_3026,"/orders/getcodeByid", 
				new RequestConfig(this, HttpTools.GET_CODE_INFO), params);
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		tvUseCode = (TextView) findViewById(R.id.tv_useCode);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvMoney = (TextView) findViewById(R.id.tv_money);
		tvUser = (TextView) findViewById(R.id.tv_user);
		tvValidationTime = (TextView) findViewById(R.id.tv_validation_time);
		tvUseCode.setText("劵码密码："+useCode);
		btnUse = (Button) findViewById(R.id.btn_use);
		btnUse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestConfig config = new RequestConfig(CodeValidationSuessfulActivity.this,HttpTools.SET_REPAIR_INFO,"使用");
				RequestParams params = new RequestParams();
				params.put("mshopid",ShoppingInfo.id);
				params.put("useCede",useCode);
				HttpTools.httpPut(Contants.URl.URl_3026,"/orders/useorderscode" ,config, params);
			}
		});
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_CODE_INFO){
		JSONArray json = HttpTools.getContentJsonArray(jsonString);
		if(json != null){
			ResponseData data = HttpTools.getResponseContent(json);
			if(data.length > 0){
				UseCedeInfo item ;
				for (int i = 0; i < data.length; i++) {
					item = new UseCedeInfo();
					item.uname = data.getString(i, "uname");
					item.price = data.getString(i, "price");
					item.cName = data.getString(i, "cName");
					item.addTime = data.getString(i, "addTime");
					list.add(item);
				}
				tvName.setText(list.get(0).cName);
				tvMoney.setText(list.get(0).price);
				tvUser.setText(list.get(0).uname);
				tvValidationTime.setText(list.get(0).addTime);
			}
		}
		}else{
			if(code == 0){
				ToastFactory.showToast(CodeValidationSuessfulActivity.this,"使用成功");
				finish();
			}else{
				ToastFactory.showToast(CodeValidationSuessfulActivity.this,message);
			}
		}
	}
	
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_code_validation_suessful, null);
	}

	@Override
	public String getHeadTitle() {
		return "验证成功";
	}

}
