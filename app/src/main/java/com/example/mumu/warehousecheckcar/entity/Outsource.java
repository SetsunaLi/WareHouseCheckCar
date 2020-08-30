package com.example.mumu.warehousecheckcar.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/***
 *created by 
 *on 2020/7/23
 */
public class Outsource implements Serializable {
    private boolean flag=true;
    private String id;
    private boolean isNewRecord;
    @SerializedName(value = "createDate", alternate = "transDate")
    private String createDate;
    private String createBy;
    private String updateDate;
    private String status;
    private String updateBy;
    private String transNo;
    private int isSend;
    private String epc;
    @SerializedName(value = "vat_no", alternate = "vatNo")
    private String vat_no;
    @SerializedName(value = "fab_roll", alternate = "fabRoll")
    private String fab_roll;
    @SerializedName(value = "product_no", alternate = "productNo")
    private String product_no;
    @SerializedName(value = "product_name", alternate = "productName")
    private String product_name;
    @SerializedName(value = "color_code", alternate = "colorCode")
    private String color_code;
    @SerializedName(value = "sel_color", alternate = "selColor")
    private String sel_color;
    @SerializedName(value = "color_name", alternate = "colorName")
    private String color_name;
    @SerializedName(value = "job_no", alternate = "jobNo")
    private String job_no;
    @SerializedName(value = "cust_po", alternate = "custPo")
    private String cust_po;
    private String gram;
    private String width;
    @SerializedName(value = "width_side", alternate = "widthSide")
    private String width_side;
    @SerializedName(value = "weight_f", alternate = "weightF")
    private double weight_f;
    private double weight;
    @SerializedName(value = "weight_kj", alternate = "weightKj")
    private double weight_kj;
    @SerializedName(value = "weight_zg", alternate = "weightZg")
    private double weight_zg;
    private String deliverNo;
    @SerializedName(value = "sup_name", alternate = "supName")
    private String sup_name;
    @SerializedName(value = "cust_name", alternate = "custName")
    private String cust_name;
    private String position;

    public Outsource() {
    }

    public Outsource(boolean flag, String transNo, String vat_no, String product_no, String product_name, String color_code, String sel_color, String color_name, String cust_po, String width_side, double weight_kj, double weight_zg, String deliverNo) {
        this.flag = flag;
        this.transNo = transNo;
        this.vat_no = vat_no;
        this.product_no = product_no;
        this.product_name = product_name;
        this.color_code = color_code;
        this.sel_color = sel_color;
        this.color_name = color_name;
        this.cust_po = cust_po;
        this.width_side = width_side;
        this.weight_kj = weight_kj;
        this.weight_zg = weight_zg;
        this.deliverNo = deliverNo;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getVatNo() {
        return vat_no;
    }

    public void setVatNo(String vat_no) {
        this.vat_no = vat_no;
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }

    public double getWeight_f() {
        return weight_f;
    }

    public void setWeight_f(double weight_f) {
        this.weight_f = weight_f;
    }

    public double getWeight_kj() {
        return weight_kj;
    }

    public void setWeight_kj(double weight_kj) {
        this.weight_kj = weight_kj;
    }

    public double getWeight_zg() {
        return weight_zg;
    }

    public void setWeight_zg(double weight_zg) {
        this.weight_zg = weight_zg;
    }

    public String getFab_roll() {
        return fab_roll;
    }

    public void setFab_roll(String fab_roll) {
        this.fab_roll = fab_roll;
    }

    public String getProduct_no() {
        return product_no;
    }

    public void setProduct_no(String product_no) {
        this.product_no = product_no;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
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

    public String getJob_no() {
        return job_no;
    }

    public void setJob_no(String job_no) {
        this.job_no = job_no;
    }

    public String getCust_po() {
        return cust_po;
    }

    public void setCust_po(String cust_po) {
        this.cust_po = cust_po;
    }

    public String getGram() {
        return gram;
    }

    public void setGram(String gram) {
        this.gram = gram;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth_side() {
        return width_side;
    }

    public void setWidth_side(String width_side) {
        this.width_side = width_side;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDeliverNo() {
        return deliverNo;
    }

    public void setDeliverNo(String deliverNo) {
        this.deliverNo = deliverNo;
    }

    public String getSup_name() {
        return sup_name;
    }

    public void setSup_name(String sup_name) {
        this.sup_name = sup_name;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
