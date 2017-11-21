package com.tg.dippermerchant;

import java.util.ArrayList;

import com.tg.dippermerchant.adapter.ComplaintAdapter;
import com.tg.dippermerchant.base.BaseActivity;
import com.tg.dippermerchant.info.ComplaintInfo;
import com.tg.dippermerchant.view.PullRefreshListView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的投诉
 * 
 * @author Administrator
 * 
 */
public class MyComplaintActivity extends BaseActivity implements OnItemClickListener{
	private PullRefreshListView pullListView;
	private ComplaintAdapter adapter;
	private ArrayList<ComplaintInfo> Complaintlist = new ArrayList<ComplaintInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		test();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
		pullListView.setEnableMoreButton(false);
		adapter = new ComplaintAdapter(this, Complaintlist);
		pullListView.setAdapter(adapter);
		pullListView.setOnItemClickListener(this);
	//	pullListView.setOnLoadingListener(this);
		pullListView.performLoading();
	}
	private void test(){
		ComplaintInfo info = new ComplaintInfo();
		info.section="工程部";
		info.type = "房屋问题";
		info.content = "维修服务态度太差";
		info.time = "2016-07-12 18:55";
		info.status = "超期2天";
		Complaintlist.add(info);
		
		info = new ComplaintInfo();
		info.type = "公共设施报修";
		info.section="人事部";
		info.content = "把我档案弄丢，一直不给予答复";
		info.time = "2016-06-23 14:00";
		info.status = "超期7天";
		Complaintlist.add(info);
		
		adapter.notifyDataSetChanged();
	}
	/*@Override
	public void onLoading(PullRefreshListView t, Handler hand) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void refreshData(PullRefreshListView t, boolean isLoadMore,Message msg, String response) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
		// TODO Auto-generated method stub
		
	}*/
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_my_complaint, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "我的投诉";
	}
}
