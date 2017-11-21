package com.tg.dippermerchant.view.dialog;

import java.util.Calendar;


import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

public class DateSelectorDialog {
	private AlertDialog dialog;
	private Activity mActivity;
	private LayoutInflater inflater;
	private TextView tvTitle,tvSumbit;
	private DatePicker datePicker;
	public DateSelectorDialog(Activity activity){
		mActivity = activity;
		inflater = activity.getLayoutInflater();
	}
	public void show(String title,Calendar currentTime,Calendar maxTime,OnDateChangedListener l){
		if(dialog == null){
			dialog = new AlertDialog.Builder(mActivity).create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			Window window = dialog.getWindow();
			View content = inflater.inflate(R.layout.date_selector, null);
			tvTitle = (TextView)content.findViewById(R.id.tv_title);
			tvSumbit = (TextView)content.findViewById(R.id.tv_sumbit);
			datePicker = (DatePicker)content.findViewById(R.id.datePicker);
			window.setContentView(content);
			WindowManager.LayoutParams p = window.getAttributes();
			DisplayMetrics metrics = Tools.getDisplayMetrics(mActivity);
			p.width = metrics.widthPixels;
			p.gravity = Gravity.BOTTOM;
			window.setAttributes(p);
		}
		tvTitle.setText(title);
		tvSumbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		//datePicker.setMinDate(c.getTimeInMillis());
		if(maxTime != null){
			if(maxTime.before(c)){
				maxTime.add(Calendar.SECOND, 1);
			}
			datePicker.setMaxDate(maxTime.getTimeInMillis());
		}
		if(currentTime.before(c)){
			currentTime.add(Calendar.MILLISECOND, 1);
		}
		datePicker.init(currentTime.get(Calendar.YEAR), 
				currentTime.get(Calendar.MONTH), 
				currentTime.get(Calendar.DAY_OF_MONTH),
				l);
		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		dialog.show();
	} 
}
