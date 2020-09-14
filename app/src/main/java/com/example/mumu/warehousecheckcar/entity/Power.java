package com.example.mumu.warehousecheckcar.entity;

/***
 *created by 
 *on 2020/4/17
 */
public class Power {
    /**
     * auth_name : 盘点
     * auth_type : 0
     * code : AUTH20191200003
     * createby : 1
     * createbytype : 1
     * createdatetime : 1575863025000
     * dataareaid : 0
     * description : 盘点操作
     * flag : 1
     * id : 3
     * modifyby : 1
     * modifybytype : 1
     * modifydatetime : 1575863197000
     */

    private String auth_name;
    private int auth_type;
    private String code;
    private String description;
    private int flag;
    private long modifydatetime;

    public String getAuth_name() {
        return auth_name;
    }

    public void setAuth_name(String auth_name) {
        this.auth_name = auth_name;
    }

    public int getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(int auth_type) {
        this.auth_type = auth_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getModifydatetime() {
        return modifydatetime;
    }

    public void setModifydatetime(long modifydatetime) {
        this.modifydatetime = modifydatetime;
    }
}
