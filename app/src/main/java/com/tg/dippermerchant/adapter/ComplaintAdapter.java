package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.ComplaintInfo;
import com.tg.dippermerchant.R;

public class ComplaintAdapter extends MyBaseAdapter<ComplaintInfo>{
	private ArrayList<ComplaintInfo> list;
	private LayoutInflater inflater;
	private ComplaintInfo item;
	private Context context ;

	public ComplaintAdapter(Context con, ArrayList<ComplaintInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.my_complaint_item, null);
		}
		item = list.get(position);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tvState = (TextView) convertView.findViewById(R.id.tv_state);
		TextView tvType = (TextView) convertView.findViewById(R.id.tv_type);
		TextView tvSection = (TextView) convertView.findViewById(R.id.tv_section);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
		tvTime.setText("投诉时间："+item.time);
		tvState.setText(item.status);
		tvType.setText("投诉类型："+item.type);
		tvSection.setText("投诉部门："+item.section);
		tvContent.setText("投诉内容："+item.content);
		
		
		return convertView;
	}
}
