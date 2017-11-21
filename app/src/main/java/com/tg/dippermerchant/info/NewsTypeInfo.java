package com.tg.dippermerchant.info;

import java.io.Serializable;

public class NewsTypeInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4651783239426035190L;
	public int id;
	public int homePushPeople;
	public int showType;
	public int isread;
	public int isHTML5url;
	public int isPC;
	public int notread;
	public String originalId="";
	public String content="";
	public String icon="";
	public String homePushUrl="";
	public String homePushTime="";
	public String weiappcode="";
	public String weiappname="";
	public String HTML5url="";
	public String PCurl="";
	public String secretKey="";
	public String tookiy="";
	public String keystr="";
}
