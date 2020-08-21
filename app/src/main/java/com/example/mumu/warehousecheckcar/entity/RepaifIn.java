package com.example.mumu.warehousecheckcar.entity;

import android.annotation.SuppressLint;

/***
 *created by 
 *on 2020/8/20
 */
public class RepaifIn {
    /**
     * vat_no : 缸号
     * product_no : 布号
     * sel_color : 色号
     * weight_inv : 库存重量
     * fab_roll : 布票号
     * epc :
     * sh_no : 送货单号
     * fact_name : 染厂名称
     * fact_code : 染厂ID
     */

    private String vat_no = "";
    private String product_no = "";
    private String sel_color = "";
    @SuppressLint("weight_inv")
    private double weight;
    private String fab_roll = "";
    private String epc = "";
    private String sh_no = "";
    private String fact_name = "";
    private String fact_code = "";
    private int count;
    private boolean flag = true;
    /**
     * cust_po : 嘉谦PO
     * weight_f : peizhong
     * weight_kj : 空加
     * weight_zg : 纸管
     * place_name : 库位
     * shelf_name : 架位
     */

    private String cust_po = "";
    private String weight_f = "";
    private String weight_kj = "";
    private String weight_zg = "";
    private String place_name = "";
    private String shelf_name = "";


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void addCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getFab_roll() {
        return fab_roll;
    }

    public void setFab_roll(String fab_roll) {
        this.fab_roll = fab_roll;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getSh_no() {
        return sh_no;
    }

    public void setSh_no(String sh_no) {
        this.sh_no = sh_no;
    }

    public String getFact_name() {
        return fact_name;
    }

    public void setFact_name(String fact_name) {
        this.fact_name = fact_name;
    }

    public String getFact_code() {
        return fact_code;
    }

    public void setFact_code(String fact_code) {
        this.fact_code = fact_code;
    }

    public String getCust_po() {
        return cust_po;
    }

    public void setCust_po(String cust_po) {
        this.cust_po = cust_po;
    }

    public String getWeight_f() {
        return weight_f;
    }

    public void setWeight_f(String weight_f) {
        this.weight_f = weight_f;
    }

    public String getWeight_kj() {
        return weight_kj;
    }

    public void setWeight_kj(String weight_kj) {
        this.weight_kj = weight_kj;
    }

    public String getWeight_zg() {
        return weight_zg;
    }

    public void setWeight_zg(String weight_zg) {
        this.weight_zg = weight_zg;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getShelf_name() {
        return shelf_name;
    }

    public void setShelf_name(String shelf_name) {
        this.shelf_name = shelf_name;
    }
}
