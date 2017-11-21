package com.tg.dippermerchant.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.dippermerchant.MessageDetailsActivity;
import com.tg.dippermerchant.R;
import com.tg.dippermerchant.base.MyBaseAdapter;
import com.tg.dippermerchant.constant.Contants;
import com.tg.dippermerchant.info.MessageInfo;
import com.tg.dippermerchant.info.ShoppingInfo;
import com.tg.dippermerchant.net.HttpTools;
import com.tg.dippermerchant.net.MessageHandler;
import com.tg.dippermerchant.net.RequestConfig;
import com.tg.dippermerchant.net.RequestParams;
import com.tg.dippermerchant.net.image.VolleyUtils;
import com.tg.dippermerchant.util.Tools;
import com.tg.dippermerchant.view.SlideSwitchView;
import com.tg.dippermerchant.view.dialog.DialogFactory;
import com.tg.dippermerchant.view.dialog.ToastFactory;

import java.util.ArrayList;

public class MessageAdapter extends MyBaseAdapter<MessageInfo> implements MessageHandler.ResponseListener {
    private ArrayList<MessageInfo> list;
    private LayoutInflater inflater;
    private MessageInfo item;
    private Activity context;
    private int id;
    private int state;
    private int operationPosition;
    private SlideSwitchView mSlideSwitchView;
    private MessageHandler msgHand;


    /**
     * 自定义一个刷新列表接口，用于回调CommodityManageMentActivity
     */
    public interface MessageCallback {
        public void doCallBack(int i);
    }

    private MessageCallback messagecallback;

    public void setCommodityCallback(MessageCallback messagecallback) {
        this.messagecallback = messagecallback;
    }

    public MessageAdapter(Activity con, ArrayList<MessageInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
        msgHand = new MessageHandler(con);
        msgHand.setResponseListener(this);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        item = list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_item, null);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        RelativeLayout rlRevert = (RelativeLayout) convertView.findViewById(R.id.rl_revert);
        RelativeLayout rlRevertDetails = (RelativeLayout) convertView.findViewById(R.id.rl_revert_details);
        TextView tvRevert = (TextView) convertView.findViewById(R.id.tv_revert);
        TextView tvRevertTime = (TextView) convertView.findViewById(R.id.tv_revert_time);
        mSlideSwitchView = (SlideSwitchView) convertView.findViewById(R.id.mSlideSwitchView);
        if (item.isshow == -1) {//不展示
            mSlideSwitchView.setChecked(false);
        } else {
            mSlideSwitchView.setChecked(true);
        }

        mSlideSwitchView.setOnChangeListener(new SlideSwitchView.OnSwitchChangedListener() {

            @Override
            public void onSwitchChange(SlideSwitchView switchView, boolean isChecked) {
                // TODO Auto-generated method stub
                operationPosition = position;
                PutAudit();
            }
        });
        if (item.isrevert == 0) {//0未回复1已回复2全部
            rlRevert.setVisibility(View.VISIBLE);
            rlRevertDetails.setVisibility(View.GONE);
        } else {
            rlRevert.setVisibility(View.GONE);
            rlRevertDetails.setVisibility(View.VISIBLE);
            tvRevert.setText(item.shopnotes);
            tvRevertTime.setText(item.retime);
        }
        tvName.setText(item.nickname);
        tvContent.setText(item.usernotes);
        tvTime.setText(item.addtime);
        rlRevert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageInfo info = list.get(position);
                Intent intent = new Intent(context, MessageDetailsActivity.class);
                intent.putExtra(MessageDetailsActivity.MESSAGE_INFO, info);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private void PutAudit() {
        RequestConfig config = new RequestConfig(context, HttpTools.PUT_AUDIT);
        config.handler = msgHand.getHandler();
        RequestParams params = new RequestParams();
        params.put("id", list.get(operationPosition).id);
        if (list.get(operationPosition).isshow == 1) {
            params.put("isshow", "-1");//1通过-1不通过
        } else {
            params.put("isshow", "1");//1通过-1不通过
        }
        HttpTools.httpPut(Contants.URl.URl_3013, "/consulting/audit", config, params);
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            ToastFactory.showToast(context, "修改成功");
        } else {
            ToastFactory.showToast(context, message);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

}
