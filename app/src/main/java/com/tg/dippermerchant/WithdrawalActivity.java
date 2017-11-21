package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.dippermerchant.application.ManagementApplication;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.database.SharedPreferencesTools;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.info.WithdrawalRecordInfo;
import com.tg.dippermerchant.inter.NetworkRequestListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.object.SlideItemObj;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.NetWorkListDialog;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.view.spinnerwheel.WheelVerticalView;
import com.tg.dippermerchant.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 提现申请
 * 
 * @author Administrator
 * 
 */
public class WithdrawalActivity extends BaseActivity {
	private TextView tvBalance, tvWarmPrompt, tvBanktype,tvRecord;
	private LinearLayout llBankType;
	private EditText etSum;
	private NetWorkListDialog bankDialog;
	private String account;
	private String bankName;
	private String accountName;
	private float money;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		RequestParams params = new RequestParams();
		params.put("id", ShoppingInfo.id);
		HttpTools.httpGet(Contants.URl.URl_3013,"/merchant/getmerchantmoneyByid/"+ShoppingInfo.id, 
				new RequestConfig(this, HttpTools.GET_SHOPPING_INFO), params);
		
		RequestParams params1 = new RequestParams();
		params1.put("uId", ShoppingInfo.id);
		params1.put("page", 1);
		params1.put("pagesize", 100);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administratorBank", 
				new RequestConfig(this, HttpTools.GET_BANK_LIST), params1);
		
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.ll_bank_type://银行卡
			showBankDialog();
			break;
			case R.id.tv_record://查看提现记录
				startActivity(new Intent(WithdrawalActivity.this, WithdrawalRecordActivity.class));
			break;
		case id.right_layout://提交
			if(Float.parseFloat(tvBalance.getText().toString()) == 0){
				ToastFactory.showToast(this, "余额为零，不能提现");
				return false;
			}
			if(tvBanktype.getText().toString().length() == 0){
				/*DialogFactory.getInstance().showDialog(WithdrawalActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(WithdrawalActivity.this, AddBankActivity.class));
					}
				}, null, "您还没添加银行卡，请你添加银行卡", null, null);*/
				ToastFactory.showToast(this, "提现银行卡不能为空");
				return false;
			}
			if(etSum.getText().toString().length() == 0){
				ToastFactory.showToast(this, "提现金额不能为空");
				return false;
			}
			if(Float.parseFloat(etSum.getText().toString()) == 0){
				ToastFactory.showToast(this, "提现金额不能小于零");
				return false;
			}
			if(Float.parseFloat(etSum.getText().toString()) < 100){
				ToastFactory.showToast(this, "提现金额不能小于100");
				return false;
			}
			if(Float.parseFloat(etSum.getText().toString()) > money){
				ToastFactory.showToast(this, "提现金额不能大于余额");
				return false;
			}
			submit();
			break;
		}
		return super.handClickEvent(v);
	}
	/**
	 * 初始化
	 */
	private void initView() {
		tvBalance = (TextView) findViewById(R.id.tv_balance);
		tvWarmPrompt = (TextView) findViewById(R.id.tv_warm_prompt);
		tvBanktype = (TextView) findViewById(R.id.tv_bank_type);
		tvRecord = (TextView) findViewById(R.id.tv_record);
		llBankType = (LinearLayout) findViewById(R.id.ll_bank_type);
		etSum = (EditText) findViewById(R.id.et_sum);
		llBankType.setOnClickListener(singleListener);
		tvRecord.setOnClickListener(singleListener);
		String hintTitle = "温馨提示：";
		String hintContent = "\n1、平台结算日为每月15号,结算日后商家可对余额进行提现"
		+"\n2、每月每个商家的提现次数为1次"
		+"\n3、提现金额必须为整数，且大于100元"
		+"\n4、提现银行卡为默认银行卡";
		String str = hintTitle+hintContent;
		final SpannableStringBuilder sp = new  SpannableStringBuilder(str);    
		sp.setSpan(new AbsoluteSizeSpan(16, true),0, hintTitle.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
		tvWarmPrompt.setText(sp);
		etSum.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		etSum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 if (s.toString().contains(".")) {  
	                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {  
	                        s = s.toString().subSequence(0,  
	                                s.toString().indexOf(".") + 3);  
	                        etSum.setText(s);  
	                        etSum.setSelection(s.length());  
	                    }  
	                }  
	                if (s.toString().trim().substring(0).equals(".")) {  
	                    s = "0" + s;  
	                    etSum.setText(s);  
	                    etSum.setSelection(2);  
	                }  
	   
	                if (s.toString().startsWith("0")  
	                        && s.toString().trim().length() > 1) {  
	                    if (!s.toString().substring(1, 2).equals(".")) {  
	                    	etSum.setText(s.subSequence(0, 1));  
	                    	etSum.setSelection(1);  
	                        return;  
	                    }  
	                }  
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		tvBalance.setText(UserInfo.money+"");
	}
	
	/**
	 * 提现申请
	 */
	private void submit(){
		RequestConfig config = new RequestConfig(WithdrawalActivity.this,HttpTools.GET_SUBMIT_MONEY);
		RequestParams params = new RequestParams();
		params.put("money", etSum.getText().toString());
		params.put("bankname",bankName);
		params.put("eName",accountName);
		params.put("payaccount",account);
		params.put("mshopid",ShoppingInfo.id);
		HttpTools.httpPost(Contants.URl.URl_3013, "/merchantWithdrawals",config, params);
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_SHOPPING_INFO){
			JSONArray response = HttpTools.getContentJsonArray(jsonString);
			ResponseData data = HttpTools.getResponseContent(response);
			if(code == 0){
				money = data.getFloat("money");
				tvBalance.setText(""+money);
			}
		}else if(msg.arg1 == HttpTools.GET_BANK_LIST){
			String response = HttpTools.getContentString(jsonString);
			if (jsonString != null) {
				ResponseData data = HttpTools.getResponseData(response);
				for (int i = 0; i < data.length; i++) {
					if(data.length == 1 || data.getInt(i,"isdefault") == 1){
						bankName = data.getString(i, "name");
						accountName = data.getString(i, "accountName");
						account = data.getString(i, "account");
						tvBanktype.setText(account+"("+bankName+")");
					}
				}
			}
		}else{
			if(code == 0){
				ToastFactory.showToast(this,"提现申请成功");
				finish();
			}else {
				ToastFactory.showToast(this,message);
			}
		}
	}

	/**
	 * 选择银行卡
	 */
	private void showBankDialog() {
		if (bankDialog == null) {
			bankDialog = new NetWorkListDialog(this);
			bankDialog.setNetworkListener(new NetworkRequestListener() {
				@Override
				public void onSuccess(WheelVerticalView wheelView, Message msg,
						String response) {
					String jsonString = HttpTools.getContentString(response);
					if (jsonString != null) {
						ResponseData data = HttpTools.getResponseData(jsonString);
						ArrayList<SlideItemObj> list = wheelView.getList();
						list.clear();
						for (int i = 0; i < data.length; i++) {
							bankName = data.getString(i, "name");
							accountName = data.getString(i, "accountName");
							account = data.getString(i, "account");
							list.add(new SlideItemObj(account+"("+bankName+")", 
									data.getString(i, "account")));
						}
						bankDialog.notifyDataInvalidated();
					}
				}

				@Override
				public void onRequest(MessageHandler msgHand) {
					// TODO Auto-generated method stub
					RequestConfig config = new RequestConfig(WithdrawalActivity.this, 0);
					config.handler = msgHand.getHandler();
					RequestParams params = new RequestParams();
					params.put("uId", ShoppingInfo.id);
					params.put("page", 1);
					params.put("pagesize", 100);
					HttpTools.httpGet(Contants.URl.URl_3013,"/administratorBank", 
							config, params);
				}

				@Override
				public void onFail(Message msg, String message) {
					// TODO Auto-generated method stub

				}
			});
			bankDialog.setOnCompleteClickListener(new OnCompleteListener() {

				@Override
				public void onComplete(SlideItemObj item1, SlideItemObj item2) {
					// TODO Auto-generated method stub
					if (item1 != null) {
						tvBanktype.setText(item1.name);
						account = item1.id;
					}
				}
			});
		}
		bankDialog.show("选择银行卡", false);

	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_withdrawal, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText("提交");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "提现申请";
	}

}
