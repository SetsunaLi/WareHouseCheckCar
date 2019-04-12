package com.example.mumu.warehousecheckcar.entity;

import java.util.Date;

public class Cloth {
    /**布号*/
    private String clothNum;
    private String carNo = "";
    /***缸号*/
    private String vatNo = "";
    /***生产单号*/
    private String product_no = "";
    /***色号（色号指的都是销售色号）*/
    private String selNo = "";
    /***颜色*/
    private String color = "";
    /** 布票号     */
    private String fabRool = "";
    /**重量（指的是库存重量）*/
    private Double weight = 0.0;
    /**入库重量*/
    private Double weight_in = 0.0;
    /**操作人*/
    private String operator = "";
//    private String count = "";
    /**布匹EPC码*/
    private String epc = "";
    /**设备号（请求的时候默认为空，上传的时候会带参）*/
    private String device;

   /* public Cloth(String clothNum, String vatNo, String selNo, String color, String fabRool, Double weight, String epc) {
        this.clothNum = clothNum;
        this.vatNo = vatNo;
        this.selNo = selNo;
        this.color = color;
        this.fabRool = fabRool;
        this.weight = weight;
        this.epc = epc;
    }*/

    public String getClothNum() {
        return clothNum;
    }

    public void setClothNum(String clothNum) {
        this.clothNum = clothNum;
    }
    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public String getProduct_no() {
        return product_no;
    }

    public void setProduct_no(String product_no) {
        this.product_no = product_no;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight_in() {
        return weight_in;
    }

    public void setWeight_in(Double weight_in) {
        this.weight_in = weight_in;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

   /* public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }*/

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
