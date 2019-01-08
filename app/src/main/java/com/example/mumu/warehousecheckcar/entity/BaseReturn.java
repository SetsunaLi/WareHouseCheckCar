package com.example.mumu.warehousecheckcar.entity;

/**
 * Created by mumu on 2019/1/8.
 */

public class BaseReturn {
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
