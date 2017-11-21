package com.tg.dippermerchant;

import java.util.ArrayList;

import com.tg.dippermerchant.adapter.ViewPagerAdapter;
import com.tg.dippermerchant.adapter.WithdrawalRecordAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.WithdrawalRecordInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.view.MyViewPager;
import com.tg.dippermerchant.view.PullRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 提现记录
 * @author Administrator
 *
 */
public class WithdrawalRecordActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
	private MyViewPager viewPager;
	private RadioGroup radioGroup;
	private ViewPagerAdapter pagerAdapter;
	private WithdrawalRecordAdapter adapter1;
	private WithdrawalRecordAdapter adapter2;
	private ArrayList<View> pagerList = new ArrayList<View>();
	private PullRefreshListView listView1;
	private PullRefreshListView listView2;
	private ArrayList<WithdrawalRecordInfo> list1 = new ArrayList<WithdrawalRecordInfo>();
	private ArrayList<WithdrawalRecordInfo> list2 = new ArrayList<WithdrawalRecordInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * 初始化
	 */
	private void initView() {
		radioGroup = (RadioGroup)findViewById(R.id.radio_group);
		viewPager = (MyViewPager)findViewById(R.id.viewPager);
		//已提现
		listView1 = new PullRefreshListView(this);
		listView1.setKeyName("withdrawalFinal");
		listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WithdrawalRecordInfo info = list1.get(position);
				Intent intent = new Intent(WithdrawalRecordActivity.this,WithdrawalRecordDetailsActivity.class);
				intent.putExtra(WithdrawalRecordDetailsActivity.ID,info.id);
				startActivity(intent);
			}
		});
		listView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
				@Override
				public void refreshData(PullRefreshListView t, boolean isLoadMore,
									Message msg, String response) {
								String jsonString = HttpTools.getContentString(response);
								if (jsonString != null) {
									ResponseData data = HttpTools.getResponseData(jsonString);
									WithdrawalRecordInfo item;
									for (int i = 0; i < data.length; i++) {
										item = new WithdrawalRecordInfo();
										item.id = data.getInt(i,"id");
										item.money = data.getFloat(i, "money");
										item.addTime = data.getString(i, "addTime");
										item.bankname = data.getString(i, "bankname");
										item.enote = data.getString(i, "enote");
										item.note = data.getString(i, "note");
										item.payaccount = data.getString(i, "payaccount");
										item.eName = data.getString(i, "eName");
										list1.add(item);
									}
									}
								}

							@Override
							public void onLoading(PullRefreshListView t, Handler hand) {
								RequestConfig config = new RequestConfig(WithdrawalRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
								config.handler = hand;
								RequestParams params = new RequestParams();
								params.put("mshopid",ShoppingInfo.id);
								params.put("state", 1);
								params.put("page", 1);
								params.put("pagesize", PullRefreshListView.PAGER_SIZE);
								HttpTools.httpGet(Contants.URl.URl_3013, "/merchantWithdrawals",config, params);
								
							}

							@Override
							public void onLoadingMore(PullRefreshListView t,
									Handler hand, int pagerIndex) {
								RequestConfig config = new RequestConfig(WithdrawalRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
								config.handler = hand;
								RequestParams params = new RequestParams();
								params.put("mshopid",ShoppingInfo.id);
								params.put("state", 1);
								params.put("page", pagerIndex);
								params.put("pagesize", PullRefreshListView.PAGER_SIZE);
								HttpTools.httpGet(Contants.URl.URl_3013, "/merchantWithdrawals",config, params);
							}
						});
				adapter1 = new WithdrawalRecordAdapter(this, list1);
				listView1.setAdapter(adapter1);
				pagerList.add(listView1);
				//提现中
				listView2 = new PullRefreshListView(this);
				listView2.setKeyName("withdrawalInHand");
			listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WithdrawalRecordInfo info = list2.get(position);
				Intent intent = new Intent(WithdrawalRecordActivity.this,WithdrawalRecordDetailsActivity.class);
				intent.putExtra(WithdrawalRecordDetailsActivity.ID,info.id);
				startActivity(intent);
				}
			});
				listView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
					@Override
					public void refreshData(PullRefreshListView t, boolean isLoadMore,
							Message msg, String response) {
						String jsonString = HttpTools.getContentString(response);
						if (jsonString != null) {
							ResponseData data = HttpTools.getResponseData(jsonString);
							WithdrawalRecordInfo item;
							for (int i = 0; i < data.length; i++) {
								item = new WithdrawalRecordInfo();
								item.id = data.getInt(i,"id");
								item.money = data.getFloat(i, "money");
								item.addTime = data.getString(i, "addTime");
								item.bankname = data.getString(i, "bankname");
								item.enote = data.getString(i, "enote");
								item.note = data.getString(i, "note");
								item.payaccount = data.getString(i, "payaccount");
								item.eName = data.getString(i, "eName");
								list2.add(item);
							}
							}
					}
					
					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						RequestConfig config = new RequestConfig(WithdrawalRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("mshopid",ShoppingInfo.id);
						params.put("state", 0);
						params.put("page", 1);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3013, "/merchantWithdrawals",config, params);
						
					}
					
					@Override
					public void onLoadingMore(PullRefreshListView t,
							Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(WithdrawalRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("mshopid",ShoppingInfo.id);
						params.put("state", 0);
						params.put("page", pagerIndex);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3013, "/merchantWithdrawals",config, params);
						
					}
				});
				
				adapter2 = new WithdrawalRecordAdapter(this, list2);
				listView2.setAdapter(adapter2);
				pagerList.add(listView2);
		pagerAdapter = new ViewPagerAdapter(pagerList,this);
		viewPager.setAdapter(pagerAdapter);
		listView1.performLoading();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == 0){
			radioGroup.check(R.id.rb_withdrawal_final);
		}else if(position == 1){
			radioGroup.check(R.id.rb_withdrawal_in_hand);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_withdrawal_final){
			if(viewPager.getCurrentItem() != 0){
				viewPager.setCurrentItem(0);
				listView1.performLoading();
			}
		}else if(checkedId == R.id.rb_withdrawal_in_hand){
			if(viewPager.getCurrentItem() != 1){
				viewPager.setCurrentItem(1);
				listView2.performLoading();
			}
		}
	}
	
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_withdrawal_record, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "提现记录";
	}

}
