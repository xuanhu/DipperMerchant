package com.tg.dippermerchant.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.AddCommoditysActivity;
import com.tg.dippermerchant.CommodityManageMentActivity;
import com.tg.dippermerchant.ConsumerCodeManageMentActivity;
import com.tg.dippermerchant.ConsumerCodeValidationActivity;
import com.tg.dippermerchant.MainActivity;
import com.tg.dippermerchant.MyBrowserActivity;
import com.tg.dippermerchant.NoticeActivity;
import com.tg.dippermerchant.OrderManageMentActivity;
import com.tg.dippermerchant.ProceedsActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.UserInfoActivity;
import com.tg.dippermerchant.WithdrawalActivity;
import com.tg.dippermerchant.adapter.AdvPagerAdapter;
import com.tg.dippermerchant.adapter.MoreGridAdapter;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.AdvInfo;
import com.tg.dippermerchant.info.HomeInfo;
import com.tg.dippermerchant.info.NoticeInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.util.statusbar.StatusBarUtil;
import com.tg.dippermerchant.view.AdvertisementPager;
import com.tg.dippermerchant.view.MyGridView;
import com.tg.dippermerchant.view.ScrollBanner;
import com.tg.dippermerchant.view.ScrollBanner.ScrollBannerOnItemClickListener;
import com.tg.dippermerchant.view.ScrollBanner.ScrollBannerRequestListener;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 电商首页（new）
 *
 * @author Administrator
 *
 */

public class FragmentHome extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private View mView;
    private MainActivity mActivity;
    private MyGridView gridview;
    private MoreGridAdapter adapter1;
    private ArrayList<AdvInfo> listAdv = new ArrayList<AdvInfo>();
    private ArrayList<NoticeInfo> list = new ArrayList<NoticeInfo>();
    private List<String> listTitle = new ArrayList<String>();
    private AdvPagerAdapter adapter;
    private AdvertisementPager viewPager;
    private ScrollBanner scrollBanner;
    private RelativeLayout rlShopName;//头部商家名称
    private RelativeLayout rlIncome;//本月收入
    private RelativeLayout rlNewList;//今日新单
    private RelativeLayout rlWaitDelivery;//待发货
    private RelativeLayout rlCollection;//收款
    private RelativeLayout rlCheckCode;//验劵
    private RelativeLayout rlOrder;//订单
    private RelativeLayout rlCustomer;//客服
    private TextView tvIncome;//本月收入
    private TextView tvNewList;//今日新单
    private TextView tvWaitDelivery;//待发货
    private TextView tvShopName;//商家名称
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home,container, false);
        initView(mView);
        return mView;
    }

    /**
     * 初始化控件
     * @param mView
     */
    private void initView(View mView) {
        RelativeLayout rl_head_bg = (RelativeLayout) mView.findViewById(R.id.rl_head_bg);
        rl_head_bg.setPadding(0, StatusBarUtil.getStatusBarHeight(getContext()),0,0);
        gridview = (MyGridView) mView.findViewById(R.id.gridview);
        viewPager = (AdvertisementPager) mView.findViewById(R.id.adverPager);
        int height = getResources().getDisplayMetrics().widthPixels / 3;
        ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
        lp.height = height;
        viewPager.setLayoutParams(lp);
        AdvInfo info;
        info = new AdvInfo();
        info.advResId = R.color.default_bg;
        listAdv.add(info);
        adapter = new AdvPagerAdapter(listAdv, mActivity, true);
        viewPager.setAdapter(adapter);
        rlShopName = (RelativeLayout) mView.findViewById(R.id.rl_shopName);
        rlIncome = (RelativeLayout) mView.findViewById(R.id.rl_income);
        rlNewList = (RelativeLayout) mView.findViewById(R.id.rl_new_list);
        rlWaitDelivery = (RelativeLayout) mView.findViewById(R.id.rl_wait_delivery);
        rlCollection = (RelativeLayout) mView.findViewById(R.id.rl_collection);
        rlCheckCode = (RelativeLayout) mView.findViewById(R.id.rl_check_code);
        rlOrder = (RelativeLayout) mView.findViewById(R.id.rl_order);
        rlCustomer = (RelativeLayout) mView.findViewById(R.id.rl_customer);
        tvIncome = (TextView) mView.findViewById(R.id.tv_income);
        tvNewList = (TextView) mView.findViewById(R.id.tv_new_list);
        tvWaitDelivery = (TextView) mView.findViewById(R.id.tv_wait_delivery);
        tvShopName = (TextView) mView.findViewById(R.id.tv_shop_name);
        if(StringUtils.isNotEmpty(ShoppingInfo.name)){
            tvShopName.setText(ShoppingInfo.name);
        }
        rlShopName.setOnClickListener(this);
        rlIncome.setOnClickListener(this);
        rlNewList.setOnClickListener(this);
        rlWaitDelivery.setOnClickListener(this);
        rlCollection.setOnClickListener(this);
        rlCheckCode.setOnClickListener(this);
        rlOrder.setOnClickListener(this);
        rlCustomer.setOnClickListener(this);
        gridview.setOnItemClickListener(this);
        gridview.setAdapter(adapter1);
        initHomeNewsView(mView);
        requestData();
    }
    //读取数据
    public void requestData() {
        scrollBanner.loaddingData();
    }
    private void initHomeNewsView(View mView) {
        /**
         * 公告
         */
        scrollBanner =(ScrollBanner) mView.findViewById(R.id.tv_notification);//获取通知消息
        scrollBanner.setRightTextColor(getResources().getColor(R.color.text_color1));
        scrollBanner.setRightTextSize(12);
        scrollBanner.setScrollBannerOnItemClickListener(new ScrollBannerOnItemClickListener() {

            @Override
            public void onItem2Click(int postion) {
                if(list.size() > 0){
                    intent = new Intent(mActivity,MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_TITLE,list.get(postion).title);
                    intent.putExtra(MyBrowserActivity.KEY_HTML_TEXT,list.get(postion).bewrite);
                    startActivity(intent);
                }

            }

            @Override
            public void onItem1Click(int postion) {
                if(list.size() > 0){
                    intent = new Intent(mActivity,MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_TITLE,list.get(postion).title);
                    intent.putExtra(MyBrowserActivity.KEY_HTML_TEXT,list.get(postion).bewrite);
                    startActivity(intent);
                }

            }
        });
        scrollBanner.setScrollBannerRequestListener(new ScrollBannerRequestListener() {
            @Override
            public void onSuccess(ScrollBanner scrollbanner, Message msg, String response) {
                JSONArray content  = HttpTools.getContentJsonArray(response);
                if (content != null) {
                    ResponseData data = HttpTools.getResponseContent(content);
                    if (data.length > 0) {
                        NoticeInfo item;
                        for (int i = 0; i < data.length; i++) {
                            item = new NoticeInfo();
                            item.id = data.getInt(i, "id");
                            item.synopsis = data.getString(i, "synopsis");
                            item.bewrite = data.getString(i,"bewrite");
                            item.title = data.getString(i, "title");
                            list.add(item);
                        }
                    }
                    if(list.size() > 0 ){
                        for (int i = 0; i < list.size(); i++) {
                            listTitle.add(list.get(i).title);
                        }
                    }
                    scrollBanner.setList(listTitle);
                    scrollBanner.startScroll();
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("scope", 3);
                HttpTools.httpGet(Contants.URl.URl_3011, "/notice/getnoticeByscope",config, params);
            }
        });
    }
    /**
     * 轮播图
     * @param list
     */
    public void setAdvList(ArrayList<AdvInfo> list) {
        listAdv.clear();
        if (list != null && list.size() > 0) {
            listAdv.addAll(list);
        } else {
            AdvInfo info;
            info = new AdvInfo();
            info.advResId = R.color.default_bg;
            listAdv.add(info);
        }
        viewPager.notifyDataSetChanged();
    }

    /**
     * 首页信息更新
     * @param list
     */
    public void setHomeList(ArrayList<HomeInfo> list) {
        DecimalFormat df = new DecimalFormat("0.00");
        HomeInfo info = list.get(0);
        if (list != null && list.size() > 0) {
            tvIncome.setText(df.format(info.money));
            tvNewList.setText(info.orders+"");
            tvWaitDelivery.setText(info.shipments+"");
        } else {
            tvIncome.setText(df.format(info.money));
            tvNewList.setText(df.format(info.orders));
            tvWaitDelivery.setText(df.format(info.shipments));
        }
    }
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = (MainActivity)activity;
        adapter1 = new MoreGridAdapter(mActivity);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden == false){
            /**
             * 刷新首页数据
             */
            MainActivity mainactivity = (MainActivity) getActivity();
            mainactivity.freshHomeInfo();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0://商品管理
                startActivity(new Intent(mActivity, CommodityManageMentActivity.class));
                break;
            case 1://劵码管理 --> //营运统计
                //startActivity(new Intent(mActivity, ConsumerCodeManageMentActivity.class));
                Intent intent = new Intent(mActivity,MyBrowserActivity.class);
                intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+ Tools.getEncryptURL(DES.KEY_URl,"&type=4"));
                startActivity(intent);
                break;
            case 2://提现申请  -->  提现申请
              //  startActivity(new Intent(mActivity, WithdrawalActivity.class));
                startActivity(new Intent(mActivity, WithdrawalActivity.class));
                break;
            case 3://商家公告  -->商家公告
               // startActivity(new Intent(mActivity, NoticeActivity.class));
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;
            case 4://服务管理  --> 咨询互动
                break;
            case 5://营运统计  --> 无
                break;
            case 6://咨询互动  --> 无
                break;
            case 7://更多  -->  无
                break;

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_shopName://商家名称
                startActivity(new Intent(mActivity, UserInfoActivity.class));
                break;
            case R.id.rl_income://本月收入
                Intent intent = new Intent(mActivity,MyBrowserActivity.class);
                //Log.d("printLog","url-"+Contants.URl.H5_URl_Order+ Tools.getEncryptURL(DES.KEY_URl,"&type=4"));
                intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+ Tools.getEncryptURL(DES.KEY_URl,"&type=4"));
                startActivity(intent);
                break;
            case R.id.rl_new_list://今日新单
                startActivity(new Intent(mActivity, OrderManageMentActivity.class));
                break;
            case R.id.rl_wait_delivery://待发货
                startActivity(new Intent(mActivity, OrderManageMentActivity.class));
                break;
            case R.id.rl_collection://收款
                startActivity(new Intent(mActivity, ProceedsActivity.class));
                break;
            case R.id.rl_check_code://验劵 --> 添加商品
               // startActivity(new Intent(mActivity, ConsumerCodeValidationActivity.class));
                startActivity(new Intent(mActivity, AddCommoditysActivity.class));
                break;
            case R.id.rl_order://订单
                startActivity(new Intent(mActivity, OrderManageMentActivity.class));
                break;
            case R.id.rl_customer://客服
                break;
        }
    }
}
