package com.tg.dippermerchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.dippermerchant.R;
import com.tg.dippermerchant.base.BaseViewHolder;

/**
 * Created by Administrator on 2017/9/19.
 */

public class MoreGridAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text = { "商品管理", "运营统计",  "提现申请",
            "商家公告"};
	public int[] imgs = { R.drawable.merchandise_management,
            R.drawable.statistics,R.drawable.withdrawal, R.drawable.notice
           };

    public MoreGridAdapter(Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_home_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_grid_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_grid_item);
        iv.setBackgroundResource(imgs[position]);

        tv.setText(img_text[position]);
        return convertView;
    }

}

