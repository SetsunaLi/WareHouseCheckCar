package com.example.mumu.warehousecheckcar.LDBE_UHF;

import com.module.interaction.ModuleConnector;
import com.nativec.tools.ModuleManager;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.rxobserver.ReaderSetting;
import com.xdl2d.scanner.TDScannerConnector;
import com.xdl2d.scanner.TDScannerHelper;

/**
 * Created by mumu on 2018/12/3.
 */

public class RFID_2DHander {
    public final byte btReadId = (byte) 0xFF;
    //    默认每个标签只读一次
    public final byte btRepeat = (byte) 0x01;

    private static RFID_2DHander rfidHander;

    private RFID_2DHander() {
    }

    public static RFID_2DHander getInstance() {
        if (rfidHander == null)
            rfidHander = new RFID_2DHander();
        return rfidHander;
    }

    private RFIDReaderHelper rfidReaderHelper;
    private ModuleConnector connectRFID;
    private TDScannerConnector Connector2D;

    private ReaderSetting m_curReaderSetting;

    public ReaderSetting getM_curReaderSetting() {
        if (m_curReaderSetting == null)
            m_curReaderSetting = ReaderSetting.newInstance();
        return m_curReaderSetting;
    }

    /**
     * RFID模块上电
     */
    public boolean on_RFID() {
        return ModuleManager.newInstance().setUHFStatus(true);
    }

    /**
     * RFID模块掉电
     */
    public boolean off_RFID() {
        return ModuleManager.newInstance().setUHFStatus(false);
    }

    /**
     * 释放模块上电掉电控制设备，退出应用的时候必须调用该方法，
     */
    public boolean releaseRFID() {
        return ModuleManager.newInstance().release();
    }

    /**
     * 连接RFID读写器
     */
    public boolean connectReader() {
        if (connectRFID == null)
            connectRFID = new ReaderConnector();
        if (!connectRFID.isConnected())
            //连接指定串口，返回true表示成功，false失败
            return connectRFID.connectCom("dev/ttyS4", 115200);
        return true;
    }

    /**
     * 断开RFID读写器
     * true为断开连接
     * 否则为失败，有可能ModuleConnector为空
     */
    public boolean disConnectReader() {
        if (connectRFID != null && connectRFID.isConnected()) {
            connectRFID.disConnect();
            return true;
        }
        return false;
    }

    /**
     * 获取RFID控制器
     */
    public RFIDReaderHelper getRFIDReader() throws Exception {
        if (rfidReaderHelper == null)
            rfidReaderHelper = RFIDReaderHelper.getDefaultHelper();
        return rfidReaderHelper;
    }

    /**
     * 2D模块上电
     */
    public boolean on_2D() {
        return ModuleManager.newInstance().setScanStatus(true);
    }

    /**
     * 2D模块断电
     */
    public boolean off_2D() {
        return ModuleManager.newInstance().setScanStatus(false);
    }

    /**
     * 连接2D模块
     * 开发文档提示使用"dev/ttyS4",115200
     * 开发Demo使用"dev/ttyS1", 9600
     */
    public boolean connect2D() {
        if (Connector2D == null)
            Connector2D = new TDScannerConnector();
//        if (!Connector2D.isConnected())
        //连接指定串口，返回true表示成功，false失败
        return Connector2D.connectCom("dev/ttyS1", 9600);
//        return true;
    }

    /**
     * 断开2D模块
     * true为断开连接
     * 否则为失败，有可能ModuleConnector为空
     */
    public boolean disConnect2D() {
        if (Connector2D != null && Connector2D.isConnected()) {
            TDScannerHelper.getDefaultHelper().unRegisterObservers();
            Connector2D.disConnect();
            ModuleManager.newInstance().release();
            return true;
        }
        return false;
    }

    /**
     * 获取2D控制器
     */
    public TDScannerHelper getTDScanner() {
   /*     if (mScanner == null)
            mScanner = TDScannerHelper.getDefaultHelper();
        return mScanner;*/
        return TDScannerHelper.getDefaultHelper();
    }

    /**
     * 主动寻读
     */
    public void customizedSessionTargetInventory(byte btReadId) {
        if (rfidReaderHelper != null) {
            int mPos1 = 1;
            byte mBtSession = (byte) (mPos1 & 0xFF);
            int mPos2 = 0;
            byte mBtTarget = (byte) (mPos2 & 0xFF);
            rfidReaderHelper.customizedSessionTargetInventory(btReadId, mBtSession, mBtTarget, btRepeat);
        }
    }

    /**
     * 保证读写器连接且上电
     **/
    public void guaranteeConnect() {
        if (connectRFID == null)
            connectRFID = new ReaderConnector();
        if (!connectRFID.isConnected()) {
            //连接指定串口，返回true表示成功，false失败
            connectRFID.connectCom("dev/ttyS4", 115200);
            on_RFID();
        }
    }

    public void realTimeInventory(byte btReadId) {
        if (rfidReaderHelper != null) {
            rfidReaderHelper.realTimeInventory(btReadId, btRepeat);
        }
    }
}
