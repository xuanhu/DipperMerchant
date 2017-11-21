package com.tg.dippermerchant;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.TimingButton;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
/**
 * 忘记密码页面
 * 
 * @author Administrator
 * 
 */
public class ForgetPasswordActivity extends BaseActivity {
	private EditText editCode;
	private EditText editUser;
	private EditText editPwd1;
	private EditText editPwd2;
	private TimingButton timingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		editCode = (EditText) findViewById(R.id.edit_code);
		editUser = (EditText) findViewById(R.id.edit_user);
		editPwd1 = (EditText) findViewById(R.id.edit_password);
		editPwd2 = (EditText) findViewById(R.id.edit_password2);
		timingButton = (TimingButton) findViewById(R.id.btn_get_code);
		timingButton.setOnClickListener(singleListener);
	}

	@Override
	protected boolean handClickEvent(View v) {
		// TODO Auto-generated method stub
		String phone = editUser.getText().toString();
		String pwd1;
		String pwd2;
		String code;
		if (v.getId() == R.id.right_layout) {
			if (!Tools.checkTelephoneNumber(phone)) {
				ToastFactory.showToast(this, "请输入11位正确的手机号");
				return false;
			}
			code = editCode.getText().toString();
			if (code.length() < 5) {
				ToastFactory.showToast(this, "请输入5位的验证码");
				return false;
			}
			pwd1 = editPwd1.getText().toString();
			if (pwd1.length() < 6) {
				ToastFactory.showToast(this, "请设置不少于6位的密码");
				return false;
			}
			pwd2 = editPwd2.getText().toString();
			if (!pwd2.equals(pwd1)) {
				ToastFactory.showToast(this, "确认密码和密码不一致");
				return false;
			}
			RequestConfig config = new RequestConfig(this,
					HttpTools.SET_PASSWORD);
			config.hintString = "修改密码";
			RequestParams params = new RequestParams("code", code);
			params.put("newpwd", pwd1);
			params.put("mobile", phone);
			HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/modfiypwd", config, params);
		} else {
			if (!Tools.checkTelephoneNumber(phone)) {
				ToastFactory.showToast(this, "请输入11位正确的手机号");
				return false;
			}
			RequestConfig config = new RequestConfig(this, HttpTools.GET_CODE);
			config.hintString = "请求验证码";
			RequestParams params = new RequestParams().put("mobile", phone)
					.put("type", 2);
			HttpTools.httpGet(Contants.URl.URl_3011, "/sms/code", config, params);
		}
		return super.handClickEvent(v);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if (msg.arg1 == HttpTools.GET_CODE) {
			if (code == 0) {
				ToastFactory.showToast(this, "验证码获取成功");
				timingButton.startTiming();
			}else {
				ToastFactory.showToast(this, "验证码获取失败");
			}
			String sms = null;
			JSONObject dataCount = HttpTools.getContentJSONObject(jsonString);
			try {
				if(dataCount != null){
					sms = dataCount.getString("smscode");
					Log.d("TAG", "sms=" + sms);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			if(code == 0){
				ToastFactory.showToast(this, "密码设置成功");
				finish();
			}else {
				ToastFactory.showToast(this, "密码设置失败");
			}
			
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_forget_password,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("提交");
		headView.setListenerRight(singleListener);
		headView.setRightTextColor(getResources().getColor(R.color.white));
		return "忘记密码";
	}

}
