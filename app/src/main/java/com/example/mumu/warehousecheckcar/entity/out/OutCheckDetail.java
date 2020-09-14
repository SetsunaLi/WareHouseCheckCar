package com.example.mumu.warehousecheckcar.entity.out;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by mumu on 2018/12/10.
 * 出库申请单详细列表
 */

public class OutCheckDetail {
    /**车牌号*/
    private String carNo;
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
    private double weight;
    /**
     * 总重量
     */
    private double weightall;
    /**
     * 入库总量*/
    private double weight_in;
    /**操作人*/
    private String operator;
    /**操作时间*/
    private Date operatingTime;
    /***数量*/
    private int count;
    /**设备号*/
    private String device;

    private boolean flag=false;
    private int type=1;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public double getWeight_in() {
        return weight_in;
    }

    public void setWeight_in(double weight_in) {
        this.weight_in = weight_in;
    }

    public double getWeightall() {
        return weightall;
    }

    public void setWeightall(double weightall) {
        this.weightall = weightall;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void addCount(){
        count++;
    }
    public void clearCount(){
        count=0;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
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
    public OutCheckDetail(String product_no, String vatNo, String selNo, String color, String fabRool, double weight, double weight_in, String operator, int count) {
        this.product_no = product_no;
        this.vatNo = vatNo;
        this.selNo = selNo;
        this.color = color;
        this.fabRool = fabRool;
        this.weight = weight;
        this.weight_in = weight_in;
        this.operator = operator;
        this.count = count;
    }
    public OutCheckDetail(){    }

   /* @Override
    public int compareTo(@NonNull OutCheckDetail outCheckDetail) {
        String  aFab=this.getFabRool();
        if (aFab==null)
            return 1;
        String bFab=outCheckDetail.getFabRool();
        if (bFab==null)
            return -1;
        if (aFab!=null&&bFab!=null){
            if (Integer.valueOf(aFab)>=Integer.valueOf(bFab)){
                return 1;
            }
            return -1;
        }
        return 0;
        //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
           *//*  if(this.Age >= s.getAge()){
                       return 1;
                 }
               return -1;*//*
    }*/
}
