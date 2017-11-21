package com.tg.dippermerchant.inter;

import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.view.spinnerwheel.WheelVerticalView;

import android.os.Message;


public interface NetworkRequestListener {
	public void onRequest(MessageHandler msgHand);
	public void onSuccess(WheelVerticalView wheelView, Message msg, String response);
	public void onFail(Message msg, String message);
}
