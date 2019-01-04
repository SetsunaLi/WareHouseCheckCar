package com.example.mumu.warehousecheckcar.entity;

import java.util.Date;

/**
 * Created by mumu on 2019/1/3.
 */

public class OutputDetail {
    /**EPC*/
    private String epc;
    /** 布票号     */
    private String fabRool;
    /**重量（指的是库存重量）*/
    private double weight;
    /**入库重量*/
    private double weight_in;
    /**操作人*/
    private String operator;
    /**操作时间*/
    private Date operatingTime;
    /**标志位（默认为0；0为默认状态，1为实盘扫码出库状态，2为非正常申请单扫码，3默认超出配货值第一个开始为3）*/
    private int flag=0;

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getFabRool() {
        return fabRool;
    }

    public void setFabRool(String fabRool) {
        this.fabRool = fabRool;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight_in() {
        return weight_in;
    }

    public void setWeight_in(double weight_in) {
        this.weight_in = weight_in;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
