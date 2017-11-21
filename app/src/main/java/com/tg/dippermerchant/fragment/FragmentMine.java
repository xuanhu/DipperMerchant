package com.tg.dippermerchant.fragment;

import java.util.ArrayList;

import com.tg.dippermerchant.MerchantAccountActivity;
import com.tg.dippermerchant.WithdrawalRecordActivity;
import com.tg.dippermerchant.base.BaseBrowserActivity;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.info.UserInfo;
import com.tg.dippermerchant.net.DES;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.object.ViewConfig;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.util.statusbar.StatusBarUtil;
import com.tg.dippermerchant.view.MessageArrowView;
import com.tg.dippermerchant.view.RoundImageView;
import com.tg.dippermerchant.view.MessageArrowView.ItemClickListener;
import com.tg.dippermerchant.MainActivity;
import com.tg.dippermerchant.MyBrowserActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.SettingActivity;
import com.tg.dippermerchant.UserInfoActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 个人中心(商家)
 * @author Administrator
 *
 */
public class FragmentMine extends Fragment implements ItemClickListener{
	private View mView;
	private MainActivity mActivity;
	private MessageArrowView mineInfoZone1, mineInfoZone2;
	private RoundImageView rivHead;
	private TextView tvCName;
	private TextView tvName;
	private TextView tvHeadMine;
	private int size;
	private RelativeLayout rlUserInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_mine, container, false);
		initView();
		initData();
		return mView;
	}

	/**
	 * 初始化控件
	 */
	private void initView(){
		tvHeadMine = (TextView) mView.findViewById(R.id.tv_head_mine);
		tvHeadMine.setPadding(0, StatusBarUtil.getStatusBarHeight(getContext()),0,0);
		rlUserInfo= (RelativeLayout) mView.findViewById(R.id.rl_userInfo);
		rivHead = (RoundImageView) mView.findViewById(R.id.img_head);
		tvName = (TextView) mView.findViewById(R.id.tv_name);
		tvCName = (TextView) mView.findViewById(R.id.tv_cname);
		rivHead.setCircleShape();
		freshUI();
		rlUserInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mActivity, UserInfoActivity.class));
			}
		});
		mineInfoZone1 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone1);
		mineInfoZone2 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone2);
		mineInfoZone1.setItemClickListener(this);
		mineInfoZone2.setItemClickListener(this);
		
		ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
		ViewConfig viewConfig = new ViewConfig("商家钱包", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.shnagjia);
		list1.add(viewConfig);
		
		viewConfig = new ViewConfig("商家流水", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.deal);
		list1.add(viewConfig);
		
		viewConfig = new ViewConfig("提现记录", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.tixian);
		list1.add(viewConfig);
		

		/*viewConfig = new ViewConfig("营运统计", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.wallet_details);
		list1.add(viewConfig);*/
		mineInfoZone1.setData(list1);
		
		ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
		viewConfig = new ViewConfig("更多设置", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.shezhi);
		list2.add(viewConfig);
		mineInfoZone2.setData(list2);
	}
	
	public void freshUI(){
		tvName.setText(ShoppingInfo.name);
		tvCName.setText(ShoppingInfo.cname);
		initData();
	}
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		if (mv == mineInfoZone1) {
			if (position == 0) {// 商家钱包
				startActivity(new Intent(mActivity, MerchantAccountActivity.class));
			}else if(position == 1){//商家流水
				//startActivity(new Intent(mActivity, ProceedsActivity.class));
				Intent intent = new Intent(mActivity,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+Tools.getEncryptURL(DES.KEY_URl,"&type=3"));
				startActivity(intent);
			}else if(position == 2){//提现记录
				startActivity(new Intent(mActivity, WithdrawalRecordActivity.class));
			}else  if(position == 3){//营运统计
				//startActivity(new Intent(mActivity, TeamManageActivity.class));
				Intent intent = new Intent(mActivity,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,Contants.URl.H5_URl_Order+Tools.getEncryptURL(DES.KEY_URl,"&type=4"));
				startActivity(intent);
			}
		} else if (mv == mineInfoZone2) {
			if (position == 0) {//  更多设置
				startActivity(new Intent(mActivity, SettingActivity.class));
			}
		}
	}
	
	public void initData(){
		VolleyUtils.getImage(getActivity(), ShoppingInfo.imgs, rivHead, size,size,R.drawable.moren_geren);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		size = getResources().getDimensionPixelSize(R.dimen.margin_45);
		mActivity = (MainActivity)activity;
	}
}
