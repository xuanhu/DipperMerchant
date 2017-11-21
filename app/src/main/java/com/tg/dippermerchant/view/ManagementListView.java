package com.tg.dippermerchant.view;


import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.net.MessageHandler.ResponseListener;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ListView;

public class ManagementListView extends ListView implements ResponseListener{

	public interface NetListRequestListener {
		public void onRequest(MessageHandler msgHand);
		public void onSuccess(ManagementListView textview, Message msg, String response);
	}

	private NetListRequestListener requestListener;
	private boolean isLoadding = false;
	private MessageHandler msgHandler;
	private Activity mActivity;

	public ManagementListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ManagementListView(Context context) {
		super(context);
		initView(context);
	}

	public ManagementListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context con) {
		mActivity = (Activity) con;
		msgHandler = new MessageHandler(con);
		msgHandler.setResponseListener(this);
	}

	public void loaddingData() {
		if (!isLoadding) {
			if (requestListener != null) {
				isLoadding = true;
				requestListener.onRequest(msgHandler);
			}
		}
	}

	public void setListRequestListener(NetListRequestListener l) {
		requestListener = l;
	}

	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		String Content = HttpTools.getContentString(jsonString);
		if (Content != null) {
			ResponseData data = HttpTools.getResponseData(Content);
			if (data == null || data.length == 0) {
				
			} else {
				if (requestListener != null) {
					requestListener.onSuccess(this, msg, jsonString);
				}
			}
		}
	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub

	}

}

