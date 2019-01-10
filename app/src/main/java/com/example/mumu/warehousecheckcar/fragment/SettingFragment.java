package com.example.mumu.warehousecheckcar.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.config.Config;
import com.example.mumu.warehousecheckcar.view.SeekBarPreferenceVolume;
import com.github.nkzawa.socketio.client.On;
import com.rfid.RFIDReaderHelper;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by mumu on 2018/11/22.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, UHFCallbackLiatener
,Preference.OnPreferenceClickListener{
    private SettingFragment() {
    }

    private static SettingFragment fragment;

    public static SettingFragment newInstance() {
        if (fragment == null) ;
        fragment = new SettingFragment();
        return fragment;
    }

    private EditTextPreference userName, userId, systemVersion, systemIP, systemPort, deviceNumber,prowerText;
    private SwitchPreference music, logcat;
    private SeekBarPreferenceVolume prower, workTime, intervalTime;
    private ListPreference statuslist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActivity().setTitle("设置");
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.setting_preference);
        userName = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.user_name_key));
        userId = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.user_id_key));
        systemVersion = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_version_key));
//        setEditTextPre(systemVersion,App.SYSTEM_VERSION);
        systemIP = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_ip_key));
//        setEditTextPre(systemIP,App.IP);
        systemPort = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_port_key));
//        setEditTextPre(systemPort,App.PORT);
        deviceNumber = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_device_number_key));
//        setEditTextPre(deviceNumber,App.DEVICE_NO);
        music = (SwitchPreference) getPreferenceScreen().findPreference(getString(R.string.system_music_key));
        logcat = (SwitchPreference) getPreferenceScreen().findPreference(getString(R.string.logcat_ket));
        prower = (SeekBarPreferenceVolume) getPreferenceScreen().findPreference(getString(R.string.device_prower_key));
        prowerText=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.prower_edit_key));
        prowerText.setOnPreferenceClickListener(this);
//        这个不生效
        prower.setProgress(App.PROWER);
     /*   workTime=(SeekBarPreferenceVolume)getPreferenceScreen().findPreference(getString(R.string.device_work_time_key));
        intervalTime=(SeekBarPreferenceVolume)getPreferenceScreen().findPreference(getString(R.string.device_interval_time_key));*/
        statuslist = (ListPreference) getPreferenceScreen().findPreference(getString(R.string.user_status_key));
        initView();
        getRFID();
        UHFResult.getInstance().setCallbackLiatener(this);
    }
    boolean flag=false;
    private RFIDReaderHelper rfidHander;

    private void getRFID() {
        try {
            rfidHander = RFID_2DHander.getInstance().getRFIDReader();
            RFID_2DHander.getInstance().on_RFID();
            rfidHander.getOutputPower(RFID_2DHander.getInstance().btReadId);
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        setEditTextPre(systemVersion);
        setEditTextPre(systemIP);
        setEditTextPre(systemPort);
        setEditTextPre(deviceNumber);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Config.USER_NAME_KEY:
                setEditTextPre(userName);
                break;
            case Config.USER_STATUS_KEY:
                setListPre(statuslist);
                break;
            case Config.USER_ID_KEY:
                setEditTextPre(userId);
                break;
            case Config.VERSION_KEY:
                setEditTextPre(systemVersion);
                App.SYSTEM_VERSION=systemVersion.getText();

                break;
            case Config.IP_KEY:
                setEditTextPre(systemIP);
                App.IP=systemIP.getText();
                break;
            case Config.PORT_KEY:
                setEditTextPre(systemPort);
                App.PORT=systemIP.getText();
                break;
            case Config.DEVICE_NUMBER_KEY:
                setEditTextPre(deviceNumber);
                App.DEVICE_NO=deviceNumber.getText();
                break;
            case Config.USER_PROWER_KEY:
                App.PROWER=getPrower(getActivity());
                byte p = (byte) App.PROWER;
                if (rfidHander != null&&p!=0) {
                    int i = rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId, p);
                    if (i != 0)
                        Toast.makeText(getActivity(), "设置功率失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case Config.MUSIC:
                App.MUSIC_SWITCH = music.isChecked();
                break;
            case Config.LOGCAT:
                App.LOGCAT_SWITCH = logcat.isChecked();
                break;
        }
    }
       /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //    设置EditTextPreference样式
    public void setEditTextPre(EditTextPreference editText) {
        editText.setSummary(editText.getText());
        editText.setText(editText.getText());
    }

    //    动态设置
    public void setEditTextPre(EditTextPreference editText, String str) {
        editText.setSummary(str);
        editText.setText(str);
    }

    //    设置ListPreference样式
    public void setListPre(ListPreference list) {
        list.setSummary(list.getValue());
    }

    @Override
    public void onResume() {
        super.onResume();
//        绑定监听
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
//        解绑
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RFID_2DHander.getInstance().off_RFID();
    }

    /**
     * 获取用户名
     */
    public static String getUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Config.USER_NAME_KEY, "");
    }

    /**
     * 获取是否推送
     */
    public static boolean getIsPush(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Config.USER_PUSH_KEY, true);
    }

    /**
     * 获取功率
     */
    public static int getPrower(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Config.USER_PROWER_KEY, 12);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ReaderSetting obj=(ReaderSetting)msg.obj;
            setEditTextPre(prowerText,obj.btAryOutputPower[0]+"");
        }
    };
    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {
        Log.i("Setting", "refreshSettingCallBack");
        if (flag) {
            Message msg = handler.obtainMessage();
            msg.obj = readerSetting;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onInventoryTagCallBack(RXInventoryTag tag) {

    }

    @Override
    public void onInventoryTagEndCallBack(RXInventoryTag.RXInventoryTagEnd tagEnd) {

    }

    @Override
    public void onOperationTagCallBack(RXOperationTag tag) {

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (rfidHander != null)
            rfidHander.getOutputPower(RFID_2DHander.getInstance().btReadId);
        flag=true;
        return false;
    }
   /* *//**
     * 获取时间*//*
    public static int getWorkTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Config.WORK_TIME_KEY,15);
    }

    *//**
     * 获取时间*//*
    public static int getIntervalTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Config.INTERVAL_TIME_KEY,15);
    }*/
}
