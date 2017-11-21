package com.tg.dippermerchant.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.LinkManInfo;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.view.RoundImageView;
import com.tg.dippermerchant.R;


public class LinkmanAdapter  extends MyBaseAdapter<LinkManInfo>{
	private ArrayList<LinkManInfo> list;
	private LayoutInflater inflater;
	private LinkManInfo item;
	private Context context;

	public LinkmanAdapter(Context con, ArrayList<LinkManInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.link_man_item, null);
		}
		item = list.get(position);
		RoundImageView rivHead = (RoundImageView) convertView.findViewById(R.id.riv_head);
		rivHead.setCircleShape();
		TextView tvname= (TextView) convertView.findViewById(R.id.tv_name);
		tvname.setText(item.realname);
		VolleyUtils.getImage(context, item.icon,rivHead,R.drawable.moren_geren);
		return convertView;
	}

}

