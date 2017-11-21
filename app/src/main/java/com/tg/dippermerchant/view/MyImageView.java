package com.tg.dippermerchant.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.MessageHandler.ResponseListener;

public class MyImageView extends ImageView implements ResponseListener{
	public interface ImageViewRequestListener {
		public void onRequest(MessageHandler msgHand);
		public void onSuccess(MyImageView imageView, Message msg, String response);
	}
	private ImageViewRequestListener requestListener;
	private boolean isLoadding = false;
	private MessageHandler msgHandler;

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MyImageView(Context context) {
		super(context);
		initView(context);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context con) {
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

	public void setImageViewRequestListener(ImageViewRequestListener l) {
		requestListener = l;
	}


	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		int code = HttpTools.getCode(jsonString);
		if (code == 0 || code == 300) {
			if (requestListener != null) {
				requestListener.onSuccess(this, msg, jsonString);
			}
		}
		isLoadding = false;

	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
	}
}

