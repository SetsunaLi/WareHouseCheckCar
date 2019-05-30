package com.example.mumu.warehousecheckcar.entity;

public class BarCode extends Cloth{
    /***颜色*/
    private String colorName = "";
    /***色号*/
    private String selColor = "";
    /***配货信息编号*/
    private String outp_id ="";
    /***出仓单号*/
    private String out_no ="";
    /**剪板长度*/
    private double yard_out =0.0;
    /**申请条数*/
    private int p_ps=0;
    /**剪板重量*/
    private double cutWeight = 0.0;

    private boolean isFlag=false;

    public int getP_ps() {
        return p_ps;
    }

    public void setP_ps(int p_ps) {
        this.p_ps = p_ps;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getSelColor() {
        return selColor;
    }

    public void setSelColor(String selColor) {
        this.selColor = selColor;
    }

    public double getCutWeight() {
        return cutWeight;
    }

    public void setCutWeight(double cutWeight) {
        this.cutWeight = cutWeight;
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

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public double getYard_out() {
        return yard_out;
    }

    public void setYard_out(double yard_out) {
        this.yard_out = yard_out;
    }
}
