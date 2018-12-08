package com.example.mumu.warehousecheckcar.entity;

/**
 * Created by mumu on 2018/12/08.
 * 申请单
 */

public class ApplyNo {
    public ApplyNo(String applyNo){
        this.applyNo=applyNo;
    }
    /**
     * 申请单号
     */
    private String applyNo;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }
}
