package com.tg.dippermerchant;


import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.tg.dippermerchant.MainActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
/**
 * 收款二维码
 * @author Administrator
 *
 */
public class ProceedsActivity extends BaseActivity {
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageView = (ImageView)findViewById(R.id.image);
		ViewGroup.LayoutParams params = imageView.getLayoutParams();
		if(MainActivity.CODE_IMAGE_WIDTH == 0){
			DisplayMetrics dm = getResources().getDisplayMetrics();
			MainActivity.CODE_IMAGE_WIDTH = dm.widthPixels - (int)(20*2*dm.density);
		}
		params.height = MainActivity.CODE_IMAGE_WIDTH;
		imageView.setLayoutParams(params);
		getScanningCodeImage();
	}
	
	public void getScanningCodeImage(){
		RequestConfig config = new RequestConfig(this,HttpTools.GET_SCANNING_IMAGE_URL,"获取二维码");
		RequestParams params = new RequestParams();
		params.put("id", ShoppingInfo.id);
		params.put("type", 1);
		HttpTools.httpGet(Contants.URl.URl_3013,"/merchant/getmerchantQRcodeByid", config, params);
	}
	
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(code == 0){
			JSONObject jsonObject =  HttpTools.getContentJSONObject(jsonString);
			String qrCodeUrl;
			try {
				qrCodeUrl = jsonObject.getString("data");
				if (TextUtils.isEmpty(qrCodeUrl)) {
					ToastFactory.showToast(this, "支付二维码不存在");
					DialogFactory.getInstance().hideTransitionDialog();
					return;
				}
				VolleyUtils.getImageLoader(this).get(qrCodeUrl,
						new ImageLoader.ImageListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub
								String errorStr = HttpTools.getExceptionMessage(arg0);
								ToastFactory.showToast(ProceedsActivity.this, errorStr);
								DialogFactory.getInstance().hideTransitionDialog();
							}

							@Override
							public void onResponse(ImageContainer arg0, boolean arg1) {
								// TODO Auto-generated method stub
								if (arg0.getBitmap() != null) {
									imageView.setImageBitmap(arg0.getBitmap());
								}
								DialogFactory.getInstance().hideTransitionDialog();
							}
						}, MainActivity.CODE_IMAGE_WIDTH, MainActivity.CODE_IMAGE_WIDTH);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			ToastFactory.showToast(ProceedsActivity.this,message);
		}
		}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sendBroadcast(new Intent(MainActivity.ACTION_GET_USERINFO));
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_proceeds,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "收款二维码";
	}


}
