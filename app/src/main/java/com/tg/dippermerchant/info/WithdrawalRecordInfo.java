package com.tg.dippermerchant.info;

import java.io.Serializable;

public class WithdrawalRecordInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 268038726368784988L;
	public int id;
	public int state;
	public int operatorId;
	public String bankname = "";
	public String enote = "";
	public String note = "";
	public String payaccount = "";
	public String eName = "";
	public String operator = "";
	public String addTime = "";
	public float money ;
}
