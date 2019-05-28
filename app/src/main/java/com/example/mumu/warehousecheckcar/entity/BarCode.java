package com.example.mumu.warehousecheckcar.entity;

public class BarCode {
    /**布号*/
    private String product_no = "";

    /***缸号*/
    private String vatNo = "";

    /***颜色*/
    private String colorName = "";

    /***色号*/
    private String selColor = "";

    /***配货信息编号*/
    private String outp_id = "";

    /***出仓单号*/
    private String out_no = "";

    /***米长*/
    private double yard_out = 0.0;

    /***条数*/
    private double p_ps = 0.0;

    /**重量（指的是库存重量）*/
    private double weight = 0.0;

    public String getOutp_id() {
        return outp_id;
    }

    public void setOutp_id(String outp_id) {
        this.outp_id = outp_id;
    }

    public double getYard_out() {
        return yard_out;
    }

    public void setYard_out(double yard_out) {
        this.yard_out = yard_out;
    }

    public double getP_ps() {
        return p_ps;
    }

    public void setP_ps(double p_ps) {
        this.p_ps = p_ps;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
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
}
