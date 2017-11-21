package com.tg.dippermerchant;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
/**
 * 输入付款金额并支付
 * @author Administrator
 *
 */
public class PayMoneyActivity extends BaseActivity {
	public static final String UID = "uid";
	public static final String UNAME = "uname";
	public static final String TYPE = "type";
	private EditText editMoney;
	private LinearLayout llSubmit;
	private int uid;
	private int type;
	private String uname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent != null){
			uid =intent.getIntExtra(UID,-1);
			type =intent.getIntExtra(TYPE,-1);
			uname= intent.getStringExtra(UNAME);
		}
		initView();
	}

	@Override
	protected boolean handClickEvent(View v) {
		String money = editMoney.getText().toString();
		if(money.length() == 0){
			ToastFactory.showToast(this, "金额不能为空");
			return false;
		}
		if(Float.parseFloat(money) == 0){
			ToastFactory.showToast(this, "金额不能小于零");
			return false;
		}
		if(type == 0){
			RequestConfig config = new RequestConfig(this, HttpTools.GET_PAY_CODE,"");
			RequestParams params = new RequestParams();
			params.put("payuid", uid);
			params.put("mshopid", ShoppingInfo.id);
			params.put("money", money);
			HttpTools.httpGet(Contants.URl.URl_3012, "/user/userpaymoney",
					config, params);
		}
		return super.handClickEvent(v);
	}

	private void initView() {
		llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
		llSubmit.setOnClickListener(singleListener);
		editMoney = (EditText) findViewById(R.id.edit_money);
		editMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		editMoney.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().contains(".")){
					if(s.length() - 1 -s.toString().indexOf(".") > 2){
						s = s.toString().subSequence(0, s.toString().indexOf(".")+3);
						editMoney.setText(s);
						editMoney.setSelection(s.length());
					}
				}
				if(s.toString().trim().substring(0).equals(".")){
					s = "." + s;
					editMoney.setText(s);
					editMoney.setSelection(2);
				}
				if(s.toString().startsWith("0")
						&& s.toString().trim().length() > 1){
					if(!s.toString().substring(1,2).equals(".")){
						editMoney.setText(s.subSequence(0,1));
						editMoney.setSelection(1);
						return;
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
					
			}
			
			@Override
			public void afterTextChanged(Editable s) {
					
			}
		});
	}
	
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if(code == 0){
			if(type == 0){
				ToastFactory.showToast(PayMoneyActivity.this, "收款成功");
				finish();
			}else if(type == 1){
				ToastFactory.showToast(PayMoneyActivity.this, "付款成功");
				finish();
			}
		}else{
			if(type == 0){
				ToastFactory.showToast(PayMoneyActivity.this, "收款失败");
			}else if(type == 1){
				ToastFactory.showToast(PayMoneyActivity.this, "付款失败");
			}
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_pay_money, null);
	}

	@Override
	public String getHeadTitle() {
		return "设置金额";
	}


}
