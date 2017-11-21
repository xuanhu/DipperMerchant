package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.WithdrawalRecordInfo;
import com.tg.dippermerchant.R;

public class WithdrawalRecordAdapter extends MyBaseAdapter<WithdrawalRecordInfo>{
	private ArrayList<WithdrawalRecordInfo> list;
	private LayoutInflater inflater;
	private WithdrawalRecordInfo item;
	private TextView  tvDate,tvMoney,tvAddTime;
	private Activity context;
	public WithdrawalRecordAdapter(Activity con, ArrayList<WithdrawalRecordInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.withdrawal_record_item, null);
		}
		tvDate = (TextView)convertView.findViewById(R.id.tv_date);
		tvAddTime = (TextView)convertView.findViewById(R.id.tv_add_time);
		tvMoney = (TextView)convertView.findViewById(R.id.tv_money);
		String payaccount = item.payaccount;
		payaccount = payaccount.substring(payaccount.length()-4,payaccount.length());
		tvDate.setText("提现至"+item.bankname+"("+payaccount+")");
		tvAddTime.setText(item.addTime);
		tvMoney.setText("￥"+item.money);
		return convertView;
	}

}
