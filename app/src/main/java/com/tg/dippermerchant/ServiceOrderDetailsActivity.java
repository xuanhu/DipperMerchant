package com.tg.dippermerchant;

import android.os.Bundle;
import android.view.View;

import com.tg.dippermerchant.base.BaseActivity;

/**
 * 服务订单详情页
 */
public class ServiceOrderDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_service_order_details,null);
    }

    @Override
    public String getHeadTitle() {
        return "订单详情";
    }
}
