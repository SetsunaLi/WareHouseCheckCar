package com.example.mumu.warehousecheckcar.entity;

/**
 * Created by mumu on 2018/12/08.
 * 申请单详细列表
 */

public class ApplicaFormEntity {
    /***申请单号*/
    private String applyNO;
    /***布号*/
    private String clothNo;
    /***色号*/
    private String colorNo;
    /***颜色*/
    private String color;
    /***缸号*/
    private String vatNo;
    /***申请条数*/
    private int applyCount;
    /**EPC码集合*/
    private String[] epcArry;

    public String getApplyNO() {
        return applyNO;
    }

    public void setApplyNO(String applyNO) {
        this.applyNO = applyNO;
    }

    public String getClothNo() {
        return clothNo;
    }

    public void setClothNo(String clothNo) {
        this.clothNo = clothNo;
    }

    public String getColorNo() {
        return colorNo;
    }

    public void setColorNo(String colorNo) {
        this.colorNo = colorNo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public int getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }

    public String[] getEpcArry() {
        return epcArry;
    }

    public void setEpcArry(String[] epcArry) {
        this.epcArry = epcArry;
    }
}
