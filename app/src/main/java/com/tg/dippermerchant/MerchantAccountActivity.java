package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.dippermerchant.application.ManagementApplication;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.database.SharedPreferencesTools;
import com.tg.dippermerchant.info.BankCardInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.ChoiceView;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.MyBrowserActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 商家账户
 * 
 * @author Administrator
 * 
 */
public class MerchantAccountActivity extends BaseActivity {
	private TextView tvBalance,tvWithdraw;
	private RelativeLayout rlNoBinding,rlAddBank,rlBalance,rlWithdraw;
	private ListView listview;
	private ListAdapter adapter;
	private float money;
	private String withdrawalsmoney;
	private int DefaultMark = -1;
	private ArrayList<BankCardInfo> list = new ArrayList<BankCardInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RequestParams params = new RequestParams();
		params.put("id", ShoppingInfo.id);
		HttpTools.httpGet(Contants.URl.URl_3013,"/merchant/getmerchantmoneyByid/"+ShoppingInfo.id, 
				new RequestConfig(this, HttpTools.GET_SHOPPING_INFO), params);
		
		RequestParams params1 = new RequestParams();
		params1.put("uId", ShoppingInfo.id);
		params1.put("page", 1);
		params1.put("pagesize",10);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administratorBank", 
				new RequestConfig(this, HttpTools.GET_WITHDRAW_INFO), params1);
		list.clear();
		getBankList();
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tvBalance = (TextView) findViewById(R.id.tv_balance);
		tvWithdraw = (TextView) findViewById(R.id.tv_withdraw);
		rlBalance = (RelativeLayout) findViewById(R.id.rl_balance);
		rlWithdraw = (RelativeLayout) findViewById(R.id.rl_withdraw);
		rlBalance.setOnClickListener(new OnClickListener() {//余额
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MerchantAccountActivity.this,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+Tools.getEncryptURL(DES.KEY_URl,"&type=3"));
				startActivity(intent);
			}
		});
		rlWithdraw.setOnClickListener(new OnClickListener() {//提现
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MerchantAccountActivity.this, WithdrawalRecordActivity.class));
			}
		});
		listview=(ListView)findViewById(R.id.lv_bank);
		adapter = new ArrayAdapter<BankCardInfo>(this,R.layout.bank_listview, list){
        	@Override
        	public View getView(final int position, View convertView, ViewGroup parent) {
        		final ChoiceView view;  
                if(convertView == null) {  
                    view = new ChoiceView(MerchantAccountActivity.this);  
                } else {  
                    view = (ChoiceView)convertView;  
                }
                TextView tvBank = (TextView) view.findViewById(R.id.tv_bank);
        		TextView tvAccount = (TextView) view.findViewById(R.id.tv_account);
        		ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        		tvBank.setText(list.get(position).name);
        		tvAccount.setText(list.get(position).account);
				ivDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DialogFactory.getInstance().showDialog(MerchantAccountActivity.this, new OnClickListener() {
							@Override
							public void onClick(View v) {
								RequestConfig config = new RequestConfig(MerchantAccountActivity.this,HttpTools.SET_DELETE_INFO,"删除银行卡");
			        			RequestParams params = new RequestParams();
			        			params.put("id",list.get(position).id);
			        			HttpTools.httpDelete(Contants.URl.URl_3013,"/administratorBank/"+list.get(position).id,config, params);
							}
						}, null, "确定要删除此银行卡吗", null, null);
					}
				});
                return view; 
        	}
        };
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list.get(position).isdefault == 1){
					ToastFactory.showToast(MerchantAccountActivity.this, "亲，已经是默认银行卡了！");
        		}else{
        			RequestConfig config = new RequestConfig(MerchantAccountActivity.this,HttpTools.SET_DEFAULT_INFO,"设置默认银行卡");
        			RequestParams params = new RequestParams();
        			params.put("id",list.get(position).id);
        			params.put("name",list.get(position).name);
        			params.put("account",list.get(position).account);
        			params.put("accountName",list.get(position).accountName);
        			params.put("uId",ShoppingInfo.id);
        			params.put("isdefault",1);
        			HttpTools.httpPut(Contants.URl.URl_3013,"/administratorBank" ,config, params);
        			}
        		}
		});
		rlNoBinding=(RelativeLayout) findViewById(R.id.rl_nobinding);
		rlAddBank=(RelativeLayout) findViewById(R.id.rl_add_bank);
		rlAddBank.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MerchantAccountActivity.this, AddBankActivity.class));
			}
		});
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if(msg.arg1 == HttpTools.GET_SHOPPING_INFO){//获取余额
			JSONArray response = HttpTools.getContentJsonArray(jsonString);
			ResponseData data = HttpTools.getResponseContent(response);
			if(code == 0){
				money = data.getFloat("money");
				tvBalance.setText(""+money);
			}
		}else if(msg.arg1 == HttpTools.GET_WITHDRAW_INFO){//累计提现
			if(code == 0){
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				if(jsonObject != null ){
					try {
						withdrawalsmoney = jsonObject.getString("withdrawalsmoney");
						tvWithdraw.setText("累计提现:"+Float.valueOf(withdrawalsmoney));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else if(msg.arg1 == HttpTools.SET_DEFAULT_INFO){//设置默认
			if(code == 0){
				ToastFactory.showToast(MerchantAccountActivity.this, "设置默认成功");
			}else{
				ToastFactory.showToast(MerchantAccountActivity.this, "设置默认失败");
			}
			list.clear();
			getBankList();
		}else if(msg.arg1 == HttpTools.SET_DELETE_INFO){//删除银行卡
			if(code == 0){
				ToastFactory.showToast(MerchantAccountActivity.this, "删除成功");
			}else{
				ToastFactory.showToast(MerchantAccountActivity.this, "删除失败");
			}
			DefaultMark = -1;
			list.clear();
			getBankList();
		}else if(msg.arg1 == HttpTools.GET_BANK_LIST){//获取银行卡信息
			if(code == 0){
				rlNoBinding.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
			}else{
				rlNoBinding.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
			}
			String response = HttpTools.getContentString(jsonString);
			if (jsonString != null) {
				ResponseData data = HttpTools.getResponseData(response);
				if(data.length > 0){
					BankCardInfo info ;
					for (int i = 0; i < data.length; i++) {
						info = new BankCardInfo();
						info.id = data.getInt(i, "id");
						info.isdefault = data.getInt(i, "isdefault");
						info.name = data.getString(i, "name");
						info.account = data.getString(i, "account");
						info.accountName = data.getString(i, "accountName");
						if(info.isdefault == 1){
		        			DefaultMark = i;
		        		}
						list.add(info);
					}
				}
			}
			listview.setAdapter(adapter);
			listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 如果不使用这个设置，选项中的radiobutton无法响应选中事件
			if(DefaultMark != -1){
				listview.setItemChecked(DefaultMark, true);
			}
		}
	}
	/**
	 * 获取银行卡信息
	 */
	private void getBankList(){
		RequestParams params = new RequestParams();
		params.put("uId", ShoppingInfo.id);
		params.put("page", 1);
		params.put("pagesize", 100);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administratorBank", 
				new RequestConfig(this, HttpTools.GET_BANK_LIST), params);
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		list.clear();
		getBankList();
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_merchant_account,null);
	}

	@Override
	public String getHeadTitle() {
		return "商家账户";
	}

}
