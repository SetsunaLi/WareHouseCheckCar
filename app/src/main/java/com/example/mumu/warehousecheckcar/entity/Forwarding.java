package com.example.mumu.warehousecheckcar.entity;

public class Forwarding extends Cloth{
    /**申请单*/
    private String applyNo="";
/*
    *//**数量*//*
    private int count=0;*/

  /*  public void addCount(){
        count++;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }*/
    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Forwarding(String clothNum, String vatNo, String selNo, String color, String fabRool, Double weight, String epc) {
        setClothNum(clothNum);
        setVatNo(vatNo);
        setSelNo(selNo);
        setColor(color);
        setFabRool(fabRool);
        setWeight(weight);
        setEpc(epc);
    }

    public Forwarding(String clothNum, String vatNo, String selNo, String color, String fabRool, Double weight, String epc, String applyNo) {
        setClothNum(clothNum);
        setVatNo(vatNo);
        setSelNo(selNo);
        setColor(color);
        setFabRool(fabRool);
        setWeight(weight);
        setEpc(epc);
        this.applyNo = applyNo;
    }
}
