package com.example.mumu.warehousecheckcar.UHF;

import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

/**
 * Created by mumu on 2018/12/8.
 */

public interface UHFCallbackLiatener {
    void refreshSettingCallBack(ReaderSetting readerSetting);
    void onInventoryTagCallBack(RXInventoryTag tag);
    void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd);
    void onOperationTagCallBack(RXOperationTag tag);
}
