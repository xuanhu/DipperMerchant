package com.tg.dippermerchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.dippermerchant.R;
import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.info.NewsTypeInfo;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.util.Tools;

import java.util.ArrayList;

public class NewsTypeAdapter extends MyBaseAdapter<NewsTypeInfo> {
	private ArrayList<NewsTypeInfo> list;
	private LayoutInflater inflater;
	private NewsTypeInfo item;
	private ImageView imgIcon;
	private TextView  tvTitle;
	private TextView  tvType;
	private TextView  tvTime;
	private TextView  tvNotread;
	public NewsTypeAdapter(Context con, ArrayList<NewsTypeInfo> list){
		super(list);
		this.list = list;
		inflater = LayoutInflater.from(con);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		item = list.get(position);
		if(convertView ==null ){
			convertView = inflater.inflate(R.layout.message_type_item, null);
		}
		imgIcon = (ImageView)convertView.findViewById(R.id.message_icon);
		tvTitle = (TextView)convertView.findViewById(R.id.message_title);
		tvType = (TextView)convertView.findViewById(R.id.message_type);
		tvTime = (TextView)convertView.findViewById(R.id.add_time);
		tvNotread = (TextView)convertView.findViewById(R.id.tv_notread);
		tvType.setText(item.weiappname);
		tvTitle.setText(item.content);
		VolleyUtils.getImage(Tools.mContext, item.icon, imgIcon,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		if(item.notread == 0){
			tvNotread.setVisibility(View.GONE);
		}else{
			tvNotread.setVisibility(View.VISIBLE);
			tvNotread.setText(String.valueOf(item.notread));
		}
		if(item.homePushTime != null){
			/*boolean isToday;
			IsTodayutil isTodayUtil = new IsTodayutil();
			try {
				isToday = isTodayUtil.IsToday(item.addtime);
				if(isToday){//表示消息时间是今天（只显示几时几分或者刚刚）
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					long servicetime = Tools.dateString2Millis(item.addtime);//获取到的时间
					long timestamp = time - servicetime;//当前时间和服务时间差
					long minutes = 10*60*1000;//10分钟转换为多少毫秒
					if( timestamp > minutes){
						String nowTime = Tools.getSecondToString(servicetime);
						tvTime.setText(nowTime);
					}else {
						tvTime.setText("刚刚");
					}
				}else{
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					String newYear = Tools.getSimpleDateToString(time);//
					String serviceYear = item.addtime.substring(0,item.addtime.indexOf(" "));
					if(newYear.substring(0,4).equals(serviceYear.substring(0,4))){//表示消息时间是今年
						tvTime.setText(serviceYear.substring(5,serviceYear.length()));
					}else{
						tvTime.setText(serviceYear);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			tvTime.setText(item.homePushTime);
		}
		return convertView;
	}
}
