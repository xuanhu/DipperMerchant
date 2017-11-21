package com.tg.dippermerchant;

import org.apache.http.protocol.HTTP;

import com.tg.dippermerchant.adapter.OrderCommoditysAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.OrderInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.view.MyListView;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.R.drawable;
import com.tg.dippermerchant.R.id;
import com.tg.dippermerchant.R.layout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 订单详情
 * 
 * @author Administrator
 * 
 */
public class OrderDetailsActivity extends BaseActivity {
	public static final  String Info = "info";
	private RelativeLayout rlCancellation, rlNegate, rlSure,rlStateBackground;
	private LinearLayout llOperation;
	private TextView tvNegate, tvSure, tvOrderState, tvOrderPlan, tvClientName,
			tvClientPhone, tvClientAddress, tvRemark,tvExpressage,tvTotalPrice,
			tvRealityPrice,tvTime,tvCancel;
	private MyListView lvCommoditys;
	private OrderInfo  info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent != null){
			info = (OrderInfo) intent.getSerializableExtra(Info);
		}
		initView();
	}
	
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.rl_cancellation://取消订单
			if(info.state == 0){
				cancellation();
			}
			break;
		case R.id.rl_negate://左侧按钮操作
			if(info.state == 1){//退款
				refund();
			}else if(info.state == 2 || info.state == 3){//延期
				postpone();
			}
			break;
		case R.id.rl_sure://右侧按钮操作
			if (info.state == 1) {//发货
				shipments();
			}else if(info.state == 2 || info.state == 3){//提醒
				remind();
			}
			break;
		}
		return super.handClickEvent(v);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		rlCancellation = (RelativeLayout) findViewById(R.id.rl_cancellation);//取消订单
		rlStateBackground = (RelativeLayout) findViewById(R.id.rl_state_background);//状态背景
		rlNegate = (RelativeLayout) findViewById(R.id.rl_negate);//左侧按钮操作
		rlSure = (RelativeLayout) findViewById(R.id.rl_sure);//右侧按钮操作
		llOperation = (LinearLayout) findViewById(R.id.ll_operation);//其他操作
		lvCommoditys =(MyListView) findViewById(R.id.lv_commoditys);//商品详情
		tvCancel = (TextView) findViewById(R.id.tv_cancel);//取消订单按钮
		tvNegate = (TextView) findViewById(R.id.tv_negate);//左侧按钮
		tvSure = (TextView) findViewById(R.id.tv_sure);//右侧按钮
		tvOrderState = (TextView) findViewById(R.id.tv_order_state);//订单状态
		tvOrderPlan = (TextView) findViewById(R.id.tv_order_plan);//订单操作
		tvClientName = (TextView) findViewById(R.id.tv_client_name);//收货人姓名
		tvClientPhone = (TextView) findViewById(R.id.tv_client_phone);//收货人电话
		tvClientAddress = (TextView) findViewById(R.id.tv_client_address);//收货人地址
		tvRemark = (TextView) findViewById(R.id.tv_remark);//订单备注
		tvExpressage = (TextView) findViewById(R.id.tv_expressage);//快递费
		tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);//总价
		tvRealityPrice = (TextView) findViewById(R.id.tv_reality_price);//实际付款金额
		tvTime = (TextView) findViewById(R.id.tv_time);//订单号以及其他时间
		rlCancellation.setOnClickListener(singleListener);
		rlNegate.setOnClickListener(singleListener);
		rlSure.setOnClickListener(singleListener);
		if(info.state == 0){//未付款
			rlCancellation.setVisibility(View.VISIBLE);
			llOperation.setVisibility(View.GONE);
			rlStateBackground.setBackgroundResource(R.drawable.weifukuan);
			tvOrderState.setText("买家未付款");
			tvOrderPlan.setText("付款后请马上安排发货");
			String NoPayStr = "订单编号："+info.orderId +"\n创建时间："+info.orderstime;
			tvTime.setText(NoPayStr);
		}else if(info.state == 1){//已付款，待发货
			rlCancellation.setVisibility(View.GONE);
			llOperation.setVisibility(View.VISIBLE);
			tvNegate.setText("退款");
			tvSure.setText("立即发货");
			rlStateBackground.setBackgroundResource(R.drawable.yifukuan);
			tvOrderState.setText("买家已付款");
			tvOrderPlan.setText("请马上安排发货");
			String NoSendStr = "订单编号："+info.orderId +"\n订单交易号："+info.batchNo+"\n创建时间："+info.orderstime+"\n付款时间："+info.paymenttime;
			tvTime.setText(NoSendStr);
		}else if(info.state == 2 || info.state == 3){//已发货，待签收
			rlCancellation.setVisibility(View.GONE);
			llOperation.setVisibility(View.VISIBLE);
			tvNegate.setText("延期收货");
			tvSure.setText("提醒发货");
			rlStateBackground.setBackgroundResource(R.drawable.yifahuo);
			tvOrderState.setText("卖家已发货");
			tvOrderPlan.setText("货物正火速飞往用户手中");
			String SendStr = "订单编号："+info.orderId +"\n订单交易号："+info.batchNo+"\n创建时间："+info.orderstime+"\n付款时间："+info.paymenttime
					+"\n发货时间："+info.shipmentsTime;
			tvTime.setText(SendStr);
		}else if(info.state == 4){//已签收,待评价
			rlCancellation.setVisibility(View.VISIBLE);
			llOperation.setVisibility(View.GONE);
			tvCancel.setText("订单跟踪");
			rlStateBackground.setBackgroundResource(R.drawable.jiaoyichenggon);
			tvOrderState.setText("交易成功");
			tvOrderPlan.setText("请您对此次服务做一个评价吧");
			String SucceefulStr = "订单编号："+info.orderId +"\n订单交易号："+info.batchNo+"\n创建时间："+info.orderstime+"\n付款时间："+info.paymenttime
					+"\n发货时间："+info.shipmentsTime+"\n成交时间："+info.receiptTime;
			tvTime.setText(SucceefulStr);
		}
		OrderCommoditysAdapter adapter = new OrderCommoditysAdapter(this,info.commoditys);
		lvCommoditys.setAdapter(adapter);
		tvClientName.setText(info.receivename);
		tvExpressage.setText(info.freightprice+"元");
		tvRealityPrice.setText(info.payment+"元");
	}

	/**
	 * 取消订单
	 */
	private void cancellation() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_CANCELLATION_ORDER,"取消订单");
		RequestParams params = new RequestParams();
		params.put("orderId",info.orderId);
		params.put("isPlatform",1);
		HttpTools.httpDelete(Contants.URl.URl_3026,"/orders" ,config, params);
	}
	/**
	 * 申请退款
	 */
	private void refund() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_REFUND_INFO,"申请退款");
		RequestParams params = new RequestParams();
		params.put("orderid",info.orderId);
		params.put("mshopid",ShoppingInfo.id);
		params.put("type",4);//1退货，2换货，3返修,4退款
		HttpTools.httpPost(Contants.URl.URl_3026,"/refund" ,config, params);
	}
	/**
	 * 立即发货
	 */
	private void shipments() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_SHIPMENTS_ORDER,"立即发货");
		RequestParams params = new RequestParams();
		params.put("orderId",info.orderId);
		params.put("state",2);
		HttpTools.httpPut(Contants.URl.URl_3026,"/orders/putordersState" ,config, params);
	}
	/**
	 * 延期收货
	 */
	private void postpone() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_POSTPONE_ORDER,"延期收货");
		RequestParams params = new RequestParams();
		//HttpTools.httpPut(Contants.URl.URl_3026,"/orders/putordersState" ,config, params);
	}
	/**
	 * 提醒收货
	 */
	private void remind() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_REMIND_ORDER,"提醒收货");
		RequestParams params = new RequestParams();
		//HttpTools.httpPut(Contants.URl.URl_3026,"/orders/putordersState" ,config, params);
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1 == HttpTools.SET_CANCELLATION_ORDER){//取消订单
			
		}else if(msg.arg1 == HttpTools.SET_REFUND_INFO){//申请退款
			
		}else if(msg.arg1 == HttpTools.SET_SHIPMENTS_ORDER){//立即发货
			
		}else if(msg.arg1 == HttpTools.SET_POSTPONE_ORDER){//延期收货
			
		}else if(msg.arg1 == HttpTools.SET_POSTPONE_ORDER){//提醒收货
			
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_order_details,
				null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "订单详情";
	}

}
