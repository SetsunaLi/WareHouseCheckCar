package com.example.mumu.warehousecheckcar.LDBE_UHF;

/***
 *created by mumu
 *on 2019/11/5
 */
public interface OnRfidResult {
    /**
     * Rfid扫码
     *
     * @param epc epc
     */
    void rfidResult(String epc);
}
