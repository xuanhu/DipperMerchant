package com.tg.dippermerchant.view;


import com.tg.dippermerchant.R;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 支付选择控件
 * 
 * @author Administrator
 * 
 */
public class ChoiceView extends FrameLayout implements Checkable {

	private RadioButton mRadioButton;

	public ChoiceView(Context context) {
		super(context);
		View.inflate(context, R.layout.bank_listview, this);
		mRadioButton = (RadioButton) findViewById(R.id.checkedView);
	}

	@Override
	public void setChecked(boolean checked) {
		mRadioButton.setChecked(checked);
	}

	@Override
	public boolean isChecked() {
		return mRadioButton.isChecked();
	}

	@Override
	public void toggle() {
		mRadioButton.toggle();
	}
}