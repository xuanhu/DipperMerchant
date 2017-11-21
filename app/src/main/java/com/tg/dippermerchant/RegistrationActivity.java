package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.object.ImageParams;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.ImageViewGroup;
import com.tg.dippermerchant.view.TimingButton;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegistrationActivity extends BaseActivity {
    private ImageViewGroup imgGroup;
    private EditText editUser;
    private EditText editAddress;
    private EditText editPhone;
    private EditText editLinkman;
    private EditText editMobile;
    private EditText editCode;
    private TimingButton timingButton;
    private String User;//商家名称
    private String Address;//商家地址
    private String Phone;//商家电话
    private String Linkman;//商家联系人
    private String Mobile;//手机号
    private String code;//验证码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
         User = editUser.getText().toString();
         Address = editAddress.getText().toString();
         Phone = editPhone.getText().toString();
         Linkman = editLinkman.getText().toString();
         Mobile = editMobile.getText().toString();
         code = editCode.getText().toString();
        if (v.getId() == R.id.right_layout) {
            if(StringUtils.isEmpty(User)){
                ToastFactory.showToast(this, "请输入商家名称");
                return false;
            }
            if(StringUtils.isEmpty(Address)){
                ToastFactory.showToast(this, "请输入商家地址");
                return false;
            }
            if(StringUtils.isEmpty(Phone)){
                ToastFactory.showToast(this, "请输入商家电话");
                return false;
            }
            if(StringUtils.isEmpty(Linkman)){
                ToastFactory.showToast(this, "请输入商家联系人");
                return false;
            }
            if (!Tools.checkTelephoneNumber(Mobile)) {
                ToastFactory.showToast(this, "请输入11位正确的手机号");
                return false;
            }
            ArrayList<ImageParams> list = imgGroup.getPostImageParam();
            if(list == null || list.size() == 0){
                ToastFactory.showToast(this, "请先上传营业执照照片");
                return false;
            }else{
                HttpTools.postImages(Contants.URl.URl_3020,mHand, list);
            }
        } else {
            if (!Tools.checkTelephoneNumber(Mobile)) {
                ToastFactory.showToast(this, "请输入11位正确的手机号");
                return false;
            }
            RequestConfig config = new RequestConfig(this, HttpTools.GET_CODE);
            config.hintString = "请求验证码";
            RequestParams params = new RequestParams().put("mobile", Mobile)
                    .put("type", 1);
            HttpTools.httpGet(Contants.URl.URl_3011, "/sms/code", config, params);
        }
        return super.handClickEvent(v);
    }

    /**
     * 初始化
     */
    private void initView() {
        editUser = (EditText) findViewById(R.id.edit_user);
        editAddress = (EditText) findViewById(R.id.edit_address);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        editLinkman = (EditText) findViewById(R.id.edit_linkman);
        editMobile = (EditText) findViewById(R.id.edit_mobile);
        editCode = (EditText) findViewById(R.id.edit_code);
        imgGroup = (ImageViewGroup) findViewById(R.id.imgGroup);
        timingButton = (TimingButton) findViewById(R.id.btn_get_code);
        timingButton.setOnClickListener(singleListener);
        imgGroup.setAddable(true);

    }
    private void verifyCode(){
        RequestParams params = new RequestParams();
        params.put("mobile", Mobile);
        params.put("code", code);
        params.put("type", 1);
        RequestConfig config = new RequestConfig(this,HttpTools.SET_REGISTER);
        config.hintString = "验证验证码";
        HttpTools.httpGet(Contants.URl.URl_3012, "/user/checkcode", config,
                params);
    }
    /**
     * 提交注册
     */
    private void submit(){
        RequestConfig config = new RequestConfig(this,
                HttpTools.POST_MERCHANT);
        config.hintString = "注册";
        RequestParams params = new RequestParams();
        params.put("username",Mobile);
        params.put("realname",User);
        params.put("tel",Phone);
        params.put("Company_tel",Phone);
        params.put("Mobile",Mobile);
        params.put("linkman",Linkman);
        params.put("address",Address);
        String urls = imgGroup.getPostUrls();
        if(!TextUtils.isEmpty(urls)){
            params.put("businessimg", urls);
        }
        HttpTools.httpPost(Contants.URl.URl_3013,"/administrator/merchant", config, params);
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
        int code = HttpTools.getCode(jsonString);
        if(msg.arg1 == HttpTools.POST_IMAG){
            Bundle bundle = msg.getData();
            if(bundle != null){
                ImageParams param = (ImageParams)bundle.getParcelable(HttpTools.KEY_IMAGE_PARAMS);
                int position = param.position;
                imgGroup.getImageParams().get(position).url = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
                boolean isLast = bundle.getBoolean(HttpTools.KEY_IS_LAST, false);
                if(isLast){
                    verifyCode();
                }
            }
        }else if (msg.arg1 == HttpTools.GET_CODE) {
            if (code == 0) {
                ToastFactory.showToast(this, "验证码获取成功");
                timingButton.startTiming();
            }else {
                ToastFactory.showToast(this, "验证码获取失败");
            }
            String sms = null;
            JSONObject dataCount = HttpTools.getContentJSONObject(jsonString);
            try {
                if(dataCount != null){
                    sms = dataCount.getString("smscode");
                    Log.d("TAG","sms="+sms);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if(msg.arg1 == HttpTools.SET_REGISTER){
            if (code == 0) {
               submit();
            }else {
                ToastFactory.showToast(this, "你输入的验证码有误，请重新输入");
            }
        }else{
           if(code == 0){
                ToastFactory.showToast(this, "你的资料已提交审核，请耐心等待！");
                finish();
            }else {
                ToastFactory.showToast(this, "提交审核失败，请稍后再试");
            }

        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_registration,null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("注册");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "商家注册";
    }
}
