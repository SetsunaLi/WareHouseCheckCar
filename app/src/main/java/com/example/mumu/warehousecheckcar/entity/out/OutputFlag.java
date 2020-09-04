package com.example.mumu.warehousecheckcar.entity.out;

import java.io.Serializable;

/***
 *created by ${mumu}
 *on 2019/10/8
 */
public class OutputFlag implements Serializable {/**
 * 是否扫描
 */
private boolean isFind = false;
    /**
     * No申请单号
     */
    private String applyNo = "";
    /**
     * 是否正常
     */
    private boolean status = true;

    /**
     * 重量（指的是库存重量）
     */
    private double weight = 0.0;


    public OutputFlag(boolean isFind, String applyNo, boolean status, double weight) {
        this.isFind = isFind;
        this.applyNo = applyNo;
        this.status = status;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isFind() {
        return isFind;
    }

    public void setFind(boolean find) {
        isFind = find;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
