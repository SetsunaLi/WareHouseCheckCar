package com.example.mumu.warehousecheckcar.entity;

public class CutSample extends Cloth {
    /**板布标识*/
    private String outp_id ="";
    /**申请单号*/
    private String applyNo="";
    /**剪板长度*/
    private double yard_out =0.0;
    /**剪板重量*/
    private double qty_kg=0.0;
    private boolean isFlag=false;


    public double getQty_kg() {
        return qty_kg;
    }
    public void setQty_kg(double qty_kg) {
        this.qty_kg = qty_kg;
    }
    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    public String getOutp_id() {
        return outp_id;
    }

    public void setOutp_id(String outp_id) {
        this.outp_id = outp_id;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public double getYard_out() {
        return yard_out;
    }

    public void setYard_out(double yard_out) {
        this.yard_out = yard_out;
    }
}
