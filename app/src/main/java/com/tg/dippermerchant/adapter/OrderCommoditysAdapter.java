package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.R;

public class OrderCommoditysAdapter extends MyBaseAdapter<OrderCommoditysInfo> {
	private ArrayList<OrderCommoditysInfo> list;
	private LayoutInflater inflater;
	private OrderCommoditysInfo item;
	private Activity context;

	public OrderCommoditysAdapter(Activity con, ArrayList<OrderCommoditysInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.order_commoditys_item,null);
		}
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView tvCName = (TextView) convertView.findViewById(R.id.tv_cName);
		TextView tvCoriginalPrice = (TextView) convertView.findViewById(R.id.tv_coriginalPrice);
		TextView tvQuantity = (TextView) convertView.findViewById(R.id.tv_quantity);
		tvCName.setText(item.cName);
		tvCoriginalPrice.setText(item.coriginalPrice+"元");
		tvQuantity.setText("x"+item.quantity);
		VolleyUtils.getImage(Tools.mContext, item.imgUrl, ivHead,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		return convertView;
	}


}
