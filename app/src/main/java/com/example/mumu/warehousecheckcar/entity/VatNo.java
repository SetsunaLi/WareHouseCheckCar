package com.example.mumu.warehousecheckcar.entity;

/**
 * Created by mumu on 2018/12/9.
 * 缸号
 */

public class VatNo {
    /**申请单号*/
    private String applyNo;
    /**缸号*/
    private String batNo;
    /**数量*/
    private int count;


    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getBatNo() {
        return batNo;
    }

    public void setBatNo(String batNo) {
        this.batNo = batNo;
    }
}
