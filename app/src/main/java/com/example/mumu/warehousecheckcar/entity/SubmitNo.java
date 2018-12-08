package com.example.mumu.warehousecheckcar.entity;

/**
 * Created by mumu on 2018/12/8.
 * 提交申请单
 */

public class SubmitNo {
    /***申请单号*/
    private String applyNO;
    /**EPC码集合*/
    private String[] epcArry;

    public String getApplyNO() {
        return applyNO;
    }

    public void setApplyNO(String applyNO) {
        this.applyNO = applyNO;
    }

    public String[] getEpcArry() {
        return epcArry;
    }

    public void setEpcArry(String[] epcArry) {
        this.epcArry = epcArry;
    }
}
