package com.example.mumu.warehousecheckcar.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 退布实体
 *
 * @author chenshengjin
 * @date 2019/9/23
 */
public class RetIn extends Inquiring implements Serializable ,Cloneable{
        private String sh_no;

        private String company1;
        private String record_by;
        private String record_date;
        private List<RetInd> ind;

        private boolean status=false;
        private Integer scane_qty=0;
    @Override
    public String toString() {
        return "RetIn{" +
                "sh_no='" + sh_no + '\'' +
                ", company1='" + company1 + '\'' +
                ", vat_no='" + super.getVat_no()+ '\'' +
                ", sel_color='" + super.getSel_color() + '\'' +
                ", product_no='" + super.getProduct_no() + '\'' +
                ", color_name='" + super.getColor_name() + '\'' +
                ", record_by='" + record_by + '\'' +
                ", record_date='" + record_date + '\'' +
                ", ind=" + ind +
                '}';
    }

    public Integer getScane_qty() {
        return scane_qty;
    }

    public void setScane_qty(Integer scane_qty) {
        this.scane_qty = scane_qty;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSh_no() {
        return sh_no;
    }

    public void setSh_no(String sh_no) {
        this.sh_no = sh_no;
    }

    public String getCompany1() {
        return company1;
    }

    public void setCompany1(String company1) {
        this.company1 = company1;
    }

    public String getRecord_by() {
        return record_by;
    }

    public void setRecord_by(String record_by) {
        this.record_by = record_by;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public List<RetInd> getInd() {
        return ind;
    }

    public void setInd(List<RetInd> ind) {
        this.ind = ind;
    }

    public RetIn() {
    }

    @Override
    public RetIn clone() {
        RetIn stu = null;
        try{
            stu = (RetIn)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
