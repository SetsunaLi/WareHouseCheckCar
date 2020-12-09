package com.example.mumu.warehousecheckcar.entity.check;

import com.example.mumu.warehousecheckcar.entity.putaway.Carrier;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mumu on 2018/12/21.
 */

public class Inventory implements Serializable {
    /**
     * 库位信息
     */
    private Carrier carrier;
    /**布匹EPC码*/
    private String epc;
    /***布号*/
    private String product_no;
    /***缸号*/
    private String vatNo;
    /***染厂*/
    private String dyeing;
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
    /**入库总量*/
    private double weight_in;
    /**操作人*/
    private String operator;
    /**操作时间*/
    private Date operatingTime;
    /**设备号*/
    private String device;
    /**标志位（默认为0；0为盘亏，1为盘盈，2为正常）*/
    private int flag=0;
    /***库存数量（这个可以不用管）*/
    private int countIn=0;
    /***实盘数量（这个可以不用管）*/
    private int countReal=0;
    /***盘盈数量（这个可以不用管）*/
    private int countProfit=0;
    /***盘亏数量（这个可以不用管）*/
    private int countLosses;
    /**
     * 勾选状态（默认选中；这个可以不用管）
     */
    private boolean status = true;
    private boolean isZero = false;
    /**增加库存数*/
    public void addCountIn(){
        countIn++;
    }
    /**增加实际盘点数*/
    public void addCountReal(){
        countReal++;
    }

    /**
     * 增加盘盈数
     */
    public void addCountProfit() {
        countProfit++;
    }

    /**
     * 增加盘亏数
     */
    public void addCountLosses() {
        countLosses++;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public boolean isZero() {
        return isZero;
    }

    public void setZero(boolean zero) {
        isZero = zero;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getDyeing() {
        return dyeing;
    }

    public void setDyeing(String dyeing) {
        this.dyeing = dyeing;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getCountIn() {
        return countIn;
    }

    public void setCountIn(int countIn) {
        this.countIn = countIn;
    }

    public int getCountReal() {
        return countReal;
    }

    public void setCountReal(int countReal) {
        this.countReal = countReal;
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
}
