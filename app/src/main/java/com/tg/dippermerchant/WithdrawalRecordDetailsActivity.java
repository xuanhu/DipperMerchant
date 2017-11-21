package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.info.WithdrawalRecordInfo;
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
 * 提现详情
 */
public class WithdrawalRecordDetailsActivity extends BaseActivity implements MessageArrowView.ItemClickListener {
    public  static final  String ID ="id";
    private int id;
    private MessageArrowView messageView1;
    private MessageArrowView messageView2;
    private ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
    private ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
    private RelativeLayout rlNote;
    private TextView tvState;
    private TextView tvNote;
    private int state ;
    private String serialNo= "";//流水号
    private String bankname= "";
    private String eName= "";
    private String payaccount= "";
    private float money ;
    private String addTime= "";
    private String note= "";//备注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            id = intent.getIntExtra(ID,-1);
        }
        messageView1 = (MessageArrowView) findViewById(R.id.messageView1);
        messageView2 = (MessageArrowView) findViewById(R.id.messageView2);
        messageView1.setItemClickListener(this);
        messageView2.setItemClickListener(this);
        messageView1.setEditable(true);
        messageView2.setEditable(true);
        RequestConfig config = new RequestConfig(WithdrawalRecordDetailsActivity.this, HttpTools.GET_MERCHANTWITHDRAWALS_INFO,"获取详情");
        RequestParams params = new RequestParams();
        params.put("id",id);
        HttpTools.httpGet(Contants.URl.URl_3013,"/merchantWithdrawals/"+id, config, params);
        initView();
    }

    private void initView() {
        rlNote = (RelativeLayout) findViewById(R.id.rl_note);
        tvState = (TextView) findViewById(R.id.tv_state);
        tvNote = (TextView) findViewById(R.id.tv_note);
        list1.clear();
        ViewConfig config = new ViewConfig("申请流水号",serialNo,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        config = new ViewConfig("开户银行",bankname,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        config = new ViewConfig("开户名称",eName,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        config = new ViewConfig("银行账户",payaccount,false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        config = new ViewConfig("提现金额",String.valueOf(money),false);
        config.rightEditable = false;
        config.enable = false;
        list1.add(config);
        messageView1.setData(list1);

        list2.clear();
        config = new ViewConfig("申请时间",addTime,false);
        config.rightEditable = false;
        config.enable = false;
        list2.add(config);
        messageView2.setData(list2);
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
                    state = data.getInt(0, "state");
                    money = data.getFloat(0, "money");
                    addTime = data.getString(0, "addTime");
                    bankname = data.getString(0, "bankname");
                    note = data.getString(0, "note");
                    payaccount = data.getString(0, "payaccount");
                    eName = data.getString(0, "eName");
                    serialNo = data.getString(0, "serialNo");
                    updateView();
                }
            }

        }else{
            ToastFactory.showToast(WithdrawalRecordDetailsActivity.this,message);
        }
    }
    private void updateView(){
        if(state == 0){//0待处理1成功2拒绝
            tvState.setText("审核中");
            tvState.setTextColor(getResources().getColor(R.color.tixian_state));
        }else if(state == 1){
            tvState.setText("提现完成");
            tvState.setTextColor(getResources().getColor(R.color.text_color1));
        }else{
            tvState.setText("不通过");
            tvState.setTextColor(getResources().getColor(R.color.red_exit));
        }
        if(StringUtils.isNotEmpty(note)){
            rlNote.setVisibility(View.VISIBLE);
            tvNote.setText(note);
        }else{
            rlNote.setVisibility(View.GONE);
        }
        list1.get(0).rightText = serialNo;
        list1.get(1).rightText = bankname;
        list1.get(2).rightText = eName;
        list1.get(3).rightText = payaccount;
        list1.get(4).rightText = "￥"+String.valueOf(money);

        list2.get(0).rightText = addTime;
        messageView1.freshAll();
        messageView2.freshAll();
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_withdrawal_record_details,null);
    }

    @Override
    public String getHeadTitle() {
        return "提现详情";
    }

    @Override
    public void onItemClick(MessageArrowView mv, View v, int position) {

    }
}
