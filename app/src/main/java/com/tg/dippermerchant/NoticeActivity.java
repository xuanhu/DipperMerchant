package com.tg.dippermerchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.tg.dippermerchant.adapter.NoticeAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.NoticeInfo;
import com.tg.dippermerchant.inter.OnLoadingListener;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.ResponseData;
import com.tg.dippermerchant.view.PullRefreshListView;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * 商家公告
 */
public class NoticeActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private PullRefreshListView pullListView;
    private NoticeAdapter adapter;
    private ArrayList<NoticeInfo> list = new ArrayList<NoticeInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
        adapter = new NoticeAdapter(this, list);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.hideFooterView();
        pullListView.setNodataImage(R.drawable.nonotice);
        pullListView.setNodataText("暂无公告");
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                // TODO Auto-generated method stub
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
                            item.releasedtime = data.getString(i, "releasedtime");
                            list.add(item);
                        }
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand,
                                      int pagerIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(NoticeActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("scope", 3);
                HttpTools.httpGet(Contants.URl.URl_3011, "/notice/getnoticeByscope",config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(NoticeActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("scope", 3);
                HttpTools.httpGet(Contants.URl.URl_3011, "/notice/getnoticeByscope",config, params);
            }
        });
        pullListView.performLoading();
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_notice,null);
    }

    @Override
    public String getHeadTitle() {
        return "商家公告";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(NoticeActivity.this,MyBrowserActivity.class);
        intent.putExtra(MyBrowserActivity.KEY_TITLE,list.get(position).title);
        intent.putExtra(MyBrowserActivity.KEY_HTML_TEXT,list.get(position).bewrite);
        startActivity(intent);
    }
}
