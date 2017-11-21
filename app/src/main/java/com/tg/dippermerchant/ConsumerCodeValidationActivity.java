package com.tg.dippermerchant;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.MipcaActivityCapture;
import com.tg.dippermerchant.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 劵码验证
 * 
 * @author Administrator
 * 
 */
public class ConsumerCodeValidationActivity extends BaseActivity {
	private EditText etValidationPassword;
	private ImageView ivSaomaValidation;
	private Button btnValidationPassword;
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.right_layout://记录
			startActivity(new Intent(this, ConsumerCodeManageMentActivity.class));
			break;
		case R.id.iv_saoma_validation:// 点击扫码
			startActivity(new Intent(this, MipcaActivityCapture.class));
			break;
		case R.id.btn_validation_password:// 点击验证
			content = etValidationPassword.getText().toString();
			if(content.length() == 0){
				ToastFactory.showToast(this, "请输入验证密码");
				return false;
			}
			RequestConfig config = new RequestConfig(this,HttpTools.SET_REPAIR_INFO,"验证");
			RequestParams params = new RequestParams();
			params.put("mshopid",ShoppingInfo.id);
			params.put("useCede",content);
			HttpTools.httpGet(Contants.URl.URl_3026,"/orders/checkcode" ,config, params);
			break;
		}
		return super.handClickEvent(v);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		etValidationPassword = (EditText) findViewById(R.id.et_validation_password);
		ivSaomaValidation = (ImageView) findViewById(R.id.iv_saoma_validation);
		btnValidationPassword = (Button) findViewById(R.id.btn_validation_password);
		ivSaomaValidation.setOnClickListener(singleListener);
		btnValidationPassword.setOnClickListener(singleListener);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(code == 0){
			ToastFactory.showToast(ConsumerCodeValidationActivity.this,"验证成功");
			Intent intent  = new Intent(this, CodeValidationSuessfulActivity.class);
			intent.putExtra(CodeValidationSuessfulActivity.USE_CEDE,content);
			startActivity(intent);
		}else{
			ToastFactory.showToast(ConsumerCodeValidationActivity.this,message);
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		etValidationPassword.setText("");
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(
				R.layout.activity_consumer_code_validation, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText("记录");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "劵码验证";
	}

}
