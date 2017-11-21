package com.tg.dippermerchant.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.dippermerchant.R;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.MessageHandler.ResponseListener;

import java.util.List;

public class ScrollBanner extends LinearLayout implements OnClickListener,
		ResponseListener {
	public interface ScrollBannerRequestListener {
		public void onRequest(MessageHandler msgHand);
		public void onSuccess(ScrollBanner scrollbanner, Message msg, String response);
	}
	public interface ScrollBannerOnItemClickListener {
		public void onItem1Click(int postion);

		public void onItem2Click(int postion);
	}
	private ScrollBannerRequestListener requestListener;
	private ScrollBannerOnItemClickListener onItemClickListener;
	private TextView mBannerTV1;
	private TextView mBannerTV2;
	private Handler handler;
	private boolean isShow;
	private int startY1, endY1, startY2, endY2;
	private Runnable runnable;
	private List<String> list;
	private int position = 0;
	private int offsetY = 100;
	private boolean isLoadding = false;
	private MessageHandler msgHandler;

	public ScrollBanner(Context context) {
		this(context, null);
		initView(context);
	}

	public ScrollBanner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initView(context);
	}

	private void initView(Context con) {
		msgHandler = new MessageHandler(con);
		msgHandler.setResponseListener(this);
	}

	public ScrollBanner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_scroll_banner, this);
		mBannerTV1 = (TextView) view.findViewById(R.id.tv_banner1);
		mBannerTV2 = (TextView) view.findViewById(R.id.tv_banner2);
		mBannerTV1.setOnClickListener(this);
		mBannerTV2.setOnClickListener(this);
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				isShow = !isShow;
				if (position == list.size())
					position = 0;
				if (isShow) {
					mBannerTV1.setText(list.get(position++));
				} else {
					mBannerTV2.setText(list.get(position++));
				}

				startY1 = isShow ? 0 : offsetY;
				endY1 = isShow ? -offsetY : 0;

				ObjectAnimator
						.ofFloat(mBannerTV1, "translationY", startY1, endY1)
						.setDuration(300).start();

				startY2 = isShow ? offsetY : 0;
				endY2 = isShow ? 0 : -offsetY;
				ObjectAnimator
						.ofFloat(mBannerTV2, "translationY", startY2, endY2)
						.setDuration(300).start();
				handler.postDelayed(runnable,3000);
			}
		};
	}
	public void setRightTextColor(int color){
		mBannerTV1.setTextColor(color);
		mBannerTV2.setTextColor(color);
	}
	
	public void setRightTextSize(int size){
		mBannerTV1.setTextSize(size);
		mBannerTV2.setTextSize(size);
	}
	public void loaddingData() {
		if (!isLoadding) {
			if (requestListener != null) {
				isLoadding = true;
				requestListener.onRequest(msgHandler);
			}
		}
	}

	public void startScroll() {
		handler.post(runnable);
	}

	public void stopScroll() {
		handler.removeCallbacks(runnable);
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void setScrollBannerOnItemClickListener(ScrollBannerOnItemClickListener l) {
		onItemClickListener = l;
	}
	
	public void setScrollBannerRequestListener(ScrollBannerRequestListener l) {
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

	@Override
	public void onClick(View v) {
		int finalPosition;
		if (position == 1) {
			finalPosition = list.size() - 1;
		} else {
			finalPosition = position - 2;
		}
		switch (v.getId()) {
		case R.id.tv_banner1:
			if (onItemClickListener != null) {
				onItemClickListener.onItem1Click(finalPosition);
			}
			break;
		case R.id.tv_banner2:
			if (onItemClickListener != null) {
				onItemClickListener.onItem2Click(finalPosition);
			}
			break;
		}
	}

}
