package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.dippermerchant.adapter.AfterSalesAdapter;
import com.tg.dippermerchant.adapter.ViewPagerAdapter;
import com.tg.dippermerchant.adapter.AfterSalesAdapter.AfterSalesCallback;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.AfterSalesInfo;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.view.MyViewPager;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 售后管理
 * @author Administrator
 *
 */
public class AfterSalesManageMentActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
	private MyViewPager viewPager;
	private RadioGroup radioGroup;
	private ViewPagerAdapter pagerAdapter;
	private AfterSalesAdapter adapter1;
	private AfterSalesAdapter adapter2;
	private AfterSalesAdapter adapter3;
	private ArrayList<View> pagerList = new ArrayList<View>();
	private PullRefreshListView listView1;
	private PullRefreshListView listView2;
	private PullRefreshListView listView3;
	private ArrayList<AfterSalesInfo> list1 = new ArrayList<AfterSalesInfo>();
	private ArrayList<AfterSalesInfo> list2 = new ArrayList<AfterSalesInfo>();
	private ArrayList<AfterSalesInfo> list3 = new ArrayList<AfterSalesInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		radioGroup = (RadioGroup)findViewById(R.id.radio_group);
		viewPager = (MyViewPager)findViewById(R.id.viewPager);
		//全部
		listView1 = new PullRefreshListView(this);
		listView1.setKeyName("all");
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		listView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
					@Override
					public void refreshData(PullRefreshListView t, boolean isLoadMore,
							Message msg, String response) {
						String jsonString = HttpTools.getContentString(response);
						if (jsonString != null) {
							ResponseData data = HttpTools.getResponseData(jsonString);
							AfterSalesInfo item;
							for (int i = 0; i < data.length; i++) {
								item = new AfterSalesInfo();
								item.id = data.getInt(i,"id");
								item.type = data.getInt(i,"type");
								item.integral = data.getInt(i,"integral");
								item.status = data.getInt(i,"status");
								item.orderid = data.getString(i,"orderid");
								item.imgurl = data.getString(i,"imgurl");
								item.addtime = data.getString(i,"addtime");
								item.refundtime = data.getString(i,"refundtime");
								item.agreetime = data.getString(i,"agreetime");
								item.returnIntegral = data.getString(i,"returnIntegral");
								item.question = data.getString(i,"question");
								item.reason = data.getString(i,"reason");
								item.userpayprice = data.getFloat(i, "userpayprice");
								item.onlinepayprice = data.getFloat(i, "onlinepayprice");
								JSONArray array = data.getJSONArray(i, "commoditys");
								if(array.length() > 0 ){
									ResponseData commoditysData = HttpTools.parseJsonArray(array);
									if(commoditysData.length > 0 ){
										OrderCommoditysInfo info;
										for (int j = 0; j < commoditysData.length; j++) {
											info = new OrderCommoditysInfo();
											info.imgUrl = commoditysData.getString(j, "imgUrl");
											info.cName = commoditysData.getString(j, "cName");
											info.cPrice = commoditysData.getFloat(j, "cPrice"); 
											info.coriginalPrice = commoditysData.getFloat(j, "coriginalPrice"); 
											info.csellingprice = commoditysData.getFloat(j, "csellingprice"); 
											item.commoditys.add(info);
										}
									}
								}
								list1.add(item);
							}
							}
						}

					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("mshopid",ShoppingInfo.id);
						params.put("page", 1);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
						
					}

					@Override
					public void onLoadingMore(PullRefreshListView t,
							Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("mshopid",ShoppingInfo.id);
						params.put("page", pagerIndex);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
					}
				});
		adapter1 = new AfterSalesAdapter(this, list1);
		listView1.setAdapter(adapter1);
		adapter1.setAfterSalesCallback(new AfterSalesCallback() {
			
			@Override
			public void doCallBack() {
				listView1.performLoading();
			}
		});
		pagerList.add(listView1);
		//处理中
		listView2 = new PullRefreshListView(this);
		listView2.setKeyName("inhand");
		listView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		listView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					AfterSalesInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new AfterSalesInfo();
							item.id = data.getInt(i,"id");
							item.type = data.getInt(i,"type");
							item.integral = data.getInt(i,"integral");
							item.status = data.getInt(i,"status");
							item.orderid = data.getString(i,"orderid");
							item.imgurl = data.getString(i,"imgurl");
							item.addtime = data.getString(i,"addtime");
							item.refundtime = data.getString(i,"refundtime");
							item.agreetime = data.getString(i,"agreetime");
							item.returnIntegral = data.getString(i,"returnIntegral");
							item.question = data.getString(i,"question");
							item.reason = data.getString(i,"reason");
							item.userpayprice = data.getFloat(i, "userpayprice");
							item.onlinepayprice = data.getFloat(i, "onlinepayprice");
							JSONArray array = data.getJSONArray(i, "commoditys");
							if(array.length() > 0 ){
								ResponseData commoditysData = HttpTools.parseJsonArray(array);
								if(commoditysData.length > 0 ){
									OrderCommoditysInfo info;
									for (int j = 0; j < commoditysData.length; j++) {
										info = new OrderCommoditysInfo();
										info.imgUrl = commoditysData.getString(j, "imgUrl");
										info.cName = commoditysData.getString(j, "cName");
										info.cPrice = commoditysData.getFloat(j, "cPrice"); 
										info.coriginalPrice = commoditysData.getFloat(j, "coriginalPrice"); 
										info.csellingprice = commoditysData.getFloat(j, "csellingprice"); 
										item.commoditys.add(info);
									}
								}
							}
							list2.add(item);
					}
					}
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("mshopid",ShoppingInfo.id);
				params.put("selType", 1);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
				
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t,
					Handler hand, int pagerIndex) {
				RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("mshopid",ShoppingInfo.id);
				params.put("selType", 1);
				params.put("page", pagerIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
				
			}
		});
		
		adapter2 = new AfterSalesAdapter(this, list2);
		listView2.setAdapter(adapter2);
		adapter2.setAfterSalesCallback(new AfterSalesCallback() {
			
			@Override
			public void doCallBack() {
				listView2.performLoading();
			}
		});
		pagerList.add(listView2);
		//退货/退款
		listView3 = new PullRefreshListView(this);
		listView3.setKeyName("refund");
		listView3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		listView3.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					AfterSalesInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new AfterSalesInfo();
							item.id = data.getInt(i,"id");
							item.type = data.getInt(i,"type");
							item.integral = data.getInt(i,"integral");
							item.status = data.getInt(i,"status");
							item.orderid = data.getString(i,"orderid");
							item.imgurl = data.getString(i,"imgurl");
							item.addtime = data.getString(i,"addtime");
							item.refundtime = data.getString(i,"refundtime");
							item.agreetime = data.getString(i,"agreetime");
							item.returnIntegral = data.getString(i,"returnIntegral");
							item.question = data.getString(i,"question");
							item.reason = data.getString(i,"reason");
							item.userpayprice = data.getFloat(i, "userpayprice");
							item.onlinepayprice = data.getFloat(i, "onlinepayprice");
							JSONArray array = data.getJSONArray(i, "commoditys");
							if(array.length() > 0 ){
								ResponseData commoditysData = HttpTools.parseJsonArray(array);
								if(commoditysData.length > 0 ){
									OrderCommoditysInfo info;
									for (int j = 0; j < commoditysData.length; j++) {
										info = new OrderCommoditysInfo();
										info.imgUrl = commoditysData.getString(j, "imgUrl");
										info.cName = commoditysData.getString(j, "cName");
										info.cPrice = commoditysData.getFloat(j, "cPrice"); 
										info.coriginalPrice = commoditysData.getFloat(j, "coriginalPrice"); 
										info.csellingprice = commoditysData.getFloat(j, "csellingprice"); 
										item.commoditys.add(info);
									}
								}
							}
							list3.add(item);
					}
					}
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("mshopid",ShoppingInfo.id);
				params.put("selType", 2);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
				
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t,
					Handler hand, int pagerIndex) {
				RequestConfig config = new RequestConfig(AfterSalesManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("mshopid",ShoppingInfo.id);
				params.put("selType", 2);
				params.put("page", pagerIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/refund",config, params);
				
			}
		});
		
		adapter3 = new AfterSalesAdapter(this, list3);
		listView3.setAdapter(adapter3);
		adapter3.setAfterSalesCallback(new AfterSalesCallback() {
			
			@Override
			public void doCallBack() {
				listView3.performLoading();
			}
		});
		pagerList.add(listView3);
		
		pagerAdapter = new ViewPagerAdapter(pagerList,this);
		viewPager.setAdapter(pagerAdapter);
		listView1.performLoading();
		}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == 0){
			radioGroup.check(R.id.rb_all);
		}else if(position == 1){
			radioGroup.check(R.id.rb_in_hand);
		}else if(position == 2){
			radioGroup.check(R.id.rb_refund);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_all){
			if(viewPager.getCurrentItem() != 0){
				viewPager.setCurrentItem(0);
				listView1.performLoading();
			}
		}else if(checkedId == R.id.rb_in_hand){
			if(viewPager.getCurrentItem() != 1){
				viewPager.setCurrentItem(1);
				listView2.performLoading();
			}
		}else if(checkedId == R.id.rb_refund){
			if(viewPager.getCurrentItem() != 2){
				viewPager.setCurrentItem(2);
				listView3.performLoading();
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		listView1.performLoading();
		listView2.performLoading();
		listView3.performLoading();
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_after_sales_manage_ment,null);
	}

	@Override
	public String getHeadTitle() {
		return "售后管理";
	}

}
