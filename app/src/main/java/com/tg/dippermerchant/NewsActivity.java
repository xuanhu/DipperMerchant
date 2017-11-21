package com.tg.dippermerchant;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.dippermerchant.adapter.NewsTypeAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.NewsTypeInfo;
import com.tg.dippermerchant.info.NoticeInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.PullRefreshListView;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 消息推送消息列表
 *
 * @author Administrator
 */
public class NewsActivity extends BaseActivity implements OnItemClickListener {
    public static final String DESKTOP_WEIAPPCODE = "weiappcode";
    private PullRefreshListView pullListView;
    private NewsTypeAdapter adapter;
    private NewsTypeInfo item;
    private ArrayList<NewsTypeInfo> list = new ArrayList<NewsTypeInfo>();
    private  String bewrite,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            item = (NewsTypeInfo) intent.getSerializableExtra(DESKTOP_WEIAPPCODE);
        }
        if (item == null) {
            ToastFactory.showToast(NewsActivity.this, "参数错误");
            finish();
        }
        headView.setTitle(item.weiappname);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
        adapter = new NewsTypeAdapter(NewsActivity.this, list);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.setNodataImage(R.drawable.nonews);
        pullListView.setNodataText("暂无消息");
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore, Message msg, String response) {
                JSONArray content = HttpTools.getContentJsonArray(response);
                ResponseData data = HttpTools.getResponseContent(content);
                if (data.length > 0) {
                    NewsTypeInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new NewsTypeInfo();
                        item.id = data.getInt(i, "id");
                        item.homePushPeople = data.getInt(i, "homePushPeople");
                        item.showType = data.getInt(i, "showType");
                        item.isHTML5url = data.getInt(i, "isHTML5url");
                        item.isPC = data.getInt(i, "isPC");
                        item.isread = data.getInt(i, "isread");
                        item.originalId = data.getString(i, "originalId");
                        item.content = data.getString(i, "content");
                        item.homePushUrl = data.getString(i, "homePushUrl");
                        item.homePushTime = data.getString(i, "homePushTime");
                        item.weiappcode = data.getString(i, "weiappcode");
                        item.weiappname = data.getString(i, "weiappname");
                        item.HTML5url = data.getString(i, "HTML5url");
                        item.PCurl = data.getString(i, "PCurl");
                        item.secretKey = data.getString(i, "secretKey");
                        item.tookiy = data.getString(i, "tookiy");
                        item.keystr = data.getString(i, "keystr");
                        item.icon = data.getString(i, "icon");
                        list.add(item);
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(NewsActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("weiappcode", item.weiappcode);
                params.put("uid", ShoppingInfo.id);
                params.put("type", 2);
                HttpTools.httpGet(Contants.URl.URl_3011, "/homepush/gethomePushByweiappId", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(NewsActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("weiappcode", item.weiappcode);
                params.put("uid", ShoppingInfo.id);
                params.put("type", 2);
                HttpTools.httpGet(Contants.URl.URl_3011, "/homepush/gethomePushByweiappId", config, params);
            }
        });
        pullListView.performLoading();
    }

    /**
     * 获取公告详情
     */
    private void getNoticeDetails(String  id){
        RequestConfig config = new RequestConfig(NewsActivity.this, HttpTools.GET_NOTICE_INFO,"获取详情");
        RequestParams params = new RequestParams();
        params.put("id",id);
        HttpTools.httpGet(Contants.URl.URl_3011,"/notice/getnoticedetail", config, params);
}
    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(code == 0){
            JSONArray content  = HttpTools.getContentJsonArray(jsonString);
            if (content != null) {
                ResponseData data = HttpTools.getResponseContent(content);
                bewrite = data.getString(0,"bewrite");
                title = data.getString(0, "title");
                Intent intent = new Intent(NewsActivity.this,MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_TITLE,title);
                intent.putExtra(MyBrowserActivity.KEY_HTML_TEXT,bewrite);
                startActivity(intent);
                }
        }else{
            ToastFactory.showToast(NewsActivity.this,message);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         *   //商家
         * "商家公告":"sjgonggao",
         *  //商家用户公用
         * "收款消息":"skxiaoxi",
         * "订单消息":"ddxiaoxi",
         * "提现消息":"txxiaoxi",
         *  //用户
         * "用户公告":"yhgonggao",
         * "付款消息":"fkxiaoxi"
         */
        Intent intent ;
        NewsTypeInfo item = list.get(position);
        if(item.weiappcode.equals("ddxiaoxi")){//订单消息
            intent = new Intent(NewsActivity.this,MyBrowserActivity.class);
            intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+ Tools.getEncryptURL(DES.KEY_URl,"&type=0&oid="+item.originalId));
            startActivity(intent);
        }
        if(item.weiappcode.equals("txxiaoxi")){//提现消息
            intent = new Intent(NewsActivity.this,AccountNewsActivity.class);
            intent.putExtra(AccountNewsActivity.ORIGINALID,item.originalId);
            intent.putExtra(AccountNewsActivity.STATE,"提现详情");
            startActivity(intent);
        }
        if(item.weiappcode.equals("skxiaoxi")){//收款消息
             intent = new Intent(NewsActivity.this,AccountNewsActivity.class);
            intent.putExtra(AccountNewsActivity.ORIGINALID,item.originalId);
            intent.putExtra(AccountNewsActivity.STATE,"收款详情");
            startActivity(intent);

        }
        if(item.weiappcode.equals("sjgonggao")){//商家公告
            getNoticeDetails(item.originalId);
        }

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_desk_top, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

}
