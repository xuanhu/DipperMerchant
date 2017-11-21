package com.tg.dippermerchant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.object.ImageParams;
import com.tg.dippermerchant.object.SlideItemObj;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.CameraView;
import com.tg.dippermerchant.view.ImageViewGroup;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.CameraView.STATE;
import com.tg.dippermerchant.view.MessageArrowView.ItemClickListener;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;
import com.tg.dippermerchant.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.google.gson.Gson;
import com.tg.dippermerchant.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 添加商品页面
 * @author Administrator
 *
 */
public class AddCommoditysActivity extends BaseActivity implements ItemClickListener{
    private ImageViewGroup imgGroup;
	private String headImgPath;
	private EditText etName, etOriginalPrice,etSellingPrice,etAmount,etDescribe;
	private ArrayList<SlideItemObj> genderList;
    private String name;
    private String originalPrice;
    private String sellingprice;
    private String amount;
    private String describe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected boolean handClickEvent(View v) {
		if(v.getId() == R.id.right_layout){
			submitUserInfo();
		}
		return super.handClickEvent(v);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		etName = (EditText) findViewById(R.id.et_name);
		etOriginalPrice = (EditText) findViewById(R.id.et_originalprice);
		etSellingPrice = (EditText) findViewById(R.id.et_sellingprice);
		etAmount = (EditText) findViewById(R.id.et_amount);
		etDescribe = (EditText) findViewById(R.id.et_describe);
        imgGroup = (ImageViewGroup) findViewById(R.id.imgGroup);
        imgGroup.setAddable(true);
		imgGroup.SetMaxCount(1);
		etOriginalPrice.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		etSellingPrice.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		etOriginalPrice.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 if (s.toString().contains(".")) {  
	                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {  
	                        s = s.toString().subSequence(0,  
	                                s.toString().indexOf(".") + 3);  
	                        etOriginalPrice.setText(s);  
	                        etOriginalPrice.setSelection(s.length());  
	                    }  
	                }  
	                if (s.toString().trim().substring(0).equals(".")) {  
	                    s = "0" + s;  
	                    etOriginalPrice.setText(s);  
	                    etOriginalPrice.setSelection(2);  
	                }  
	   
	                if (s.toString().startsWith("0")  
	                        && s.toString().trim().length() > 1) {  
	                    if (!s.toString().substring(1, 2).equals(".")) {  
	                    	etOriginalPrice.setText(s.subSequence(0, 1));  
	                    	etOriginalPrice.setSelection(1);  
	                        return;  
	                    }  
	                }  
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		etSellingPrice.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(".")) {  
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {  
						s = s.toString().subSequence(0,  
								s.toString().indexOf(".") + 3);  
						etSellingPrice.setText(s);  
						etSellingPrice.setSelection(s.length());  
					}  
				}  
				if (s.toString().trim().substring(0).equals(".")) {  
					s = "0" + s;  
					etSellingPrice.setText(s);  
					etSellingPrice.setSelection(2);  
				}  
				
				if (s.toString().startsWith("0")  
						&& s.toString().trim().length() > 1) {  
					if (!s.toString().substring(1, 2).equals(".")) {  
						etSellingPrice.setText(s.subSequence(0, 1));  
						etSellingPrice.setSelection(1);  
						return;  
					}  
				}  
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
    @Override
    public void onRequestStart(Message msg, String hintString) {
        // TODO Auto-generated method stub
        if(msg.arg1 == HttpTools.POST_IMAG){
            ImageParams param = (ImageParams)msg.getData().getParcelable(HttpTools.KEY_IMAGE_PARAMS);
            DialogFactory.getInstance().showTransitionDialog(this,
                    "正在上传图片... "+(param.position+1)+"/"+(imgGroup.getImageParams().size()-1),
                    this.toString(), HttpTools.POST_IMAG);
        }else{
            super.onRequestStart(msg, hintString);
        }
    }
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
        if(msg.arg1 == HttpTools.POST_IMAG){
            Bundle bundle = msg.getData();
            if(bundle != null){
                ImageParams param = (ImageParams)bundle.getParcelable(HttpTools.KEY_IMAGE_PARAMS);
                int position = param.position;
                imgGroup.getImageParams().get(position).url = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
                boolean isLast = bundle.getBoolean(HttpTools.KEY_IS_LAST, false);
                if(isLast){
                    submit();
                }
            }
        }else if(msg.arg1 == HttpTools.SET_SHELVES_INFO){
			int code = HttpTools.getCode(jsonString);
			if(code == 0 ){
				ToastFactory.showToast(this,"上架成功");
				startActivity(new Intent(this, CommodityManageMentActivity.class));
				finish();
			}else{
				ToastFactory.showToast(this,"上架失败，请稍后重试。");
			}
		}
	}
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		if (headImgPath == null) {
			headImgPath = getFilesDir().getAbsolutePath() + "/"+ "head.jpg";
		}
		DialogFactory.getInstance().showPhotoSelector(this, null,headImgPath, 0, 0, 0);
	}

	/**
	 * 提交数据
	 */
	private void submitUserInfo() {
		 name = etName.getText().toString();
		if(name.length() == 0){
			ToastFactory.showToast(this, "请输入商品名");
			return;
		}
        originalPrice =etOriginalPrice.getText().toString();
		if(originalPrice.length() == 0){
			ToastFactory.showToast(this, "请输入商品供货价");
			return;
		}
		if(Float.parseFloat(originalPrice) == 0 ){
			ToastFactory.showToast(this, "商品供货价不能为零");
			return;
		}
        sellingprice =etSellingPrice.getText().toString();
		if(sellingprice.length() == 0){
			ToastFactory.showToast(this, "请输入商品建议零售价");
			return;
		}
		if(Float.parseFloat(sellingprice) == 0 ){
			ToastFactory.showToast(this, "商品建议零售价不能为零");
			return;
		}
        amount = etAmount.getText().toString();
		if(amount.length() == 0){
			ToastFactory.showToast(this, "请输入商品库存");
			return;
		}
		if(Integer.parseInt(amount) == 0 ){
			ToastFactory.showToast(this, "商品库存不能为零");
			return;
		}
        describe = etDescribe.getText().toString();
		if(describe.length() == 0){
			ToastFactory.showToast(this, "请输入商品摘要");
			return;
		}
        ArrayList<ImageParams> list = imgGroup.getPostImageParam();
        if(list == null || list.size() == 0){
            ToastFactory.showToast(this, "请先上传商品照片");
            return;
        }else{
            HttpTools.postImages(Contants.URl.URl_3020,mHand, list);
        }
        //submit();
	}
	private  void submit(){
		String url = null;
		String urls = imgGroup.getPostUrls();
		if(!TextUtils.isEmpty(urls)){
			Map<String, String> map = new HashMap<String, String>();
			map.put("imgUrl",urls);
			map.put("isMain","1");
			url = mapToJson(map);
		}
        RequestConfig config = new RequestConfig(this, HttpTools.SET_SHELVES_INFO);
        RequestParams params = new RequestParams();
        params.put("mshopId", ShoppingInfo.id);
        params.put("type", 0);
        params.put("state", 1);//0下架  1上架
        params.put("name", name);
        params.put("amount",amount);
        params.put("originalPrice",originalPrice);
        params.put("sellingprice",sellingprice);
        params.put("describe",describe);
		if(url != null){
			params.put("imgs","["+url+"]");
		}
        config.hintString = "上架商品";
        HttpTools.httpPost(Contants.URl.URl_3026, "/commodity", config,params);
    }
	 /**
     * map to json
     * 
     * @param map
     * @return
     */
    public static String mapToJson(Map<String, String> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_commoditys_selves,null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText("提交");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "添加商品";
	}


}
