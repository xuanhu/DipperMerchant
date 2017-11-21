package com.tg.dippermerchant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.R;

/**
 * Created by Administrator on 2017/10/19.
 */

public class OrderSelectAdapter extends BaseAdapter {
    private Context mContext;
    private int selectorPosition;

    public String[] img_text = { "全部订单", "待付款", "待发货",  "待收货", "待评价",
            "已评价", "已退款", "已取消"};

    public OrderSelectAdapter(Context mContext) {
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
                    R.layout.grid_order_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tv);
        textView.setText(img_text[position]);
        //如果当前的position等于传过来点击的position,就去改变他的状态
        if (selectorPosition == position) {
            textView.setTextColor(mContext.getResources().getColor(R.color.home_fill));
        } else {
            //其他的恢复原来的状态
            textView.setTextColor(mContext.getResources().getColor(R.color.text_color3));
        }
        return convertView;
    }
    public void changeState(int pos) {
        selectorPosition = pos;
        notifyDataSetChanged();
    }
}

