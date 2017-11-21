package com.tg.dippermerchant.info;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderInfo  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9091048591687147158L;
	public int state ;
	public String orderId ;
	public float price ;
	public float originprice ;
	public float sellingprice ;
	public float freightprice ;//运费
	public float payment ;//支付金额
	public String batchNo ;//订单编号
	public String receivename ;//收货人姓名
	public String paymenttime ;//付款时间
	public String orderstime ;//订单时间
	public String receiptTime ;//确认收货时间
	public String shipmentsTime ;//发货时间
	public ArrayList<OrderCommoditysInfo>  commoditys = new ArrayList<OrderCommoditysInfo>();
}
