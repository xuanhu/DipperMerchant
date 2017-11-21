package com.tg.dippermerchant;

import java.util.ArrayList;
import java.util.Calendar;

import com.tg.dippermerchant.adapter.ConsumerCodeManageMentAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ConsumerCodeManageMentInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.DateSelectorDialog;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

/**
 * 消费码管理
 * 
 * @author Administrator
 * 
 */
public class ConsumerCodeManageMentActivity extends BaseActivity {
	private RelativeLayout rlStartTime, rlEndTime,rlClickQuery;
	private TextView tvStartTime, tvEndTime;
	private DateSelectorDialog StartDateDialog,EndDateDialog;
	private PullRefreshListView pullListView;
	private ConsumerCodeManageMentAdapter adapter;
	private ArrayList<ConsumerCodeManageMentInfo> list = new ArrayList<ConsumerCodeManageMentInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.rl_startTime:// 开始时间
			if (StartDateDialog == null) {
				StartDateDialog = new DateSelectorDialog(this);
			}
			long startTime = Tools.simpledateString2Mini(tvStartTime.getText().toString());
			Calendar cStrart = Calendar.getInstance();
			cStrart.setTimeInMillis(startTime);
			StartDateDialog.show("选择活动日期", cStrart, null, new OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// TODO Auto-generated method stub
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, year);
					c.set(Calendar.MONTH, monthOfYear);
					c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvStartTime.setText(Tools.getSimpleDateToString(c
							.getTimeInMillis()));
				}
			});
			break;

		case R.id.rl_endTime:// 结束时间
			if (EndDateDialog == null) {
				EndDateDialog = new DateSelectorDialog(this);
			}
			long endTime = Tools.simpledateString2Mini(tvEndTime.getText().toString());
			Calendar cEnd = Calendar.getInstance();
			cEnd.setTimeInMillis(endTime);
			EndDateDialog.show("选择活动日期", cEnd, null, new OnDateChangedListener() {

				@Override
				public void onDateChanged(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// TODO Auto-generated method stub
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, year);
					c.set(Calendar.MONTH, monthOfYear);
					c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvEndTime.setText(Tools.getSimpleDateToString(c
							.getTimeInMillis()));
				}
			});
			break;
		case R.id.rl_click_query:// 点击查询
			long start = Tools.simpledateString2Mini(tvStartTime.getText().toString());
			long end = Tools.simpledateString2Mini(tvEndTime.getText().toString());
			if(start > end){
				ToastFactory.showToast(this,"开始时间不能大于结束时间");
				return false;
			}
			query();
			break;
		}
		return super.handClickEvent(v);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		rlStartTime = (RelativeLayout) findViewById(R.id.rl_startTime);
		rlEndTime = (RelativeLayout) findViewById(R.id.rl_endTime);
		rlClickQuery = (RelativeLayout) findViewById(R.id.rl_click_query);
		tvStartTime = (TextView) findViewById(R.id.tv_startTime);
		tvEndTime = (TextView) findViewById(R.id.tv_endTime);
		rlStartTime.setOnClickListener(singleListener);
		rlEndTime.setOnClickListener(singleListener);
		rlClickQuery.setOnClickListener(singleListener);
		Calendar c = Calendar.getInstance();
		String Time = Tools.getSimpleDateToString(c.getTimeInMillis());
		tvStartTime.setText(Time);
		tvEndTime.setText(Time);
		pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
		pullListView.setEnableMoreButton(false);
		pullListView.setDividerHeight(0);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				// TODO Auto-generated method stub
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					ConsumerCodeManageMentInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new ConsumerCodeManageMentInfo();
						item.cName = data.getString(i, "cName");
						item.useTime = data.getString(i, "useTime");
						item.useCede = data.getString(i, "useCede");
						item.cPrice = data.getFloat(i, "cPrice");
						item.state = data.getInt(i, "state");
						list.add(item);
					}
				}
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				RequestConfig config = new RequestConfig(ConsumerCodeManageMentActivity.this,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				//params.put("uid", ShoppingInfo.id);
				params.put("isPlatform", 1);
				params.put("page", pageIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/orders/getcommoditycodeByInfo",config, params);
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(ConsumerCodeManageMentActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				//params.put("uid", ShoppingInfo.id);
				params.put("isPlatform", 1);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/orders/getcommoditycodeByInfo",config, params);
			}
		});
		adapter = new ConsumerCodeManageMentAdapter(this,list);
		pullListView.setAdapter(adapter);
		pullListView.performLoading();
	}
	/*
	 * 点击查询
	 */
	private void query(){
		adapter.getList().clear();
		RequestConfig config = new RequestConfig(ConsumerCodeManageMentActivity.this,HttpTools.GET_QUERY_CODE);
		RequestParams params = new RequestParams();
		//params.put("uid", ShoppingInfo.id);
		params.put("btime", tvStartTime.getText().toString());
		params.put("etime",tvEndTime.getText().toString());
		params.put("isPlatform", 1);
		params.put("page", 1);
		params.put("pagesize", PullRefreshListView.PAGER_SIZE);
		HttpTools.httpGet(Contants.URl.URl_3026, "/orders/getcommoditycodeByInfo",config, params);
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		String response = HttpTools.getContentString(jsonString);
		int code = HttpTools.getCode(jsonString);
		if(code != 0){
			pullListView.setVisibility(View.GONE);
			ToastFactory.showToast(this,"暂无数据");
		}else{
			pullListView.setVisibility(View.VISIBLE);
		}
		if (response != null) {
			ResponseData data = HttpTools.getResponseData(response);
			ConsumerCodeManageMentInfo item;
			for (int i = 0; i < data.length; i++) {
				item = new ConsumerCodeManageMentInfo();
				item.cName = data.getString(i, "cName");
				item.useTime = data.getString(i, "useTime");
				item.useCede = data.getString(i, "useCede");
				item.cPrice = data.getFloat(i, "cPrice");
				item.state = data.getInt(i, "state");
				list.add(item);
			}
			adapter.notifyDataSetChanged();
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_consumer_code_manage_ment, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "验证记录";
	}

}
