package com.example.mumu.warehousecheckcar.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.view.View;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.UHF.UHFResult;
import com.example.mumu.warehousecheckcar.config.Config;
import com.example.mumu.warehousecheckcar.view.SeekBarPreferenceVolume;
import com.rfid.RFIDReaderHelper;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import butterknife.ButterKnife;


/**
 * Created by mumu on 2018/11/22.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,UHFCallbackLiatener{
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }
    private EditTextPreference userName,userId,systemVersion,systemIP,systemPort,systemNumber;
    private SwitchPreference push;
    private SeekBarPreferenceVolume prower,workTime,intervalTime;
    private ListPreference statuslist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.setting_preference);
        userName=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.user_name_key));
        userId=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.user_id_key));
        systemVersion=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.system_version_key));
        systemIP=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.system_ip_key));
        systemPort=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.system_port_key));
        systemNumber=(EditTextPreference)getPreferenceScreen().findPreference(getString(R.string.system_device_number_key));
        push=(SwitchPreference)getPreferenceScreen().findPreference(getString(R.string.system_push_key));
        prower=(SeekBarPreferenceVolume)getPreferenceScreen().findPreference(getString(R.string.device_prower_key));
     /*   workTime=(SeekBarPreferenceVolume)getPreferenceScreen().findPreference(getString(R.string.device_work_time_key));
        intervalTime=(SeekBarPreferenceVolume)getPreferenceScreen().findPreference(getString(R.string.device_interval_time_key));*/
        statuslist=(ListPreference)getPreferenceScreen().findPreference(getString(R.string.user_status_key));

        getRFID();
        UHFResult.getInstance().setCallbackLiatener(this);
    }
    private RFIDReaderHelper rfidHander;
    private void getRFID(){
        try {
            rfidHander=RFID_2DHander.getInstance().getRFIDReader();
            RFID_2DHander.getInstance().on_RFID();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
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
                break;
            case Config.IP_KEY:
                setEditTextPre(systemIP);
                break;
            case Config.PORT_KEY:
                setEditTextPre(systemPort);
                break;
            case Config.NUMBER_KEY:
                setEditTextPre(systemNumber);
                break;
            case Config.USER_PROWER_KEY:
                byte prower=(byte)getPrower(getActivity());
                if (rfidHander!=null){
                    rfidHander.setOutputPower(RFID_2DHander.getInstance().btReadId,prower);
                }
                break;
        }
    }
//    设置EditTextPreference样式
    public void setEditTextPre(EditTextPreference editText){
        editText.setSummary(editText.getText());
        editText.setText(editText.getText());
    }
    //    设置ListPreference样式
    public void setListPre(ListPreference list){
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
/**
 * 获取用户名*/
    public static String getUsername(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Config.USER_NAME_KEY,"");
    }
    /**
     * 获取是否推送*/
    public static boolean getIsPush(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Config.USER_PUSH_KEY,true);
    }
    /**
     * 获取功率*/
    public static int getPrower(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Config.USER_PROWER_KEY,12);
    }

    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {
        Log.i("Setting","refreshSettingCallBack");
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
