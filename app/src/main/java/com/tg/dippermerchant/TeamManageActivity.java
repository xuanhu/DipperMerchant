package com.tg.dippermerchant;

import com.tg.dippermerchant.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
/**
 * 团队管理
 * @author Administrator
 *
 */
public class TeamManageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_team_manage,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "团队管理";
	}

}
