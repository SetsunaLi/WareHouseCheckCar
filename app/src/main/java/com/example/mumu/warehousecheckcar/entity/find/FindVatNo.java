package com.example.mumu.warehousecheckcar.entity.find;

/**
 * Created by mumu on 2019/1/26.
 */

public class FindVatNo {
    /***布号*/
    private String cloth_name;
    /***缸号*/
    private String vat_no;
    /***布票号*/
    private String inv_serial;
    /***仓位号*/
    private String location_name;
    /***库存重量*/
    private String weight_inv;
    /***EPC*/
    private String epc;
    /***颜色*/
    private String color_name;
    /***托盘号*/
    private String pallet_name;

    public String getCloth_name() {
        return cloth_name;
    }

    public void setCloth_name(String cloth_name) {
        this.cloth_name = cloth_name;
    }

    public String getInv_serial() {
        return inv_serial;
    }

    public void setInv_serial(String inv_serial) {
        this.inv_serial = inv_serial;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getWeight_inv() {
        return weight_inv;
    }

    public void setWeight_inv(String weight_inv) {
        this.weight_inv = weight_inv;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getPallet_name() {
        return pallet_name;
    }

    public void setPallet_name(String pallet_name) {
        this.pallet_name = pallet_name;
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }
}
