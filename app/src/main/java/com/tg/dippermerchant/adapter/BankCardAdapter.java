package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.BankCardInfo;
import com.tg.dippermerchant.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BankCardAdapter extends MyBaseAdapter<BankCardInfo> {
	private ArrayList<BankCardInfo> list;
	private LayoutInflater inflater;
	private BankCardInfo item;

	public BankCardAdapter(Context context, ArrayList<BankCardInfo> list) {
		super(list);
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bank_listview, null);
		}
		TextView tvBank = (TextView) convertView.findViewById(R.id.tv_bank);
		TextView tvAccount = (TextView) convertView.findViewById(R.id.tv_account);
		tvBank.setText(item.name);
		tvAccount.setText(item.account);
		return convertView;
	}
}
