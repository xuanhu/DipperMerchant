package com.tg.dippermerchant.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 订单弹出框ViewPager
 */
public class ShowViewPager extends ViewPager{
	private boolean enable = true;
	private int current;
	private int height = 0;
	public ShowViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ShowViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setEnableScroll(boolean enable){
		this.enable = enable;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if(enable){
			return super.onTouchEvent(arg0);
		}else{
			return false;
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if(enable){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getChildCount() > current) {
			View child = getChildAt(current);
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			height = h;
		}
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void resetHeight(int current) {
		this.current = current;
		if (getChildCount() > current) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
			if (layoutParams == null) {
				layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
			} else {
				layoutParams.height = height;
			}
			setLayoutParams(layoutParams);
		}
	}
}
