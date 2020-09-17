package com.example.mumu.warehousecheckcar.entity.forwarding;

/***
 *created by 发运列表
 *on 2020/5/20
 */
public class ForwardingListBean implements Cloneable {

    /**
     * id : 2
     * bas_transport_type : 0
     * code : CTO20200400001
     * transdate : -62135798400000
     * license_plate :
     * driver :
     * phone :
     * qty : 0
     * delivery_address :
     * output_status : 0
     * createbytype : 1
     * createby : 1
     * createdatetime : 1588139884000
     * modifybytype : 0
     * modifyby : 0
     * modifydatetime : 1588139884000
     * dataareaid : 0
     */

    private int id;
    private int bas_transport_type;
    private String code;
    private long transdate;
    private String license_plate;
    private String driver;
    private String phone;
    private String company;
    private int qty;
    private String delivery_address;
    private int output_status;
    private int status;
    private int createbytype;
    private int createby;
    private long createdatetime;
    private int modifybytype;
    private int modifyby;
    private long modifydatetime;
    private int dataareaid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBas_transport_type() {
        return bas_transport_type;
    }

    public void setBas_transport_type(int bas_transport_type) {
        this.bas_transport_type = bas_transport_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getTransdate() {
        return transdate;
    }

    public void setTransdate(long transdate) {
        this.transdate = transdate;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public int getOutput_status() {
        return output_status;
    }

    public void setOutput_status(int output_status) {
        this.output_status = output_status;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        ForwardingListBean object = (ForwardingListBean) super.clone();
        return object;
    }

}
