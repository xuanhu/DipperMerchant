package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.OrderInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.view.dialog.ToastFactory;

/**
 * 订单发货
 */
public class ShipmentActivity extends BaseActivity {
    public static final  String ORDERINFO ="orderinfo";
    private OrderInfo info ;
    private EditText etCompany,etNumber,etRemark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            info = (OrderInfo) intent.getSerializableExtra(ORDERINFO);
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        if(v.getId() == R.id.right_layout){
            String company = etCompany.getText().toString();
            if(StringUtils.isEmpty(company)){
                ToastFactory.showToast(ShipmentActivity.this,"快递公司不能为空");
                return  false;
            }
            String number = etNumber.getText().toString();
            if(StringUtils.isEmpty(number)){
                ToastFactory.showToast(ShipmentActivity.this,"快递单号不能为空");
                return  false;
            }
            String remark = etRemark.getText().toString();
            RequestConfig config = new RequestConfig(ShipmentActivity.this,HttpTools.PUT_SHIPMENTS);
            RequestParams params = new RequestParams();
            params.put("orderId",info.orderId);
            params.put("logisticscompany",company);
            params.put("ExpressNumber",number);
            params.put("operator", ShoppingInfo.linkman);
            params.put("operatorId",ShoppingInfo.id);
            HttpTools.httpPut(Contants.URl.URl_3026,"/orders/shipments" ,config, params);
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        etCompany = (EditText) findViewById(R.id.et_company);
        etNumber = (EditText) findViewById(R.id.et_number);
        etRemark = (EditText) findViewById(R.id.et_remark);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(msg.arg1 == HttpTools.PUT_SHIPMENTS){//发货
            if(code == 0 ){
                ToastFactory.showToast(ShipmentActivity.this, "发货成功");
            }else {
                ToastFactory.showToast(ShipmentActivity.this, message);
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_shipment,null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("提交");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "立即发货";
    }
}
