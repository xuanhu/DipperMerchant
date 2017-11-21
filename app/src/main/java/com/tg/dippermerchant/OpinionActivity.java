package com.tg.dippermerchant;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R.color;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
/**
 * 意见反馈
 * @author Administrator
 *
 */
public class OpinionActivity extends BaseActivity {
	private EditText editOpinion;
	private String opinion = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		editOpinion = (EditText)findViewById(R.id.edit_opinion);
	}
	@Override
	protected boolean handClickEvent(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.right_layout://提交
				opinion = editOpinion.getText().toString();
				if(opinion.length() == 0){
					ToastFactory.showToast(this, "请填写反馈内容");
					return false;
				}
				if(opinion.length() <5){
					ToastFactory.showToast(this, "你输入的内容少于5个字，请重新输入");
					return false;
				}
				if(opinion.length() > 100){
					ToastFactory.showToast(this, "反馈内容不能大于100字");
					return false;
				}
				submit();
				break;
		}
		
		return super.handClickEvent(v);
	}
	/**
	 * 提交
	 */
	private void submit() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_OPINION_INFO,"提交信息");
		RequestParams params = new RequestParams();
		params.put("founder",UserInfo.realname);
		params.put("content",editOpinion.getText().toString());
		HttpTools.httpPost(Contants.URl.URl_3014,"/feedback" ,config, params);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(code == 0){
			ToastFactory.showToast(this,"您的反馈已收到，感谢您的参与");
			finish();
		}else{
			ToastFactory.showToast(this,message);
		}

	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_opinion,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("提交");
		headView.setListenerRight(singleListener);
		headView.setRightTextColor(getResources().getColor(color.white));
		return "意见反馈";
	}

}
