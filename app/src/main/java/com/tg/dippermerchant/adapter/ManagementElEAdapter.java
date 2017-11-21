package com.tg.dippermerchant.adapter;


import com.tg.dippermerchant.base.BaseViewHolder;
import com.tg.dippermerchant.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagementElEAdapter extends BaseAdapter{
	private Context mContext;
	public String[] img_text = { "订单管理", "商品管理", "添加商品",  "消费码管理", "提现申请"};
	public int[] imgs = { R.drawable.dingdan_manage,R.drawable.shangpin_manage,R.drawable.shangjia_manage,
			R.drawable.xiaofeima_manage,R.drawable.tixian_manage};
	
	public ManagementElEAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_ele, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_grid_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_grid_item);
		iv.setBackgroundResource(imgs[position]);

		tv.setText(img_text[position]);
		return convertView;
	}

}
