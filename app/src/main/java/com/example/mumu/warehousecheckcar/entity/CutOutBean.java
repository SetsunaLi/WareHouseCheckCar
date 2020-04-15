package com.example.mumu.warehousecheckcar.entity;

/***
 *created by 
 *on 2020/4/8
 */
public class CutOutBean {

    /**
     * id : 49
     * cc_cut_out_id : 25
     * product_applypid : P0001351925
     * bas_tag_id : 293578
     * cut_weight : 5
     * code : COL20191200031
     * createbytype : 1
     * createby : 1
     * createdatetime : 1575535393000
     * modifybytype : 0
     * modifyby : 0
     * modifydatetime : 1575535393000
     * dataareaid : 0
     * epc : null
     * apply_no : null
     * product_applyp_model : {"id":94017,"outp_id":"P0001351925","out_no":"B90506409","product_no":"JQ5353","sel_color":"5353-15","color_name":"深彩兰","yard_out":0,"l_price":0,"qty_kg":5,"p_ps":0,"f_price":0,"vat_no":"181118002","p_place":"PI35E,","note":"","cust_po":"ZD20180412","in_no":"","weight_kj":0,"weight_zg":0,"record_by":"杨靖","record_date":1557401791000,"prod_name_out":"","cust_name":"","prod_name":"","color_code":"","row":0,"statu":"","productout_id":53751,"createbytype":0,"createby":0,"createdatetime":1557402033000,"modifybytype":0,"modifyby":0,"modifydatetime":1557402296000,"dataareaid":0}
     */

    private int id;
    private int cc_cut_out_id;
    private String product_applypid;
    private int bas_tag_id;
    private double cut_weight;
    private String code;
    private int createbytype;
    private int createby;
    private long createdatetime;
    private int modifybytype;
    private int modifyby;
    private long modifydatetime;
    private int dataareaid;
    private String epc;
    private String apply_no;
    private boolean isUpload = true;
    private BarCode product_applyp_model;

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCc_cut_out_id() {
        return cc_cut_out_id;
    }

    public void setCc_cut_out_id(int cc_cut_out_id) {
        this.cc_cut_out_id = cc_cut_out_id;
    }

    public String getProduct_applypid() {
        return product_applypid;
    }

    public void setProduct_applypid(String product_applypid) {
        this.product_applypid = product_applypid;
    }

    public int getBas_tag_id() {
        return bas_tag_id;
    }

    public void setBas_tag_id(int bas_tag_id) {
        this.bas_tag_id = bas_tag_id;
    }

    public double getCut_weight() {
        return cut_weight;
    }

    public void setCut_weight(double cut_weight) {
        this.cut_weight = cut_weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCreatebytype() {
        return createbytype;
    }

    public void setCreatebytype(int createbytype) {
        this.createbytype = createbytype;
    }

    public int getCreateby() {
        return createby;
    }

    public void setCreateby(int createby) {
        this.createby = createby;
    }

    public long getCreatedatetime() {
        return createdatetime;
    }

    public void setCreatedatetime(long createdatetime) {
        this.createdatetime = createdatetime;
    }

    public int getModifybytype() {
        return modifybytype;
    }

    public void setModifybytype(int modifybytype) {
        this.modifybytype = modifybytype;
    }

    public int getModifyby() {
        return modifyby;
    }

    public void setModifyby(int modifyby) {
        this.modifyby = modifyby;
    }

    public long getModifydatetime() {
        return modifydatetime;
    }

    public void setModifydatetime(long modifydatetime) {
        this.modifydatetime = modifydatetime;
    }

    public int getDataareaid() {
        return dataareaid;
    }

    public void setDataareaid(int dataareaid) {
        this.dataareaid = dataareaid;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getApply_no() {
        return apply_no;
    }

    public void setApply_no(String apply_no) {
        this.apply_no = apply_no;
    }

    public BarCode getProduct_applyp_model() {
        return product_applyp_model;
    }

    public void setProduct_applyp_model(BarCode product_applyp_model) {
        this.product_applyp_model = product_applyp_model;
    }

}
