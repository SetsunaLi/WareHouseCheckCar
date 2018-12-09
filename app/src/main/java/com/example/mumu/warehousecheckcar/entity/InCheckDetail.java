package com.example.mumu.warehousecheckcar.entity;

import java.util.Date;

/**
 * Created by mumu on 2018/12/08.
 * 申请单详细列表
 */

public class InCheckDetail {
    /**
     * EPC码
     */
    private String epc;
    /***布号*/
    private String product_no;
    /***缸号*/
    private String vatNo;
    /***色号*/
    private String selNo;
    /***颜色*/
    private String color;
    /**
     * 布票号
     */
    private String fabRool;
    /**
     * 重量
     */
    private float weight;
    /**操作人*/
    private String operator;
    /**操作时间*/
    private Date operatingTime;
    /***数量*/
    private int count;


    public void addCount(){
        count++;
    }
    public void clearCount(){
        count=0;
    }
    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getProduct_no() {
        return product_no;
    }

    public void setProduct_no(String product_no) {
        this.product_no = product_no;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public String getSelNo() {
        return selNo;
    }

    public void setSelNo(String selNo) {
        this.selNo = selNo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFabRool() {
        return fabRool;
    }

    public void setFabRool(String fabRool) {
        this.fabRool = fabRool;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(Date operatingTime) {
        this.operatingTime = operatingTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
