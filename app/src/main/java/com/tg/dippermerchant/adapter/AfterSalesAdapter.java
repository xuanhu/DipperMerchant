package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.AfterSalesInfo;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.view.MyButton;
import com.tg.dippermerchant.view.MyButton.ButtonRequestListener;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;

public class AfterSalesAdapter extends MyBaseAdapter<AfterSalesInfo>{
	private ArrayList<AfterSalesInfo> list;
	private LayoutInflater inflater;
	private AfterSalesInfo item;
	private Activity context;
	private int id; 
	 /**
     * 自定义一个刷新列表接口，用于回调CommodityManageMentActivity
     */
    public interface AfterSalesCallback {
        public void doCallBack();
    }

    private AfterSalesCallback afterSalesCallback;
    
    public void setAfterSalesCallback(AfterSalesCallback afterSalesCallback) {
        this.afterSalesCallback = afterSalesCallback;
    }
	public AfterSalesAdapter(Activity con, ArrayList<AfterSalesInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.after_sales_item, null);
		}
		ListView lvCommoditys = (ListView) convertView.findViewById(R.id.lv_commoditys);
		TextView tvState =(TextView) convertView.findViewById(R.id.tv_state);
		RelativeLayout rlHandle = (RelativeLayout) convertView.findViewById(R.id.rl_handle);
		RelativeLayout rlManage = (RelativeLayout) convertView.findViewById(R.id.rl_manage);
		final MyButton btnRefuse = (MyButton) convertView.findViewById(R.id.btn_refuse);
		final MyButton btnAgree = (MyButton) convertView.findViewById(R.id.btn_agree);
		final MyButton btnDelete = (MyButton) convertView.findViewById(R.id.btn_delete);
		btnRefuse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//拒绝
				id = list.get(position).id;
				btnRefuse.loaddingData();
			}
		});
		btnAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//同意
				id = list.get(position).id;
				btnAgree.loaddingData();
				
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//删除
				id = list.get(position).id;
				btnDelete.loaddingData();
				
			}
		});
		btnRefuse.setButtonRequestListener(new ButtonRequestListener() {
			
			@Override
			public void onSuccess(MyButton button, Message msg, String response) {
				int code = HttpTools.getCode(response);
				if(code == 0 ){
					ToastFactory.showToast(context, "拒绝成功");
					afterSalesCallback.doCallBack();
				}
			}
			
			@Override
			public void onRequest(MessageHandler msgHand) {
				RequestConfig config = new RequestConfig(context,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("id",id);
				params.put("status",-1);
				HttpTools.httpPut(Contants.URl.URl_3026, "/refund",config, params);
			}
		});
		btnAgree.setButtonRequestListener(new ButtonRequestListener() {
			
			@Override
			public void onSuccess(MyButton button, Message msg, String response) {
				int code = HttpTools.getCode(response);
				if(code == 0 ){
					ToastFactory.showToast(context, "退款成功");
					afterSalesCallback.doCallBack();
				}
			}
			
			@Override
			public void onRequest(MessageHandler msgHand) {
				RequestConfig config = new RequestConfig(context,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("id",id);
				params.put("status",4);
				HttpTools.httpPut(Contants.URl.URl_3026, "/refund",config, params);
			}
		});
		btnDelete.setButtonRequestListener(new ButtonRequestListener() {
			
			@Override
			public void onSuccess(MyButton button, Message msg, String response) {
				int code = HttpTools.getCode(response);
				if(code == 0 ){
					ToastFactory.showToast(context, "删除成功");
					afterSalesCallback.doCallBack();
				}
			}
			
			@Override
			public void onRequest(MessageHandler msgHand) {
				RequestConfig config = new RequestConfig(context,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("id",id);
				params.put("status",4);
				HttpTools.httpDelete(Contants.URl.URl_3026, "/refund/"+id,config, params);
			}
		});
		if(item.status == 0 ){
			tvState.setText("处理中");
			rlManage.setVisibility(View.VISIBLE);
			rlHandle.setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.GONE);
		}else if(item.status == 4){
			tvState.setText("已退款");
			rlManage.setVisibility(View.VISIBLE);
			rlHandle.setVisibility(View.GONE);
			btnDelete.setVisibility(View.VISIBLE);
		}else if(item.status == -3){
			tvState.setText("退款失败");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == -2){
			tvState.setText("已取消");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == -1){
			tvState.setText("未通过审核");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == 3){
			tvState.setText("待退款");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == 1){
			tvState.setText("待用户退货");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == 2){
			tvState.setText("待收货");
			rlManage.setVisibility(View.GONE);
		}else if(item.status == 5){
			tvState.setText("已完成");
			rlManage.setVisibility(View.GONE);
		}
		//VolleyUtils.getImage(Tools.mContext, item.imgurl, ivHead,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		lvCommoditys.setEnabled(false);
		ArrayList<OrderCommoditysInfo> commoditysList = item.commoditys;
		OrderCommoditysAdapter adapter = new OrderCommoditysAdapter(context,commoditysList);
		lvCommoditys.setAdapter(adapter);
		/**
		 * 重新计算listview的高度
		 */
		int totalHeight = 0;
        for(int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
            View listItem = adapter.getView(i, null, lvCommoditys);  
            listItem.measure(0, 0); // 计算子项View 的宽高  
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
        }  
        ViewGroup.LayoutParams params = lvCommoditys.getLayoutParams();
        params.height = totalHeight+(lvCommoditys.getDividerHeight() * (adapter.getCount() - 1));  
        lvCommoditys.setLayoutParams(params); 
		return convertView;
	}
	}
