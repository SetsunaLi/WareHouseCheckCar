package com.example.mumu.warehousecheckcar.entity;

import java.util.List;

/***
 *created by 
 *on 2020/4/3
 */
public class BaseReturnObject<T> {
    /**
     * status : 1
     * message : 查找成功
     */

    private int status;
    private String message;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
