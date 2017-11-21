package com.tg.dippermerchant.info;

import java.io.Serializable;
import java.util.ArrayList;

public class AfterSalesInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6758551770383924739L;
	public int id ;
	public int integral ;//积分
	public int status ;//-3退款失败，-2已取消，-1未通过审核，0等待审核，1待用户退货，2待收货，3待退款，4已退款，5完成
	public int type ;//1退货，2换货，3返修,4退款
	public String orderid = "";//订单id
	public String imgurl = "";
	public String addtime = "";
	public String refundtime = "";
	public String agreetime = "";
	public String returnIntegral = "";//
	public String question = "";//问题
	public String reason = "";//原因
	public float userpayprice;
	public float onlinepayprice;//在线支付
	public ArrayList<OrderCommoditysInfo>  commoditys = new ArrayList<OrderCommoditysInfo>();
	
}
