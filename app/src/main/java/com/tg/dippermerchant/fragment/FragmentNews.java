package com.tg.dippermerchant.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tg.dippermerchant.NewsActivity;
import com.tg.dippermerchant.MainActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.adapter.NewsTypeAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.NewsTypeInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.StringUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.util.statusbar.StatusBarUtil;
import com.tg.dippermerchant.view.PullRefreshListView;

import java.util.ArrayList;

/**
 * 消息
 */

public class FragmentNews extends Fragment implements AdapterView.OnItemClickListener {
    private View mView;
    private static final int ISTREAD=1;
    private MainActivity mActivity;
    private PullRefreshListView pullListView;
    private NewsTypeAdapter adapter;
    private String messageListCache;
    private ArrayList<NewsTypeInfo> list = new ArrayList<NewsTypeInfo>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_news,container, false);
        initView(mView);
        return mView;
    }

    /**
     * 初始化控件
     * @param mView
     */
    private void initView(View mView) {
        TextView tvHeadMessage = (TextView) mView.findViewById(R.id.tv_head_message);
        tvHeadMessage.setPadding(0, StatusBarUtil.getStatusBarHeight(getContext()),0,0);
        pullListView = (PullRefreshListView)mView.findViewById(R.id.pull_listview);
        adapter = new NewsTypeAdapter(mActivity, list);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.setNodataImage(R.drawable.nonews);
        pullListView.setNodataText("暂无消息");
        //读取本地缓存列表
        getCacheList();
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                // TODO Auto-generated method stub
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    Tools.saveMessageList(mActivity, response);
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    NewsTypeInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new NewsTypeInfo();
                        item.id = data.getInt(i, "id");
                        item.homePushPeople = data.getInt(i, "homePushPeople");
                        item.showType = data.getInt(i, "showType");
                        item.isHTML5url = data.getInt(i, "isHTML5url");
                        item.isPC = data.getInt(i, "isPC");
                        item.notread = data.getInt(i, "notread");
                        item.content = data.getString(i, "content");
                        item.icon = data.getString(i, "icon");
                        item.homePushUrl = data.getString(i,"homePushUrl");
                        item.homePushTime = data.getString(i, "homePushTime");
                        item.weiappcode = data.getString(i, "weiappcode");
                        item.weiappname = data.getString(i, "weiappname");
                        item.HTML5url = data.getString(i, "HTML5url");
                        item.PCurl = data.getString(i, "PCurl");
                        item.secretKey = data.getString(i, "secretKey");
                        item.tookiy = data.getString(i, "tookiy");
                        item.keystr = data.getString(i, "keystr");
                        list.add(item);
                    }
                }
                getNotReadNum();
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand,
                                      int pagerIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("uid", ShoppingInfo.id);
                params.put("type", 2);//1员工，2商家
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3011, "/homepush",config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("uid", ShoppingInfo.id);
                params.put("type", 2);//1员工，2商家
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URl_3011, "/homepush",config, params);
            }
        });
        pullListView.performLoading();
    }
    private void getNotReadNum(){
        int notNum = 0;
        for (int i = 0; i < list.size(); i++){ //外循环是循环的次数
            notNum += list.get(i).notread;
        }
        /**
         * 未读消息数量更新
         */
        MainActivity mainactivity = (MainActivity) getActivity();
        mainactivity.UpdataUnreadNum(notNum);
    }
    /**
     * 获取消息缓存列表
     */
    private void getCacheList() {
        messageListCache = Tools.getMessageList(mActivity);
        if(StringUtils.isNotEmpty(messageListCache)){
            String jsonString = HttpTools.getContentString(messageListCache);
            if (jsonString != null) {
                ResponseData data = HttpTools.getResponseData(jsonString);
                NewsTypeInfo item;
                for (int i = 0; i < data.length; i++) {
                    item = new NewsTypeInfo();
                    item.id = data.getInt(i, "id");
                    item.homePushPeople = data.getInt(i, "homePushPeople");
                    item.showType = data.getInt(i, "showType");
                    item.isHTML5url = data.getInt(i, "isHTML5url");
                    item.isPC = data.getInt(i, "isPC");
                    item.notread = data.getInt(i, "notread");
                    item.content = data.getString(i, "content");
                    item.icon = data.getString(i, "icon");
                    item.homePushUrl = data.getString(i,"homePushUrl");
                    item.homePushTime = data.getString(i, "homePushTime");
                    item.weiappcode = data.getString(i, "weiappcode");
                    item.weiappname = data.getString(i, "weiappname");
                    item.HTML5url = data.getString(i, "HTML5url");
                    item.PCurl = data.getString(i, "PCurl");
                    item.secretKey = data.getString(i, "secretKey");
                    item.tookiy = data.getString(i, "tookiy");
                    item.keystr = data.getString(i, "keystr");
                    list.add(item);
                }
            }
        }
        pullListView.setAdapter(adapter);
        getNotReadNum();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = (MainActivity)activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsTypeInfo info = list.get(position);

        Intent intent = new Intent(mActivity, NewsActivity.class);
        if((int) parent.getAdapter().getItemId(position) != -1){
            intent.putExtra(NewsActivity.DESKTOP_WEIAPPCODE,info);
            startActivityForResult(intent,ISTREAD);
        }else{
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ISTREAD){
            pullListView.performLoading();
        }
    }
}
