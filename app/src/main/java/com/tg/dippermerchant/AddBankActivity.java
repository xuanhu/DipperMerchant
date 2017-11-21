package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.NetworkRequestListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.object.SlideItemObj;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.MessageArrowView.ItemClickListener;
import com.tg.dippermerchant.view.dialog.NetWorkListDialog;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.view.spinnerwheel.WheelVerticalView;
import com.tg.dippermerchant.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 添加银行卡
 * 
 * @author Administrator
 * 
 */
public class AddBankActivity extends BaseActivity implements ItemClickListener {
	private MessageArrowView messageView;
	private ArrayList<ViewConfig> list = new ArrayList<ViewConfig>();
	private NetWorkListDialog bankDialog;
	private TextView tvWarmPrompt;
	private LinearLayout llSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	@Override
	protected boolean handClickEvent(View v) {
		String bankName = messageView.getRightTextString(0);
		if (bankName.length() == 0 || bankName.equals("请选择")) {
			ToastFactory.showToast(AddBankActivity.this, "请选择开户银行");
			return false;
		}
		String name = messageView.getRightTextString(1);
		if (name.length() == 0  ) {
			ToastFactory.showToast(AddBankActivity.this, "请填写开户名");
			return false;
		}
		String accout = messageView.getRightTextString(2);
		if (accout.length() == 0 ) {
			ToastFactory.showToast(AddBankActivity.this, "请填写银行卡号");
			return false;
		}
		boolean isNum = accout.matches("[0-9]+");
		if (!isNum) {
			ToastFactory.showToast(AddBankActivity.this, "银行卡号只能为数字");
			return false;
		}
		RequestConfig config = new RequestConfig(this,HttpTools.SET_ADD_BANK);
		config.hintString = "绑定银行卡";
		RequestParams params = new RequestParams();
		params.put("uId", ShoppingInfo.id);
		params.put("name", bankName);
		params.put("account", accout);
		params.put("accountName", name);
		params.put("isdefault", 0);
		HttpTools.httpPost(Contants.URl.URl_3013,"/administratorBank", config, params);
		return super.handClickEvent(v);
	}
	/**
	 * 初始化
	 */
	private void initView() {
		tvWarmPrompt = (TextView) findViewById(R.id.tv_warm_prompt);
		llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
		llSubmit.setOnClickListener(singleListener);
		String hintContent = "温馨提示："
		+"\n1、请正确填写银行卡的开户行、开户名和银行卡号"
		+"\n2、首次绑定的银行卡即为默认提现账户";
		tvWarmPrompt.setText(hintContent);
		messageView= (MessageArrowView) findViewById(R.id.messageView);
		messageView.setItemClickListener(this);
		messageView.setEditable(true);
		list.clear();
		
		ViewConfig config = new ViewConfig("开户银行","", true);
		config.rightText="请选择";
		list.add(config);
		config = new ViewConfig("开户名称","", false);
		config.rightEditable = true;
		config .enable = false;
		list.add(config);
		
		config = new ViewConfig("银行卡号","", false);
		config.rightEditable = true;
		config .enable = false;
		list.add(config);
		messageView.setData(list);
		
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if(code == 0){
			ToastFactory.showToast(AddBankActivity.this,"添加成功");
			finish();
		}
	}
	
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		if(mv == messageView){
			if(position == 0){
				showBankDialog();
			}
		}
	}
	/**
	 * 选择银行
	 */
		private void showBankDialog() {
			if (bankDialog == null) {
				bankDialog = new NetWorkListDialog(this);
				bankDialog.setNetworkListener(new NetworkRequestListener() {
					@Override
					public void onSuccess(WheelVerticalView wheelView, Message msg,
							String response) {
						JSONArray jsonArray = HttpTools.getContentJsonArray(response);
						if (jsonArray != null) {
							ResponseData data = HttpTools.getResponseContent(jsonArray);
							ArrayList<SlideItemObj> list = wheelView.getList();
							list.clear();
							if(data.length > 0){
								SlideItemObj item;
								for (int i = 0; i < data.length; i++) {
									item = new SlideItemObj(
											data.getString(i, "name"),
											data.getString(i, "id")
											);
									list.add(item);
							}
							}
							bankDialog.notifyDataInvalidated();
						}
					}

					@Override
					public void onRequest(MessageHandler msgHand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(AddBankActivity.this, 0);
						config.handler = msgHand.getHandler();
						RequestParams params = new RequestParams();
						HttpTools.httpGet(Contants.URl.URl_3013,"/bank/bankSelect", 
								config, params);
					}

					@Override
					public void onFail(Message msg, String message) {

					}
				});
				bankDialog.setOnCompleteClickListener(new OnCompleteListener() {

					@Override
					public void onComplete(SlideItemObj item1, SlideItemObj item2) {
						if (item1 != null) {
							ViewConfig vc = messageView.getItem(0);
							vc.rightText = item1.name;
							messageView.freshView(0);
						}
					}
				});
			}
			bankDialog.show("选择银行", false);
		}

	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_add_bank, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "银行卡绑定";
	}

}
