package com.tg.dippermerchant.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.MyBrowserActivity;
import com.tg.dippermerchant.OrderManageMentActivity;
import com.tg.dippermerchant.ShipmentActivity;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.OrderCommoditysInfo;
import com.tg.dippermerchant.info.OrderInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;

public class OrderManageMentAdapter extends MyBaseAdapter<OrderInfo> implements MessageHandler.ResponseListener {
	private ArrayList<OrderInfo> list;
	private LayoutInflater inflater;
	private OrderInfo item;
	private Activity context;
	private String orderId;
	private ArrayList<OrderCommoditysInfo> commoditysList;
	private MessageHandler msgHand;
	private int operationPosition;
    private AlertDialog dialog;


	/**
     * 自定义一个刷新列表接口，用于回调CommodityManageMentActivity
     */
    public interface OrderManageMentCallback {
        public void doCallBack();
    }

    private OrderManageMentCallback orderManageMentCallback;
    
    public void setAfterSalesCallback(OrderManageMentCallback orderManageMentCallback) {
        this.orderManageMentCallback = orderManageMentCallback;
    }

	public OrderManageMentAdapter(Activity con, ArrayList<OrderInfo> list) {
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
			convertView = inflater.inflate(R.layout.order_management_item,null);
		}
		ListView lvCommoditys = (ListView) convertView.findViewById(R.id.lv_commoditys);
		TextView tvOrderId  = (TextView) convertView.findViewById(R.id.tv_orderId);
		TextView tvState  = (TextView) convertView.findViewById(R.id.tv_state);
		RelativeLayout rlBtn1 = (RelativeLayout) convertView.findViewById(R.id.rl_btn1);
		RelativeLayout rlBtn2 = (RelativeLayout) convertView.findViewById(R.id.rl_btn2);
		RelativeLayout rlBtn3 = (RelativeLayout) convertView.findViewById(R.id.rl_btn3);
        TextView tvBtn1  = (TextView) convertView.findViewById(R.id.tv_btn1);
        TextView tvBtn2  = (TextView) convertView.findViewById(R.id.tv_btn2);
        TextView tvBtn3  = (TextView) convertView.findViewById(R.id.tv_btn3);

		tvOrderId.setText("NO:"+item.orderId);
		if(item.state == 0){
			tvState.setText("待支付");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.VISIBLE);
            rlBtn3.setVisibility(View.GONE);
            tvBtn1.setText("查看订单");
            tvBtn2.setText("取消订单");
		}else if(item.state == 1){
			tvState.setText("待发货");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.VISIBLE);
            rlBtn3.setVisibility(View.VISIBLE);
			tvBtn1.setText("查看订单");
            tvBtn2.setText("退款");
            tvBtn3.setText("立即发货");
		}else if(item.state == 2 || item.state == 3){
			tvState.setText("待收货");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == 4){
			tvState.setText("待评价");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == 5){
			tvState.setText("申请退货");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == 6){
			tvState.setText("申请退款");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == 7){
			tvState.setText("已退货");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
		}else if(item.state == 8){
			tvState.setText("已退款");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == 9){
			tvState.setText("订单完成");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == -2){
			tvState.setText("管理员取消");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}else if(item.state == -1){
			tvState.setText("会员取消");
            rlBtn1.setVisibility(View.VISIBLE);
            rlBtn2.setVisibility(View.GONE);
            rlBtn3.setVisibility(View.GONE);
			tvBtn1.setText("查看订单");
		}
        rlBtn1.setOnClickListener(new OnClickListener() {//查看订单
            @Override
            public void onClick(View v) {
                OrderInfo item = list.get(position);
                Intent intent = new Intent(context,MyBrowserActivity.class);
                intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+ Tools.getEncryptURL(DES.KEY_URl,"&type=0&oid="+item.orderId));
                context.startActivity(intent);
            }
        });
        rlBtn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				operationPosition = position;
				if(list.get(operationPosition).state == 0){//取消订单
					cancelOrder();
				}else if(list.get(operationPosition).state == 1){//退款
					DialogFactory.getInstance().showDialog(context, new OnClickListener() {
						@Override
						public void onClick(View v) {
							PostRefund();
						}
					}, null, "确定要退款吗", null, null);

				}
            }
        });
        rlBtn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				operationPosition = position;
				if(list.get(operationPosition).state == 1){//立即发货
					Intent intent = new Intent(context,ShipmentActivity.class);
					intent.putExtra(ShipmentActivity.ORDERINFO ,list.get(operationPosition));
					context.startActivity(intent);
				}
            }
        });
		lvCommoditys.setEnabled(false);
		commoditysList  = item.commoditys;
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

	/**
	 * 取消订单
	 */
	private void PutordersState(){
		RequestConfig config = new RequestConfig(context,HttpTools.PUT_PUTORDERSSTATE);
		config.handler = msgHand.getHandler();
		RequestParams params = new RequestParams();
		params.put("orderId",list.get(operationPosition).orderId);
		params.put("operatorId",ShoppingInfo.id);
		params.put("state",-2);
		HttpTools.httpPut(Contants.URl.URl_3026,"/orders/putordersState" ,config, params);
	}
	/**
	 * 退款
	 */
	private void PostRefund(){
		RequestConfig config = new RequestConfig(context,HttpTools.POST_REFUND);
		config.handler = msgHand.getHandler();
		RequestParams params = new RequestParams();
		params.put("orderid",list.get(operationPosition).orderId);
		params.put("mshopid",ShoppingInfo.id);
		params.put("type",4);//1退货，2换货，3返修,4退款
		HttpTools.httpPost(Contants.URl.URl_3026,"/refund" ,config, params);
	}
	/**
	 * 转换为json数据
	 */
	private  String changeToJson() {
		JSONObject object=null;
		JSONObject object2=null;
		JSONArray jsonArray=null;
		object=new JSONObject();
		object2=new JSONObject();
		jsonArray=new JSONArray();
		try {
			for (int i = 0; i < commoditysList.size(); i++) {
				object2.put("commodityId",commoditysList.get(i).cName); 
			}
			jsonArray.put(object2);
			String personInfos = jsonArray.toString(); // 将JSONArray转换得到String
			object.put("personInfos", personInfos);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonString = null;
		jsonString = object.toString();// 把JSONObject转换成json格式的字符串
		return jsonString;
	}

	@Override
	public void onRequestStart(Message msg, String hintString) {

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.PUT_PUTORDERSSTATE){//取消订单
			if(code == 0){
				ToastFactory.showToast(context, "取消成功");
				orderManageMentCallback.doCallBack();
			}else {
				ToastFactory.showToast(context, message);
			}
		}else {//退款
			if(code == 0 ){
				ToastFactory.showToast(context, "退款成功");
				orderManageMentCallback.doCallBack();
			}else {
				ToastFactory.showToast(context, message);
			}
		}
	}

	@Override
	public void onFail(Message msg, String hintString) {

	}
    /**
     * 验证个人密码
     */
    private void cancelOrder(){
        dialog = null;
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(R.layout.custom_alert_dialog);
            window.findViewById(R.id.dialog_button_ok).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {//确定
					PutordersState();
					dialog.dismiss();
                }
            });
            window.findViewById(R.id.dialog_button_cancel).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {//取消
                    dialog.dismiss();
                }
            });
            DisplayMetrics dm = Tools.getDisplayMetrics(context);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (dm.widthPixels - 100 * dm.density);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }
}
