package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.CommodityInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.ManageMentLinearlayout;
import com.tg.dippermerchant.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;

public class CommodityMentAdapter extends MyBaseAdapter<CommodityInfo> implements MessageHandler.ResponseListener {
	private ArrayList<CommodityInfo> list;
	private LayoutInflater inflater;
	private CommodityInfo item;
	private Activity context;
	private int id;
	private int state;
	private MessageHandler msgHand;
	private int operationPosition;


	/**
     * 自定义一个刷新列表接口，用于回调CommodityManageMentActivity
     */
    public interface CommodityCallback {
        public void doCallBack(int i);
    }

    private CommodityCallback commentcallback;
    
    public void setCommodityCallback(CommodityCallback commodityCallback) {
        this.commentcallback = commodityCallback;
    }
	public CommodityMentAdapter(Activity con, ArrayList<CommodityInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
		msgHand = new MessageHandler(con);
		msgHand.setResponseListener(this);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.commddity_management_item, null);
		}
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvOriginalPrice = (TextView) convertView.findViewById(R.id.tv_originalPrice);
		TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_amount);
		TextView tvShelvesChoose = (TextView) convertView.findViewById(R.id.tv_shelves_choose);
		RelativeLayout rlShelves = (RelativeLayout) convertView.findViewById(R.id.rl_shelves);
		rlShelves.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				id = list.get(position).id;
				state = list.get(position).state;
				if(state == 0){
					DialogFactory.getInstance().showDialog(context, new OnClickListener() {
						@Override
						public void onClick(View v) {
							PutCommodity();
						}
					}, null, "确定要重新上架吗", null, null);
				}else{
					DialogFactory.getInstance().showDialog(context, new OnClickListener() {
						@Override
						public void onClick(View v) {
							PutCommodity();
						}
					}, null, "确定要下架吗", null, null);
				}
				
			}
		});
		tvName.setText(item.name);
		tvOriginalPrice.setText("供货价："+item.originalPrice+"元");
		tvAmount.setText("库存："+item.amount);
		if(item.state == 0){
			tvShelvesChoose.setText("上架");
			/*tvShelvesChoose.setTextColor(context.getResources().getColor(R.color.shelves_up));
			rlShelves.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_shelves_up));*/
		}else if(item.state == 1){
			tvShelvesChoose.setText("下架");
			/*tvShelvesChoose.setTextColor(context.getResources().getColor(R.color.shelves_down));
			rlShelves.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_shelves_down));*/
		}
		VolleyUtils.getImage(Tools.mContext, item.imgUrl, ivHead,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		return convertView;
	}
	/**
	 * 上/下架
	 */
	private void PutCommodity(){
		RequestConfig config = new RequestConfig(context,0);
		config.handler = msgHand.getHandler();
		RequestParams params = new RequestParams();
		params.put("id",id);
		params.put("isPlatform", 1);
		if(state == 0){//下架状态
			params.put("state", 1);
		}else if(state == 1){
			params.put("state", 0);
		}
		HttpTools.httpPut(Contants.URl.URl_3026, "/commodity",config, params);
	}
	@Override
	public void onRequestStart(Message msg, String hintString) {

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		int code = HttpTools.getCode(jsonString);
		if(code == 0){
			ToastFactory.showToast(context, "修改成功");
			commentcallback.doCallBack(item.type);
		}else{
			ToastFactory.showToast(context, "修改失败");
		}
	}

	@Override
	public void onFail(Message msg, String hintString) {

	}
}
