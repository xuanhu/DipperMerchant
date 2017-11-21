package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * 推送消息详情（提现与收款）
 */
public class AccountNewsActivity extends BaseActivity {
    public  static final  String ORIGINALID ="originalId";
    public  static final  String STATE ="state";
    private String originalId;
    private String state;
    private MessageArrowView messageView1;
    private ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
    private String addTime ,remark;
    private TextView tvMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            originalId = intent.getStringExtra(ORIGINALID);
            state = intent.getStringExtra(STATE);
        }
        if(StringUtils.isNotEmpty(state)){
            headView.setTitle(state);
        }
        messageView1 = (MessageArrowView) findViewById(R.id.messageView1);
        messageView1.setEditable(true);
        RequestConfig config = new RequestConfig(AccountNewsActivity.this, HttpTools.GET_MERCHANTWITHDRAWALS_INFO,"获取详情");
        RequestParams params = new RequestParams();
        params.put("id",originalId);
        HttpTools.httpGet(Contants.URl.URl_3013,"/administratorBankLog/"+originalId, config, params);
        initView();
    }

    private void initView() {
        tvMoney = (TextView) findViewById(R.id.tv_money);
        if(StringUtils.isNotEmpty(originalId)){
            tvMoney.setText("￥"+originalId);
        }
        list1.clear();
        ViewConfig config = new ViewConfig("备注",remark,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        config = new ViewConfig("时间",addTime,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        messageView1.setData(list1);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(code == 0){
            JSONArray content = HttpTools.getContentJsonArray(jsonString);
            if(content != null ){
                ResponseData data = HttpTools.getResponseContent(content);
                if (data.length > 0) {
                    addTime = data.getString(0, "addTime");
                    remark = data.getString(0, "remark");
                    updateView();
                }
            }

        }else{
            ToastFactory.showToast(AccountNewsActivity.this,message);
        }
    }

    /**
     * 刷新控件
     */
    private void updateView() {
        list1.get(0).rightText = remark;
        list1.get(1).rightText = addTime;
        messageView1.freshAll();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account_news,null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }
}
