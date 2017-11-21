package com.tg.dippermerchant.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;


import com.tg.dippermerchant.R;
import com.tg.dippermerchant.adapter.OrderSelectAdapter;
import com.tg.dippermerchant.adapter.ViewPagerAdapter;
import com.tg.dippermerchant.info.OrderEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class PopWindowView extends PopupWindow implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private View conentView;  
    private Intent intent;
    private Activity  context;
    private MyGridView gridview1;
    private MyGridView gridview2;
    private OrderSelectAdapter adapter1;
    private OrderSelectAdapter adapter2;
    private ShowViewPager viewPager;
    private RadioGroup radioGroup;
    private ViewPagerAdapter pagerAdapter;
    private  int selectorPositionNmerchandise;
    private  int selectorPositionService;
    private ArrayList<View> pagerList = new ArrayList<View>();

    public PopWindowView(final Activity context){
    	this.context = context;
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.popup_window, null);  
        int h = context.getWindowManager().getDefaultDisplay().getHeight();  
        int w = context.getWindowManager().getDefaultDisplay().getWidth();  
     // 设置SelectPicPopupWindow的View  
        this.setContentView(conentView);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationPreview);
        conentView.findViewById(R.id.view_bg).getBackground().setAlpha(30);
        init();
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    private void init() {
        radioGroup = (RadioGroup)conentView.findViewById(R.id.radio_group);
        viewPager = (ShowViewPager)conentView.findViewById(R.id.viewPager);
        viewPager.setEnableScroll(false);
        /**
         * 实物
         */
        gridview1 = new MyGridView(context);
        //gridView的点击事件
        gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击的position传递到adapter里面去
                adapter1.changeState(position);
                selectorPositionNmerchandise = position;
                EventBus.getDefault().post(new OrderEvent(3,selectorPositionNmerchandise,true));
                PopWindowView.this.dismiss();
            }
        });
        gridview1.setNumColumns(3);
        gridview1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter1 = new OrderSelectAdapter(context);
        gridview1.setAdapter(adapter1);
        pagerList.add(gridview1);
        /**
         * 服务
         */
        gridview2 = new MyGridView(context);
        gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击的position传递到adapter里面去
                adapter2.changeState(position);
                selectorPositionService = position;
                EventBus.getDefault().post(new OrderEvent(5,selectorPositionService,true));
                PopWindowView.this.dismiss();
            }
        });
        gridview2.setNumColumns(3);
        gridview2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter2 = new OrderSelectAdapter(context);
        gridview2.setAdapter(adapter2);
        pagerList.add(gridview2);
        /**
         * 虚拟
         */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View receiveView = inflater.inflate(R.layout.virtual_order, null);
        pagerList.add(receiveView);
        pagerAdapter = new ViewPagerAdapter(pagerList,context);
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 10);
        } else {  
            this.dismiss();  
        }  
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //viewPager.resetHeight(position);
        if(position == 0){
            radioGroup.check(R.id.rb_nmerchandise);
        }else if(position == 1){
            radioGroup.check(R.id.rb_service);
        }else {
            radioGroup.check(R.id.rb_virtual);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(checkedId == R.id.rb_nmerchandise){
            if(viewPager.getCurrentItem() != 0){
                viewPager.setCurrentItem(0);
                adapter1.notifyDataSetChanged();
                EventBus.getDefault().post(new OrderEvent(3,selectorPositionNmerchandise,false));
            }
        }else if(checkedId == R.id.rb_service){
            if(viewPager.getCurrentItem() != 1){
                viewPager.setCurrentItem(1);
                adapter2.notifyDataSetChanged();
                EventBus.getDefault().post(new OrderEvent(5,selectorPositionService,false));
            }
        }else {
            if(viewPager.getCurrentItem() != 2){
                viewPager.setCurrentItem(2);
            }
        }
    }
}