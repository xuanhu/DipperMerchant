package com.tg.dippermerchant.info;

/**
 * Created by Administrator on 2017/10/20.
 */

public class OrderEvent {
    private int ordertype;
    private int state;
    private boolean isHides;
    public OrderEvent(int ordertype, int state,boolean isHides) {
        this.ordertype = ordertype;
        this.state = state;
        this.isHides = isHides;
    }
    public boolean isHides() {
        return isHides;
    }

    public void setisHides(boolean showing) {
        isHides = showing;
    }

    public int getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(int ordertype) {
        this.ordertype = ordertype;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
