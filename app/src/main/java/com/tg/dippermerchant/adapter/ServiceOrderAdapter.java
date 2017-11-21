package com.tg.dippermerchant.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.R;
import com.tg.dippermerchant.ServiceOrderDetailsActivity;
import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.info.ServiceOrderInfo;
import com.tg.dippermerchant.net.MessageHandler;


import java.util.ArrayList;

public class ServiceOrderAdapter extends MyBaseAdapter<ServiceOrderInfo> implements MessageHandler.ResponseListener {
	private ArrayList<ServiceOrderInfo> list;
	private LayoutInflater inflater;
	private ServiceOrderInfo item;
	private Activity context;
	private String orderId;
	private ArrayList<OrderCommoditysInfo> commoditysList;
	private MessageHandler msgHand;
	private int operationPosition;
    private AlertDialog dialog;



	/**
     * 自定义一个刷新列表接口，用于回调CommodityManageMentActivity
     */
    public interface OrderManageMentCallback {
        public void doCallBack();
    }

    private OrderManageMentCallback orderManageMentCallback;

    public void setAfterSalesCallback(OrderManageMentCallback orderManageMentCallback) {
        this.orderManageMentCallback = orderManageMentCallback;
    }

	public ServiceOrderAdapter(Activity con, ArrayList<ServiceOrderInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
		msgHand = new MessageHandler(con);
		msgHand.setResponseListener(this);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.service_order_item, null);
		}
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
		TextView tvState = (TextView) convertView.findViewById(R.id.tv_state);
		RelativeLayout rlBtn1 = (RelativeLayout) convertView.findViewById(R.id.rl_btn1);
		RelativeLayout rlBtn2 = (RelativeLayout) convertView.findViewById(R.id.rl_btn2);
		RelativeLayout rlBtn3 = (RelativeLayout) convertView.findViewById(R.id.rl_btn3);
		TextView tvBtn1 = (TextView) convertView.findViewById(R.id.tv_btn1);
		TextView tvBtn2 = (TextView) convertView.findViewById(R.id.tv_btn2);
		TextView tvBtn3 = (TextView) convertView.findViewById(R.id.tv_btn3);

		rlBtn1.setVisibility(View.VISIBLE);
		rlBtn2.setVisibility(View.GONE);
		rlBtn3.setVisibility(View.GONE);
		tvBtn1.setText("查看订单");

		rlBtn1.setOnClickListener(new View.OnClickListener() {//查看订单
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,ServiceOrderDetailsActivity.class);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	@Override
	public void onRequestStart(Message msg, String hintString) {

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {

	}

	@Override
	public void onFail(Message msg, String hintString) {

	}
}
