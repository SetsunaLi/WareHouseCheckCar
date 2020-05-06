package com.example.mumu.warehousecheckcar.entity;

import com.google.gson.annotations.SerializedName;

/***
 *created by 查布接收详情
 *on 2020/5/6
 */
public class ChubbGetCloth extends Cloth {

    /**
     * id : 271361
     * location_name : PI18C
     * pallet_name : TP0560
     * color_name : 1103-51
     * cloth_name : JQ1103
     * vat_no : 123
     * inv_serial : 003
     * qty : 1
     * weight_inv : 24.7
     * weight_in : 24.7
     */

    private int id;
    private String location_name;
    private String pallet_name;
    private String color_name;
    private String cloth_name;
    private String vat_no;
    private String inv_serial;
    private int qty;
    private double weight_inv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getPallet_name() {
        return pallet_name;
    }

    public void setPallet_name(String pallet_name) {
        this.pallet_name = pallet_name;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getCloth_name() {
        return cloth_name;
    }

    public void setCloth_name(String cloth_name) {
        this.cloth_name = cloth_name;
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }

    public String getInv_serial() {
        return inv_serial;
    }

    public void setInv_serial(String inv_serial) {
        this.inv_serial = inv_serial;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getWeight_inv() {
        return weight_inv;
    }

    public void setWeight_inv(double weight_inv) {
        this.weight_inv = weight_inv;
    }
}
