package com.example.mumu.warehousecheckcar.entity.car;

public class Coadjutant {
    /**描述*/
    private String description;
    /**账号*/
    private String account_code;
    /**用户名*/
    private String username;
    /**id*/
    private int id;

    public Coadjutant() {
    }

    public Coadjutant(String description, String account_code, String username, int id) {
        this.description = description;
        this.account_code = account_code;
        this.username = username;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccount_code() {
        return account_code;
    }

    public void setAccount_code(String account_code) {
        this.account_code = account_code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
