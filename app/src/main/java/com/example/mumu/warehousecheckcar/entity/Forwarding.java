package com.example.mumu.warehousecheckcar.entity;

public class Forwarding extends Cloth{
    /**申请单*/
    private String applyNo="";

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**数量*/
    private int count=0;

    public void addCount(){
        count++;
    }
    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }


}
