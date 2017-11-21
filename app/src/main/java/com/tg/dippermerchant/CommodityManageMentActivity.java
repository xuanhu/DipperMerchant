package com.tg.dippermerchant;

import java.util.ArrayList;

import com.tg.dippermerchant.adapter.CommodityMentAdapter;
import com.tg.dippermerchant.adapter.ViewPagerAdapter;
import com.tg.dippermerchant.adapter.CommodityMentAdapter.CommodityCallback;
import com.tg.dippermerchant.application.ManagementApplication;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.database.SharedPreferencesTools;
import com.tg.dippermerchant.info.CommodityInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.view.MyViewPager;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.DialogFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 商品管理页面
 * @author Administrator
 *
 */
public class CommodityManageMentActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
	private MyViewPager viewPager;
	private RadioGroup radioGroup;
	private ViewPagerAdapter pagerAdapter;
	private CommodityMentAdapter adapter1;
	private CommodityMentAdapter adapter2;
	private ArrayList<View> pagerList = new ArrayList<View>();
	private PullRefreshListView listView1;
	private PullRefreshListView listView2;
	private ArrayList<CommodityInfo> list1 = new ArrayList<CommodityInfo>();
	private ArrayList<CommodityInfo> list2 = new ArrayList<CommodityInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	protected boolean handClickEvent(View v) {
		startActivity(new Intent(CommodityManageMentActivity.this,AddCommoditysActivity.class));
		return super.handClickEvent(v);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		radioGroup = (RadioGroup)findViewById(R.id.radio_group);
		viewPager = (MyViewPager)findViewById(R.id.viewPager);
		//已上架
		listView1 = new PullRefreshListView(this);
		listView1.setNodataImage(R.drawable.nomerchandise);
		listView1.setNodataText("暂无商品");
		listView1.setKeyName("all");
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommodityInfo  info = list1.get(position);
				Intent intent = new Intent(CommodityManageMentActivity.this, CommodityDetailsActivity.class);
				intent.putExtra(CommodityDetailsActivity.COMMODITY, info);
				startActivity(intent);
			}
		});
		/*listView1.SetOnItemLongClickListener(new PullRefreshListView.NetOnItemLongClickListener() {
			@Override
			public void setOnItemLongClickListener(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				DialogFactory.getInstance().showDialog(CommodityManageMentActivity.this, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommodityInfo  info = list1.get(position);
						RequestConfig config = new RequestConfig(CommodityManageMentActivity.this,HttpTools.DELETE_COMMODITY);
						RequestParams params = new RequestParams();
						params.put("id",info.id);
						HttpTools.httpDelete(Contants.URl.URl_3026,"/commodity",config,params);
					}
				}, null, "确定要删除该商品吗", null, null);
			}
		});*/
		listView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
					@Override
					public void refreshData(PullRefreshListView t, boolean isLoadMore,
							Message msg, String response) {
						String jsonString = HttpTools.getContentString(response);
						if (jsonString != null) {
							ResponseData data = HttpTools.getResponseData(jsonString);
							CommodityInfo item;
							for (int i = 0; i < data.length; i++) {
								item = new CommodityInfo();
								item.name = data.getString(i,"name");
								item.addTime = data.getString(i,"addTime");
								item.eTime = data.getString(i,"eTime");
								item.bTime = data.getString(i,"bTime");
								item.imgUrl = data.getString(i,"imgUrl");
								item.imgName = data.getString(i,"imgName");
								item.describe = data.getString(i,"describe");
								item.price = data.getFloat(i,"price");
								item.originalPrice = data.getFloat(i,"originalPrice");
								item.sellingprice = data.getFloat(i,"sellingprice");
								item.discount = data.getFloat(i,"discount");
								item.amount = data.getInt(i,"amount");
								item.state = data.getInt(i,"state");
								item.id = data.getInt(i,"id");
								item.type = data.getInt(i,"type");
								list1.add(item);
							}
							}
						}

					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						RequestConfig config = new RequestConfig(CommodityManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("isPlatform", 1);
						params.put("type", 0);//0实体商品1虚拟商品2服务商品
						params.put("state", 1);
						params.put("mshopId",ShoppingInfo.id);
						params.put("page", 1);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3026, "/commodity",config, params);
						
					}

					@Override
					public void onLoadingMore(PullRefreshListView t,
							Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(CommodityManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("isPlatform", 1);
						params.put("type", 0);//0实体商品1虚拟商品2服务商品
						params.put("state", 1);
						params.put("mshopId",ShoppingInfo.id);
						params.put("page", pagerIndex);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URl_3026, "/commodity",config, params);
					}
				});
		adapter1 = new CommodityMentAdapter(this, list1);
		listView1.setAdapter(adapter1);
		adapter1.setCommodityCallback(new CommodityCallback() {
			
			@Override
			public void doCallBack(int i) {
				listView1.performLoading();
			}
		});
		pagerList.add(listView1);
		//已下架
		listView2 = new PullRefreshListView(this);
		listView2.setNodataImage(R.drawable.nomerchandise);
		listView2.setNodataText("暂无商品");
		listView2.setKeyName("nopayment");
		listView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommodityInfo  info = list2.get(position);
				Intent intent = new Intent(CommodityManageMentActivity.this, CommodityDetailsActivity.class);
				intent.putExtra(CommodityDetailsActivity.COMMODITY, info);
				startActivity(intent);
			}
		});
		/*listView2.SetOnItemLongClickListener(new PullRefreshListView.NetOnItemLongClickListener() {
			@Override
			public void setOnItemLongClickListener(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				DialogFactory.getInstance().showDialog(CommodityManageMentActivity.this, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommodityInfo  info = list2.get(position);
						RequestConfig config = new RequestConfig(CommodityManageMentActivity.this,HttpTools.DELETE_COMMODITY);
						RequestParams params = new RequestParams();
						params.put("id",info.id);
						HttpTools.httpDelete(Contants.URl.URl_3026,"/commodity",config,params);
					}
				}, null, "确定要删除该商品吗", null, null);
			}
		});*/
		listView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					CommodityInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new CommodityInfo();
						item.name = data.getString(i,"name");
						item.addTime = data.getString(i,"addTime");
						item.eTime = data.getString(i,"eTime");
						item.bTime = data.getString(i,"bTime");
						item.imgUrl = data.getString(i,"imgUrl");
						item.imgName = data.getString(i,"imgName");
						item.describe = data.getString(i,"describe");
						item.token = data.getString(i,"token");
						item.source = data.getString(i,"source");
						item.price = data.getFloat(i,"price");
						item.originalPrice = data.getFloat(i,"originalPrice");
						item.sellingprice = data.getFloat(i,"sellingprice");
						item.discount = data.getFloat(i,"discount");
						item.amount = data.getInt(i,"amount");
						item.state = data.getInt(i,"state");
						item.id = data.getInt(i,"id");
						list2.add(item);
					}
					}
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(CommodityManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("isPlatform", 1);
				params.put("type", 0);//0实体商品1虚拟商品2服务商品
				params.put("state", 0);
				params.put("mshopId",ShoppingInfo.id);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/commodity",config, params);
				
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t,
					Handler hand, int pagerIndex) {
				RequestConfig config = new RequestConfig(CommodityManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("isPlatform", 1);
				params.put("type", 0);//0实体商品1虚拟商品2服务商品
				params.put("state", 0);
				params.put("mshopId",ShoppingInfo.id);
				params.put("page", pagerIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/commodity",config, params);
				
			}
		});
		
		adapter2 = new CommodityMentAdapter(this, list2);
		listView2.setAdapter(adapter2);
		adapter2.setCommodityCallback(new CommodityCallback() {
			
			@Override
			public void doCallBack(int i) {
				listView2.performLoading();
			}
		});
		pagerList.add(listView2);
		pagerAdapter = new ViewPagerAdapter(pagerList,this);
		viewPager.setAdapter(pagerAdapter);
		listView1.performLoading();
		}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		Log.d("printLog","jsonString="+jsonString);
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
			radioGroup.check(R.id.rb_entity);
		}else if(position == 1){
			radioGroup.check(R.id.rb_dummy);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_entity){
			if(viewPager.getCurrentItem() != 0){
				viewPager.setCurrentItem(0);
				listView1.performLoading();
			}
		}else if(checkedId == R.id.rb_dummy){
			if(viewPager.getCurrentItem() != 1){
				viewPager.setCurrentItem(1);
				listView2.performLoading();
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		listView1.performLoading();
		listView2.performLoading();
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_commodity_manage_ment,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("添加商品");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "商品管理";
	}

}
