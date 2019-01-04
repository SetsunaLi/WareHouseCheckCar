package com.example.mumu.warehousecheckcar.entity;

import java.util.Date;

/**
 * Created by mumu on 2019/1/3.
 */

public class Input {
    /**库位信息（请求的时候默认为空，上传的时候会带参）*/
    private Carrier carrier;
    /**布匹EPC码*/
    private String epc;
    /***布号*/
    private String product_no;
    /***缸号*/
    private String vatNo;
    /***色号（色号指的都是销售色号）*/
    private String selNo;
    /***颜色*/
    private String color;
    /** 布票号     */
    private String fabRool;
    /**重量（指的是库存重量）*/
    private double weight;
    /**总重量（这个可以不用管）*/
    private double weightall;
    /**入库重量*/
    private double weight_in;
    /**操作人*/
    private String operator;
    /**操作时间*/
    private Date operatingTime;
    /**设备号（请求的时候默认为空，上传的时候会带参）*/
    private String device;
    /***实盘数量（这个可以不用管）*/
    private int count;
    /**勾选状态（默认选中；这个可以不用管）*/
    private boolean status=true;

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeightall() {
        return weightall;
    }

    public void setWeightall(double weightall) {
        this.weightall = weightall;
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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
