package com.example.mumu.warehousecheckcar.entity.cutCloth;

/***
 *created by 
 *on 2020/4/3
 */
public class ClothPlan {
    /**
     * id : 93565
     * outp_id : P0001351490
     * out_no : P00400721
     * product_no : JQ1103
     * sel_color : 1103-38
     * color_name : 桔红
     * yard_out : 1
     * l_price : 30
     * qty_kg : 0
     * p_ps : 0
     * f_price : 0
     * vat_no : A611-181205156
     * p_place : P剪A03A
     * note :
     * cust_po : JQ187716
     * in_no :
     * weight_kj : 0
     * weight_zg : 0
     * record_by : 杨靖
     * record_date : 1557392579000
     * prod_name_out :
     * cust_name :
     * prod_name :
     * color_code :
     * row : 0
     * statu :
     * productout_id : 53500
     * createbytype : 0
     * createby : 0
     * createdatetime : 1557392826000
     * modifybytype : 0
     * modifyby : 0
     * modifydatetime : 1557392826000
     * dataareaid : 0
     */

    private int id;
    private String outp_id;
    private String track_id;
    private String out_no;
    private String product_no;
    private String sel_color;
    private String color_name;
    private double yard_out;
    private int l_price;
    private int qty_kg;
    private int p_ps;
    private int f_price;
    private String vat_no;
    private String p_place;
    private String note;
    private String cust_po;
    private String in_no;
    private int weight_kj;
    private int weight_zg;
    private String record_by;
    private long record_date;
    private String prod_name_out;
    private String cust_name;
    private String prod_name;
    private String color_code;
    private int row;
    private String statu;
    private int productout_id;
    private int createbytype;
    private int createby;
    private long createdatetime;
    private int modifybytype;
    private int modifyby;
    private long modifydatetime;
    private int dataareaid;
    private boolean isUpLoad = true;

    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public boolean isUpLoad() {
        return isUpLoad;
    }

    public void setUpLoad(boolean upLoad) {
        isUpLoad = upLoad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getYard_out() {
        return yard_out;
    }

    public void setYard_out(double yard_out) {
        this.yard_out = yard_out;
    }

    public int getL_price() {
        return l_price;
    }

    public void setL_price(int l_price) {
        this.l_price = l_price;
    }

    public int getQty_kg() {
        return qty_kg;
    }

    public void setQty_kg(int qty_kg) {
        this.qty_kg = qty_kg;
    }

    public int getP_ps() {
        return p_ps;
    }

    public void setP_ps(int p_ps) {
        this.p_ps = p_ps;
    }

    public int getF_price() {
        return f_price;
    }

    public void setF_price(int f_price) {
        this.f_price = f_price;
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }

    public String getP_place() {
        return p_place;
    }

    public void setP_place(String p_place) {
        this.p_place = p_place;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCust_po() {
        return cust_po;
    }

    public void setCust_po(String cust_po) {
        this.cust_po = cust_po;
    }

    public String getIn_no() {
        return in_no;
    }

    public void setIn_no(String in_no) {
        this.in_no = in_no;
    }

    public int getWeight_kj() {
        return weight_kj;
    }

    public void setWeight_kj(int weight_kj) {
        this.weight_kj = weight_kj;
    }

    public int getWeight_zg() {
        return weight_zg;
    }

    public void setWeight_zg(int weight_zg) {
        this.weight_zg = weight_zg;
    }

    public String getRecord_by() {
        return record_by;
    }

    public void setRecord_by(String record_by) {
        this.record_by = record_by;
    }

    public long getRecord_date() {
        return record_date;
    }

    public void setRecord_date(long record_date) {
        this.record_date = record_date;
    }

    public String getProd_name_out() {
        return prod_name_out;
    }

    public void setProd_name_out(String prod_name_out) {
        this.prod_name_out = prod_name_out;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public int getProductout_id() {
        return productout_id;
    }

    public void setProductout_id(int productout_id) {
        this.productout_id = productout_id;
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
}
