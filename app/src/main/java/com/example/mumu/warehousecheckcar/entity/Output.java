package com.example.mumu.warehousecheckcar.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mumu on 2019/1/3.
 */

public class Output implements Cloneable, Serializable {
    /**申请单号*/
    private String applyNo;
    /***id*/
    private String outp_id;
    /***布号*/
    private String product_no;
    /***缸号*/
    private String vatNo;
    /***色号（色号指的都是销售色号）*/
    private String selNo;
    /***颜色*/
    private String color;
    /**设备号*/
    private String device;
    /***申请数量*/
    private int countOut;
    /****/
    private int countLosses=0;

    /***扫描数量（这个可以不用管）*/
    private int count=0;
    /**扫描总重量（手持机扫描时候计算，这个可以不用管）*/
    private double weightall=0;


    /***配货数量（这个可以不用管）*/
    private int countProfit=0;
    /**配货总重量（手持机扫描时候计算，这个可以不用管）*/
    private double weightPei=0;

    /**头*/
    private boolean status=false;
    /***出库申请单缸号列表*/
    private List<OutputDetail> list;
    /***标志位,0为正常，2为非正常申请单扫码*/
    private int flag=0;

    public double getWeightPei() {
        return weightPei;
    }

    public void setWeightPei(double weightPei) {
        this.weightPei = weightPei;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getOutp_id() {
        return outp_id;
    }

    public void setOutp_id(String outp_id) {
        this.outp_id = outp_id;
    }

    public void addCount(){
        count++;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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

    public int getCountOut() {
        return countOut;
    }

    public void setCountOut(int countOut) {
        this.countOut = countOut;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCountProfit() {
        return countProfit;
    }

    public void setCountProfit(int countProfit) {
        this.countProfit = countProfit;
    }

    public int getCountLosses() {
        return countLosses;
    }

    public void setCountLosses(int countLosses) {
        this.countLosses = countLosses;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<OutputDetail> getList() {
        return list;
    }

    public void setList(List<OutputDetail> list) {
        this.list = list;
    }

    @Override
    public Object clone() {
        Output stu = null;
        try{
            stu = (Output)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
