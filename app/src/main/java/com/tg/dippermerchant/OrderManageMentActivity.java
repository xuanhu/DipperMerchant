package com.tg.dippermerchant;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import com.tg.dippermerchant.adapter.OrderManageMentAdapter;
import com.tg.dippermerchant.adapter.OrderManageMentAdapter.OrderManageMentCallback;
import com.tg.dippermerchant.adapter.ServiceOrderAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.info.OrderEvent;
import com.tg.dippermerchant.info.OrderInfo;
import com.tg.dippermerchant.info.ServiceOrderInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.util.statusbar.StatusBarUtil;
import com.tg.dippermerchant.view.PopWindowView;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.DialogFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * 订单管理页面
 * @author Administrator
 *
 */
public class OrderManageMentActivity extends BaseActivity{
	private PullRefreshListView pullListView1;
	private PullRefreshListView pullListView2;
	private OrderManageMentAdapter adapter1;
	private ServiceOrderAdapter adapter2;
	private ArrayList<OrderInfo> list1 = new ArrayList<OrderInfo>();
	private ArrayList<ServiceOrderInfo> list2 = new ArrayList<ServiceOrderInfo>();
	private Intent intent;
	private ImageView ivFinish;
	private ImageView ivPullDown;
	private RelativeLayout rlselect;
	private boolean flag = true;
	private PopWindowView popWindowView;
	private int ordertype = 3;// 物业费:1, 预缴费:2, 实体订单:3, 虚拟订单:4,  服务订单:5
	private int state = -3  ;//  -3全部订单（不传参数为全部订单，自定义为-3，方便传参）-2管理员取消，-1会员取消，0未付款 1已付款 2已发货 3派送中 4已签收 5申请退货 6申请退款 7已退货 8已退款 9订单完成
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_manage_ment);
		RelativeLayout rlLoginBg = (RelativeLayout) findViewById(R.id.rl_login_bg);
		rlLoginBg.setPadding(0, StatusBarUtil.getStatusBarHeight(OrderManageMentActivity.this),0,0);
		ivFinish = (ImageView) findViewById(R.id.iv_finish);
		ivFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rlselect = (RelativeLayout) findViewById(R.id.rl_select);
		ivPullDown = (ImageView) findViewById(R.id.iv_pull_down);
		rlselect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/**
				 * 弹出框
				 */
				if(popWindowView == null){
					popWindowView = new PopWindowView(OrderManageMentActivity.this);
					popWindowView.setOnDismissListener(new poponDismissListener());
				}
				popWindowView.showPopupWindow(findViewById(R.id.rl_select));
				flag = !flag;
				getflag();
			}
		});
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		pullListView1 = (PullRefreshListView)findViewById(R.id.pull_listview1);
		pullListView2 = (PullRefreshListView)findViewById(R.id.pull_listview2);
		if(ordertype == 3){
			pullListView1.setVisibility(View.VISIBLE);
			pullListView2.setVisibility(View.GONE);
		}else if(ordertype == 4){
			pullListView1.setVisibility(View.GONE);
			pullListView2.setVisibility(View.VISIBLE);
		}
		/*pullListView1.SetOnItemLongClickListener(new PullRefreshListView.NetOnItemLongClickListener() {
			@Override
			public void setOnItemLongClickListener(AdapterView<?> arg0, View arg1, int position, long arg3) {
				DialogFactory.getInstance().showDialog(OrderManageMentActivity.this, new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, null, "确定要删除该订单吗", null, null);
			}
		});*/
		pullListView1.setNodataImage(R.drawable.noorder);
		pullListView1.setNodataText("当前暂无订单");
		pullListView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
									Message msg, String response) {
				ArrayList<OrderInfo> listData = new ArrayList<OrderInfo>();
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					OrderInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new OrderInfo();
						item.state = data.getInt(i, "state");
						item.orderId = data.getString(i, "orderId");
						item.batchNo = data.getString(i, "batchNo");
						item.receivename = data.getString(i, "receivename");
						item.paymenttime = data.getString(i, "paymenttime");
						item.orderstime = data.getString(i, "orderstime");
						item.receiptTime = data.getString(i, "receiptTime");
						item.shipmentsTime = data.getString(i, "shipmentsTime");
						item.freightprice = data.getFloat(i, "freightprice");
						item.price = data.getFloat(i, "price");
						item.originprice = data.getFloat(i, "originprice");
						item.sellingprice = data.getFloat(i, "sellingprice");
						item.payment = data.getFloat(i, "payment");
						JSONArray array = data.getJSONArray(i, "commoditys");
						if(array.length() > 0 ){
							ResponseData commoditysData = HttpTools.parseJsonArray(array);
							if(commoditysData.length > 0 ){
								OrderCommoditysInfo info;
								for (int j = 0; j < commoditysData.length; j++) {
									info = new OrderCommoditysInfo();
									info.imgUrl = commoditysData.getString(j, "imgUrl");
									info.cName = commoditysData.getString(j, "cName");
									info.quantity = commoditysData.getInt(j,"quantity");
									info.coriginalPrice = commoditysData.getFloat(j, "coriginalPrice");
									info.csellingprice = commoditysData.getFloat(j, "csellingprice");
									item.commoditys.add(info);
								}
							}
						}
						listData.add(item);
					}
					list1.clear();
					list1.addAll(listData);
				}
			}

			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(OrderManageMentActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("ordertype", ordertype);
				if(!isAll(state)){
					params.put("state", state);
				}
				params.put("isPlatform", 1);
				params.put("mshopId", ShoppingInfo.id);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/orders",config, params);

			}

			@Override
			public void onLoadingMore(PullRefreshListView t,
									  Handler hand, int pagerIndex) {
				RequestConfig config = new RequestConfig(OrderManageMentActivity.this, PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("ordertype", ordertype);
				if(!isAll(state)){
					params.put("state", state);
				}
				params.put("isPlatform", 1);
				params.put("mshopId",ShoppingInfo.id);
				params.put("page", pagerIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3026, "/orders",config, params);
			}
		});
		adapter1 = new OrderManageMentAdapter(this, list1);
		pullListView1.setAdapter(adapter1);
		adapter1.setAfterSalesCallback(new OrderManageMentCallback() {

			@Override
			public void doCallBack() {
				pullListView1.performLoading();
			}
		});
		/**
		 * 服务订单
		 */
		/*pullListView2.SetOnItemLongClickListener(new PullRefreshListView.NetOnItemLongClickListener() {
			@Override
			public void setOnItemLongClickListener(AdapterView<?> arg0, View arg1, int position, long arg3) {
				DialogFactory.getInstance().showDialog(OrderManageMentActivity.this, new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, null, "确定要删除该订单吗", null, null);
			}
		});*/
		pullListView2.setNodataImage(R.drawable.noorder);
		pullListView2.setNodataText("当前暂无订单");
		pullListView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
									Message msg, String response) {
			}

			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {

			}

			@Override
			public void onLoadingMore(PullRefreshListView t,
									  Handler hand, int pagerIndex) {
			}
		});
		adapter2 = new ServiceOrderAdapter(this, list2);
		pullListView2.setAdapter(adapter2);

		pullListView1.performLoading();
	}

	/**
	 * 根据条件查询订单
	 */
	private void getOrderList(){
		RequestConfig config = new RequestConfig(OrderManageMentActivity.this, HttpTools.GET_ORDER_LIST);
		RequestParams params = new RequestParams();
		params.put("ordertype", ordertype);
		if(!isAll(state)){
			params.put("state", state);
		}
		params.put("isPlatform", 1);
		params.put("mshopId",ShoppingInfo.id);
		params.put("page", 1);
		params.put("pagesize", 10);
		HttpTools.httpGet(Contants.URl.URl_3026, "/orders",config, params);
	}
	/**
	 * 根据条件查询订单（服务）
	 */
	private void getServiceOrderList(){
		Log.d("printLog","getServiceOrderList()");
		/*RequestConfig config = new RequestConfig(OrderManageMentActivity.this, HttpTools.GET_ORDER_LIST);
		RequestParams params = new RequestParams();
		params.put("pagesize", 10);
		HttpTools.httpGet(Contants.URl.URl_3026, "/orders",config, params);*/
		ArrayList<ServiceOrderInfo> listData = new ArrayList<ServiceOrderInfo>();
		ServiceOrderInfo info;
		for(int i = 0 ; i < 5 ; i++){
			info = new ServiceOrderInfo();
			info.name = "张三";
			listData.add(info);
		}
		list2.clear();
		list2.addAll(listData);
		adapter2.notifyDataSetChanged();
	}

	/**
	 * 更改图标状态
	 */
	private void getflag(){
	if(flag){
		ivPullDown.setImageResource(R.drawable.pull_down);
	}else{
		ivPullDown.setImageResource(R.drawable.pull_up);
	}
}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1 == HttpTools.GET_ORDER_LIST){
			ArrayList<OrderInfo> listData = new ArrayList<OrderInfo>();
			String response = HttpTools.getContentString(jsonString);
			if (response != null) {
				ResponseData data = HttpTools.getResponseData(response);
				OrderInfo item;
				for (int i = 0; i < data.length; i++) {
					item = new OrderInfo();
					item.state = data.getInt(i, "state");
					item.orderId = data.getString(i, "orderId");
					item.batchNo = data.getString(i, "batchNo");
					item.receivename = data.getString(i, "receivename");
					item.paymenttime = data.getString(i, "paymenttime");
					item.orderstime = data.getString(i, "orderstime");
					item.receiptTime = data.getString(i, "receiptTime");
					item.shipmentsTime = data.getString(i, "shipmentsTime");
					item.freightprice = data.getFloat(i, "freightprice");
					item.price = data.getFloat(i, "price");
					item.originprice = data.getFloat(i, "originprice");
					item.sellingprice = data.getFloat(i, "sellingprice");
					item.payment = data.getFloat(i, "payment");
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
					listData.add(item);
				}
				list1.clear();
				list1.addAll(listData);
				adapter1.notifyDataSetChanged();
			}
		}else{

		}
	}

	@Subscribe
	public void onEvent(OrderEvent event) {
		flag = event.isHides();
		getflag();
		if(event.getOrdertype() == 3){//实体订单
			ordertype = 3;
		}
		if(event.getOrdertype() == 5){//服务订单
			ordertype = 5 ;
		}
		if(event.getState() == 0){//全部订单
			state = -3 ;
		}
		if(event.getState() == 1){//待付款
			state = 0 ;
		}
		if(event.getState() == 2){//待发货
			state = 1;
		}
		if(event.getState() == 3){//待收货
			state = 2;
		}
		if(event.getState() == 4){//待评价
			state = 4;
		}
		if(event.getState() == 5){//已评价
			state = 9 ;
		}
		if(event.getState() == 6){//已退款
			state = 8 ;
		}
		if(event.getState() == 7){//已取消
			state = -2 ;
		}
		if(ordertype ==  3){
			pullListView1.setVisibility(View.VISIBLE);
			pullListView2.setVisibility(View.GONE);
			getOrderList();
		}else {
			pullListView1.setVisibility(View.GONE);
			pullListView2.setVisibility(View.VISIBLE);
			getServiceOrderList();
		}

	}
	@Override
	public void onResume() {
		super.onResume();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	private boolean isAll(int state){
		if(state == -2 ||
				state == -1||
				state == 0||
				state == 1||
				state == 2||
				state == 3||
				state == 4||
				state == 5||
				state == 6||
				state == 7||
				state == 8||
				state == 9){
			return false;
		}else{
			return  true;
		}
	}
	/**
	 * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
	 * @author cg
	 *
	 */
	class poponDismissListener implements PopupWindow.OnDismissListener{

		@Override
		public void onDismiss() {
			WindowManager.LayoutParams lp=OrderManageMentActivity.this.getWindow().getAttributes();
			lp.alpha=1.0f;
			OrderManageMentActivity.this.getWindow().setAttributes(lp);
		}
	}
}
