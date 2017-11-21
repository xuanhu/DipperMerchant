package com.tg.dippermerchant.info;

import java.io.Serializable;

public class CommodityInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8222378520785366272L;
	public String name = "";
	public String token = "";
	public String source = "";
	public String addTime = "";
	public String eTime = "";
	public String bTime = "";
	public String imgUrl = "";
	public String imgName = "";
	public String describe = "";//描述
	public float price;//平台格
	public float originalPrice;//原价
	public float sellingprice;//售价
	public float discount;//折扣
	public int amount;//数量
	public int state;//状态
	public int type;//类型
	public int id;
}
