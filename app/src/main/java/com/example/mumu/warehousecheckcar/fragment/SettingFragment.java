package com.example.mumu.warehousecheckcar.fragment;

import android.annotation.SuppressLint;
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

import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.LDBE_UHF.RFID_2DHander;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFCallbackLiatener;
import com.example.mumu.warehousecheckcar.LDBE_UHF.UHFResult;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.config.Config;
import com.example.mumu.warehousecheckcar.view.SeekBarPreferenceVolume;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.mumu.warehousecheckcar.App.PROWER;
import static com.example.mumu.warehousecheckcar.utils.AppUtil.showToast;


/**
 * Created by mumu on 2018/11/22.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, UHFCallbackLiatener
,Preference.OnPreferenceClickListener{


    private static SettingFragment fragment;

    public static SettingFragment newInstance() {
        if (fragment == null) ;
        fragment = new SettingFragment();
        return fragment;
    }

    private EditTextPreference userName, userId, systemVersion, systemIP, systemPort, deviceNumber, prowerText;
    private SwitchPreference music, logcat;
    private SeekBarPreferenceVolume prower;
    private ListPreference statuslist;

    boolean connectSuccess = false;
    boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("设置");
        addPreferencesFromResource(R.xml.setting_preference);
        userName = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.user_name_key));
        userId = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.user_id_key));
        systemVersion = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_version_key));
        systemIP = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_ip_key));
        setEditTextPre(systemIP,App.IP);
        systemPort = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_port_key));
        setEditTextPre(systemPort,App.PORT);
        deviceNumber = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.system_device_number_key));
        music = (SwitchPreference) getPreferenceScreen().findPreference(getString(R.string.system_music_key));
        logcat = (SwitchPreference) getPreferenceScreen().findPreference(getString(R.string.logcat_ket));
        prower = (SeekBarPreferenceVolume) getPreferenceScreen().findPreference(getString(R.string.device_prower_key));
        prowerText = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.prower_edit_key));
        prowerText.setOnPreferenceClickListener(this);
        prower.setProgress(PROWER);
        statuslist = (ListPreference) getPreferenceScreen().findPreference(getString(R.string.user_status_key));
        initView();
        initRFID();
        UHFResult.getInstance().setCallbackLiatener(this);
    }

    /*  private RFIDReaderHelper rfidHander;

      private void getRFID() {
          try {
              rfidHander = RFID_2DHander.getInstance().getRFIDReader();
              RFID_2DHander.getInstance().on_RFID();
              flag=true;
          } catch (Exception e) {
              e.printStackTrace();
          }
      }*/
    private void initRFID() {
        if (!PdaController.initRFID(this)) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
        } else {
            flag = true;
            connectSuccess = true;
        }
    }

    private void disRFID() {
        if (!PdaController.disRFID()) {
            showToast(getResources().getString(R.string.hint_rfid_mistake));
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
                App.PORT = systemIP.getText();
                break;
            case Config.DEVICE_NUMBER_KEY:
                setEditTextPre(deviceNumber);
                App.DEVICE_NO = deviceNumber.getText();
                break;
            case Config.USER_PROWER_KEY:
                PROWER = getPrower(getActivity());
                byte p = (byte) PROWER;
                if (PdaController.getRfidHandler() != null && p != 0 && connectSuccess) {
                    int i = PdaController.getRfidHandler().setOutputPower(RFID_2DHander.getInstance().btReadId, p);
                    if (i != 0)
                        Toast.makeText(getActivity(), "设置功率失败", Toast.LENGTH_SHORT).show();
                    else
                        flag = true;
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
        disRFID();
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

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ReaderSetting obj=(ReaderSetting)msg.obj;
            if (obj.btAryOutputPower!=null&&obj.btAryOutputPower.length>0)
                setEditTextPre(prowerText,obj.btAryOutputPower[0]+"");
        }
    };
    @Override
    public void refreshSettingCallBack(ReaderSetting readerSetting) {
        Log.i("Setting", "refreshSettingCallBack");
        if (flag&&readerSetting!=null) {
            flag=false;
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
        if (PdaController.getRfidHandler() != null && connectSuccess)
            PdaController.getRfidHandler().getOutputPower(RFID_2DHander.getInstance().btReadId);
        flag = true;
        return false;
    }
}
