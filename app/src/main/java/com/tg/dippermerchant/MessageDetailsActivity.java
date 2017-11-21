package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.MessageInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.view.dialog.ToastFactory;

public class MessageDetailsActivity extends BaseActivity {
    public final static String MESSAGE_INFO ="message_info";
    private MessageInfo info ;
    private TextView tvName;
    private TextView tvContent;
    private TextView tvTime;
    private EditText editOpinion;
    private String opinion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null ){
            info = (MessageInfo) intent.getSerializableExtra(MESSAGE_INFO);
        }

        initView();
    }
    @Override
    protected boolean handClickEvent(View v) {
        switch(v.getId()){
            case R.id.right_layout://提交
                opinion = editOpinion.getText().toString();
                if(opinion.length() == 0){
                    ToastFactory.showToast(this, "请填写回复内容");
                    return false;
                }
                if(opinion.length() <5){
                    ToastFactory.showToast(this, "你输入的内容少于5个字，请重新输入");
                    return false;
                }
                if(opinion.length() > 200){
                    ToastFactory.showToast(this, "回复内容不能大于200字");
                    return false;
                }
                submit();
                break;
        }

        return super.handClickEvent(v);
    }
    /**
     * 提交
     */
    private void submit() {
        RequestConfig config = new RequestConfig(this, HttpTools.SET_CONSULT_INFO,"提交信息");
        RequestParams params = new RequestParams();
        params.put("id", info.id);
        params.put("content",editOpinion.getText().toString());
        HttpTools.httpPut(Contants.URl.URl_3013,"/consulting" ,config, params);
    }
    private void initView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTime = (TextView) findViewById(R.id.tv_time);
        editOpinion = (EditText) findViewById(R.id.edit_opinion);
        if(StringUtils.isNotEmpty(info.nickname)){
            tvName.setText(info.nickname);
        }
        if(StringUtils.isNotEmpty(info.usernotes)){
            tvContent.setText(info.usernotes);
        }
        if(StringUtils.isNotEmpty(info.addtime)){
            tvTime.setText(info.addtime);
        }
    }


    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(code == 0){
            ToastFactory.showToast(MessageDetailsActivity.this,"回复成功");
            finish();
        }else{
            ToastFactory.showToast(MessageDetailsActivity.this,message);
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_message_details, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("提交");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "咨询互动";
    }
}
