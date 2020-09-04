package com.example.mumu.warehousecheckcar.entity.in;

/**
 * 退库入仓实体
 *
 * @author chenshengjin
 * @date 2019/9/23
 */
public class Inquiring {
    private String vat_no;
    private String product_no;
    private String sel_color;
    private String color_name;
    private String company;
    private Integer ps;
    private Double out_qty;

    @Override
    public String toString() {
        return "InQuiting{" +
                "vat_no='" + vat_no + '\'' +
                ", product_no='" + product_no + '\'' +
                ", sel_color='" + sel_color + '\'' +
                ", color_name='" + color_name + '\'' +
                ", company='" + company + '\'' +
                ", ps=" + ps +
                ", out_qty=" + out_qty +
                '}';
    }

    public Double getOut_qty() {
        return out_qty;
    }

    public void setOut_qty(Double out_qty) {
        this.out_qty = out_qty;
    }

    public Inquiring() {
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }

    public String getProduct_no() {
        return product_no;
    }

    public void setProduct_no(String product_no) {
        this.product_no = product_no;
    }

    public String getSel_color() {
        return sel_color;
    }

    public void setSel_color(String sel_color) {
        this.sel_color = sel_color;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getPs() {
        return ps;
    }

    public void setPs(Integer ps) {
        this.ps = ps;
    }
}
