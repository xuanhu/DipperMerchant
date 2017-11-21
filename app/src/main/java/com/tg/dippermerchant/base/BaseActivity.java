package com.tg.dippermerchant.base;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

import com.tg.dippermerchant.application.ManagementApplication;
import com.tg.dippermerchant.inter.ResultCallBack;
import com.tg.dippermerchant.inter.SingleClickListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.MessageHandler.ResponseListener;
import com.tg.dippermerchant.view.ActivityHeaderView;
import com.tg.dippermerchant.view.CameraView;
import com.tg.dippermerchant.view.GifImageView;
import com.tg.dippermerchant.view.CameraView.STATE;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BaseActivity extends Activity implements ResponseListener {

	public interface ActivityResultCallBack {
		void onResult(int requestCode, int resultCode, Intent data);
	}

	public interface ActivityBackListener {
		void onBackPressed(Activity activity);
	}

	private ActivityBackListener backListener;
	protected ActivityHeaderView headView;
	protected View contentLayout;
	private GifImageView gifView;
	private TextView tvLoaddingHint;
	private LinearLayout refreshLayout;
	private ArrayList<ActivityResultCallBack> list = new ArrayList<ActivityResultCallBack>();
	protected Handler mHand;
	private MessageHandler msgHand;
	private ResultCallBack callBack;
	private int requestCode;
	private boolean isLoadding = false;
	protected SingleClickListener singleListener = new SingleClickListener() {
		@Override
		public void onSingleClick(View v) {
			if (handClickEvent(v)) {
				shieldClickEvent();
			}
		}

	};

	public void setActivityBackListener(ActivityBackListener l) {
		backListener = l;
	}

	/**
	 * 
	 * @param v
	 * @return true : 短时间内限制点击， false ： 无限制
	 */
	protected boolean handClickEvent(View v) {
		return false;
	}

	public Handler getHandler() {
		return mHand;
	}

	public void addResultCallBack(ActivityResultCallBack r) {
		if (!list.contains(r)) {
			list.add(r);
		}
	}

	public BaseActivity() {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (callBack != null) {
			callBack.onResult(requestCode, resultCode, data);
		}
	}

	public int onLoadding() {
		return -1;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ManagementApplication.addActivity(this);
		msgHand = new MessageHandler(this);
		msgHand.setResponseListener(this);
		mHand = msgHand.getHandler();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_base);

		//透明导航栏
	//	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		headView = (ActivityHeaderView) findViewById(R.id.title);
		refreshLayout = (LinearLayout) findViewById(R.id.refresh_layout);
		gifView = (GifImageView) findViewById(R.id.gifView);
		tvLoaddingHint = (TextView) findViewById(R.id.base_loadding_text);
		FrameLayout baseContentLayout = (FrameLayout) findViewById(R.id.base_content_layout);
		contentLayout = getContentView();
		if (contentLayout != null) {
			Drawable backDrawable = contentLayout.getBackground();
			if (backDrawable == null) {
				contentLayout.setBackgroundColor(getResources().getColor(
						R.color.base_color));
			}
			FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			baseContentLayout.addView(contentLayout, p);
		}
		String title = getHeadTitle();
		if (title == null) {
			title = "";
		}
		headView.setTitle(title);
		parserIntent(getIntent());
		if ((requestCode = onLoadding()) > 0) {
			isLoadding = true;
			refreshLayout.setVisibility(View.VISIBLE);
			if (contentLayout != null) {
				contentLayout.setVisibility(View.GONE);
			}
		}
	}

	public void parserIntent(Intent intent) {

	}

	public void onReload(View v) {
		if (isLoadding) {
			return;
		}
		isLoadding = true;
		onLoadding();
		refreshLayout.setVisibility(View.VISIBLE);
		if (contentLayout != null) {
			contentLayout.setVisibility(View.GONE);
		}
	}

	public abstract View getContentView();

	public abstract String getHeadTitle();

	public void setBaseContentBackgroundColor(int color) {
		if (contentLayout != null) {
			contentLayout.setBackgroundColor(color);
		}
	}

	public void setBaseContentBackgroundResource(int resid) {
		if (contentLayout != null) {
			contentLayout.setBackgroundResource(resid);
		}
	}

	@Override
	public void onBackPressed() {
		if (backListener != null) {
			backListener.onBackPressed(this);
		} else {
			super.onBackPressed();
		}
	}

	public void onRequestStart(Message msg, String hintString) {
		if (hintString != null) {
			if (msg.arg1 == requestCode) {
				gifView.play(true);
				tvLoaddingHint.setText("正在努力加载中...");
			} else {
				DialogFactory.getInstance().showTransitionDialog(this,
						hintString, msg.obj, msg.arg1);
			}
		}
	}

	public void onSuccess(Message msg, String jsonString, String hintString) {
		Bundle data = msg.getData();
		if (data != null) {
			if (!data.getBoolean(HttpTools.KEY_SILENT_REQUEST, true)
					&& msg.arg1 != HttpTools.POST_IMAG) {
				DialogFactory.getInstance().hideTransitionDialog();
			}
		}
		if (msg.arg1 == requestCode) {
			isLoadding = false;
			gifView.play(false);
			tvLoaddingHint.setText("点击重新加载");
			refreshLayout.setVisibility(View.GONE);
			if (contentLayout != null) {
				contentLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	public void onFail(Message msg, String hintString) {
		Bundle data = msg.getData();
		if (data != null) {
			if (!data.getBoolean(HttpTools.KEY_SILENT_REQUEST, true)) {
				DialogFactory.getInstance().hideTransitionDialog();
				if (!TextUtils.isEmpty(hintString)
						&& !"null".equals(hintString)) {
					ToastFactory.showToast(this, hintString);
				}
			}
		}
		if (msg.arg1 == requestCode) {
			isLoadding = false;
			gifView.play(false);
			tvLoaddingHint.setText("点击重新加载");
			refreshLayout.setVisibility(View.VISIBLE);
			if (contentLayout != null) {
				contentLayout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ManagementApplication.removeActivity(this);
		HttpTools.cancelRequest(this.toString());
		if (mHand != null) {
			mHand.removeCallbacksAndMessages(null);
		}
	}

	public void setResultCallBack(ResultCallBack call) {
		callBack = call;
	}

	public void onCancel(Object tag, int requestCode) {
	}

	public void returnData(CameraView cv, STATE state, int groupPosition,
			int childPosition, int position, Bitmap bitmap, String path) {
	}
}
