package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.ConsumerCodeManageMentInfo;
import com.tg.dippermerchant.R;

public class ConsumerCodeManageMentAdapter extends MyBaseAdapter<ConsumerCodeManageMentInfo>{
	private ArrayList<ConsumerCodeManageMentInfo> list;
	private LayoutInflater inflater;
	private ConsumerCodeManageMentInfo item;
	private Context context;
	public ConsumerCodeManageMentAdapter(Context context,ArrayList<ConsumerCodeManageMentInfo> list){
		super(list);
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if(convertView ==null ){
			convertView = inflater.inflate(R.layout.comsumer_code_item, null);
		}
		TextView tvUseCede = (TextView) convertView.findViewById(R.id.tv_useCede);
		TextView tvState = (TextView) convertView.findViewById(R.id.tv_state);
		TextView tvcName = (TextView) convertView.findViewById(R.id.tv_cName);
		TextView tvcPrice = (TextView) convertView.findViewById(R.id.tv_cPrice);
		TextView tvUser = (TextView) convertView.findViewById(R.id.tv_user);
		TextView tvUseTime = (TextView) convertView.findViewById(R.id.tv_useTime);
		tvUseCede.setText(item.useCede);
		tvcName.setText(item.cName);
		tvcPrice.setText(item.cPrice+"元");
		tvUser.setText("13046524635");
		tvUseTime.setText(item.useTime);
		if(item.state == 0){
			tvState.setText("已验证");
		}else{
			tvState.setText("已消费");
		}
		return convertView;
	}
}
