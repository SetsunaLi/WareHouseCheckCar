package com.example.mumu.warehousecheckcar.entity;

public class UpdateBean {
    private String title;
    /**更新描述*/
    private String update_describe;
    /**版本号*/
    private String version_no;
    /**版本名称*/
    private String version_name;
    /**URL*/
    private String update_url;
    /**更新时间*/
    private String update_time;
    /**更新人*/
    private String operator;


    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdate_describe() {
        return update_describe;
    }

    public void setUpdate_describe(String update_describe) {
        this.update_describe = update_describe;
    }

    public String getVersion_no() {
        return version_no;
    }

    public void setVersion_no(String version_no) {
        this.version_no = version_no;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
