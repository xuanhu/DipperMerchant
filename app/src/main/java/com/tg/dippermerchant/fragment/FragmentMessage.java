package com.tg.dippermerchant.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tg.dippermerchant.CommodityDetailsActivity;
import com.tg.dippermerchant.MainActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.adapter.CommodityMentAdapter;
import com.tg.dippermerchant.adapter.MessageAdapter;
import com.tg.dippermerchant.adapter.ViewPagerAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.MessageInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.statusbar.StatusBarUtil;
import com.tg.dippermerchant.view.MyViewPager;
import com.tg.dippermerchant.view.PullRefreshListView;

import java.util.ArrayList;

/**
 * 留言
 */

public class FragmentMessage extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private View mView;
    private MainActivity mActivity;
    private MyViewPager viewPager;
    private RadioGroup radioGroup;
    private ViewPagerAdapter pagerAdapter;
    private MessageAdapter adapter1;
    private MessageAdapter adapter2;
    private ArrayList<View> pagerList = new ArrayList<View>();
    private PullRefreshListView listView1;
    private PullRefreshListView listView2;
    private ArrayList<MessageInfo> list1 = new ArrayList<MessageInfo>();
    private ArrayList<MessageInfo> list2 = new ArrayList<MessageInfo>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message,container, false);
        initView(mView);
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
        return mView;
    }

    private void initView(View mView) {
        TextView tvHeadMessage = (TextView) mView.findViewById(R.id.tv_head_message);
        tvHeadMessage.setPadding(0, StatusBarUtil.getStatusBarHeight(getContext()),0,0);
        radioGroup = (RadioGroup)mView.findViewById(R.id.radio_group);
        viewPager = (MyViewPager)mView.findViewById(R.id.viewPager);
        //已回复
        listView1 = new PullRefreshListView(mActivity);
        listView1.setNodataImage(R.drawable.nomessage);
        listView1.setNodataText("暂无留言");
        listView1.setKeyName("all");
        listView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    MessageInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new MessageInfo();
                        item.nickname = data.getString(i,"nickname");
                        item.realname = data.getString(i,"realname");
                        item.icon = data.getString(i,"icon");
                        item.addtime = data.getString(i,"addtime");
                        item.shopname = data.getString(i,"shopname");
                        item.usernotes = data.getString(i,"usernotes");
                        item.shopnotes = data.getString(i,"shopnotes");
                        item.retime = data.getString(i,"retime");
                        item.id = data.getInt(i,"id");
                        item.uid = data.getInt(i,"uid");
                        item.shopid = data.getInt(i,"shopid");
                        item.isrevert = data.getInt(i,"isrevert");
                        item.isshow = data.getInt(i,"isshow");
                        list1.add(item);
                    }
                }
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("isrevert", 0);//0未回复1已回复2全部
                params.put("isshow", 0);//-1不展示1展示0全部（审核展示字段，用户端用，商家端查全部）
                params.put("shopid",ShoppingInfo.id);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3013, "/consulting",config, params);

            }

            @Override
            public void onLoadingMore(PullRefreshListView t,
                                      Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("isrevert", 0);//0未回复1已回复2全部
                params.put("isshow", 0);//-1不展示1展示0全部（审核展示字段，用户端用，商家端查全部）
                params.put("shopid",ShoppingInfo.id);
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3013, "/consulting",config, params);
            }
        });
        adapter1 = new MessageAdapter(mActivity, list1);
        listView1.setAdapter(adapter1);
        /*adapter1.setCommodityCallback(new MessageAdapter.CommodityCallback() {

            @Override
            public void doCallBack(int i) {
                listView1.performLoading();
            }
        });*/
        pagerList.add(listView1);
        //已回复
        listView2 = new PullRefreshListView(mActivity);
        listView2.setNodataImage(R.drawable.nomessage);
        listView2.setNodataText("暂无留言");
        listView2.setKeyName("nopayment");
        listView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    MessageInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new MessageInfo();
                        item.nickname = data.getString(i,"nickname");
                        item.realname = data.getString(i,"realname");
                        item.icon = data.getString(i,"icon");
                        item.addtime = data.getString(i,"addtime");
                        item.shopname = data.getString(i,"shopname");
                        item.usernotes = data.getString(i,"usernotes");
                        item.shopnotes = data.getString(i,"shopnotes");
                        item.retime = data.getString(i,"retime");
                        item.id = data.getInt(i,"id");
                        item.uid = data.getInt(i,"uid");
                        item.shopid = data.getInt(i,"shopid");
                        item.isrevert = data.getInt(i,"isrevert");
                        item.isshow = data.getInt(i,"isshow");
                        list2.add(item);
                    }
                }
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("isrevert", 1);//0未回复1已回复2全部
                params.put("isshow", 0);//-1不展示1展示0全部（审核展示字段，用户端用，商家端查全部）
                params.put("shopid",ShoppingInfo.id);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3013, "/consulting",config, params);

            }

            @Override
            public void onLoadingMore(PullRefreshListView t,
                                      Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("isrevert", 1);//0未回复1已回复2全部
                params.put("isshow", 0);//-1不展示1展示0全部（审核展示字段，用户端用，商家端查全部）
                params.put("shopid",ShoppingInfo.id);
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3013, "/consulting",config, params);

            }
        });

        adapter2 = new MessageAdapter(mActivity, list2);
        listView2.setAdapter(adapter2);
       /* adapter2.setCommodityCallback(new MessageAdapter.CommodityCallback() {

            @Override
            public void doCallBack(int i) {
                listView2.performLoading();
            }
        });*/
        pagerList.add(listView2);
        pagerAdapter = new ViewPagerAdapter(pagerList,mActivity);
        viewPager.setAdapter(pagerAdapter);
        listView1.performLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        listView1.performLoading();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = (MainActivity)activity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0){
            radioGroup.check(R.id.rb_entity);
        }else if(position == 1){
            radioGroup.check(R.id.rb_dummy);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_entity){
            if(viewPager.getCurrentItem() != 0){
                viewPager.setCurrentItem(0);
                listView1.performLoading();
            }
        }else if(checkedId == R.id.rb_dummy){
            if(viewPager.getCurrentItem() != 1){
                viewPager.setCurrentItem(1);
                listView2.performLoading();
            }
        }
    }
}
