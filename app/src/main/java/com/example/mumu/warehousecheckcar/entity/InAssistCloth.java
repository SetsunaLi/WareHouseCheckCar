package com.example.mumu.warehousecheckcar.entity;

/***
 *created by ${mumu}
 *on 2019/9/19
 */
public class InAssistCloth  {
    private String bas_batch_name ="";
    private String inv_serial="";
    /***原托盘号**/
    private String suggest_pallet = "";
    /***原库位号**/
    private String suggest_location = "";
    private int qtys;
    private String epc;

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getBas_batch_name() {
        return bas_batch_name;
    }

    public void setBas_batch_name(String bas_batch_name) {
        this.bas_batch_name = bas_batch_name;
    }

    public String getInv_serial() {
        return inv_serial;
    }

    public void setInv_serial(String inv_serial) {
        this.inv_serial = inv_serial;
    }

    public String getSuggest_pallet() {
        return suggest_pallet;
    }

    public void setSuggest_pallet(String suggest_pallet) {
        this.suggest_pallet = suggest_pallet;
    }

    public String getSuggest_location() {
        return suggest_location;
    }

    public void setSuggest_location(String suggest_location) {
        this.suggest_location = suggest_location;
    }

    public int getQtys() {
        return qtys;
    }

    public void setQtys(int qtys) {
        this.qtys = qtys;
    }
}
