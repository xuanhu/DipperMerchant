package com.tg.dippermerchant.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;

public class TimingButton extends Button{
	public static final int MSG_UPDATE_UI = 1;
	public static final int TIME_SECONDS = 60 ;
	private int count = TIME_SECONDS;
	private String text = "获取验证码";
	private String text1 = "重新获取(";
	private Handler hand = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case MSG_UPDATE_UI:
					if(count < 0){
						enableButton();
					}else{  
						setText(text1+count--+")");
						hand.sendEmptyMessageDelayed(MSG_UPDATE_UI ,1000);
					}
					break;
			}
		}
	};
	
	public void startTiming(){
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		ViewGroup.LayoutParams lp = getLayoutParams();
		lp.width = width;
		lp.height = height;
		setPadding(0, 0, 0, 0);
		setLayoutParams(lp);
		setEnabled(false);
		count = TIME_SECONDS;
		hand.sendEmptyMessage(MSG_UPDATE_UI);
	}
	
	public void enableButton(){
		hand.removeMessages(MSG_UPDATE_UI);
		setEnabled(true);
		setText(text);
	}
	
	public TimingButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public TimingButton(Context context) {
		super(context);
		init(context);
	}


	public TimingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	
	private void init(Context con){
		text = getText().toString();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		hand.removeMessages(MSG_UPDATE_UI);
		super.onDetachedFromWindow();
	}
}

