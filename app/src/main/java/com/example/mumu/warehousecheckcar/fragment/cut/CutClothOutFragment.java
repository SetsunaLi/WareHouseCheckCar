package com.example.mumu.warehousecheckcar.fragment.cut;

import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.LDBE_UHF.OnCodeResult;
import com.example.mumu.warehousecheckcar.LDBE_UHF.PdaController;
import com.example.mumu.warehousecheckcar.LDBE_UHF.ScanResultHandler;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.CutOutBean;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.service.BluetoothLeService;
import com.example.mumu.warehousecheckcar.utils.AppLog;
import com.squareup.okhttp.Request;
import com.xdl2d.scanner.callback.RXCallback;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.application.App.TIME;
import static org.greenrobot.eventbus.EventBus.TAG;

/***
 *created by 剪布出库
 *on 2019/11/21
 */
public class CutClothOutFragment extends BaseFragment implements RXCallback, OnCodeResult, BRecyclerAdapter.OnItemClickListener {

    //蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    //蓝牙特征值
    private static BluetoothGattCharacteristic target_chara = null;
    @Bind(R.id.recyle)
    RecyclerView recyle;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.scrollView)
    HorizontalScrollView scrollView;
    @Bind(R.id.button3)
    Button button3;
    private ArrayList<CutOutBean> myList;
    private ScanResultHandler scanResultHandler;
    private ArrayList<String> noList;
    private String name;
    private String address;
    private boolean isBlueTooth;
    private boolean isConnect = false;
    private Boolean isGetWeight = false;
    private Double weight;
    private Handler delayHandler = new Handler();
    private UIHandler uiHandler;
    private BluetoothLeService bleService;
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Message message = new Message();
            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    //视图更新连接状态
                    message.what = 1;
                    uiHandler.sendMessage(message);
                    break;

                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    //视图更新断连状态
                    message.what = 2;
                    uiHandler.sendMessage(message);
                    break;

                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    //服务被发现
                    displayGattServices(bleService.getSupportedGattServices());
                    break;

                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    //有数据发过来
                    String data = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                    if (data.contains(".") && data.length() > 6 && isGetWeight) {//过滤条件
                        data = data.replace("=", "");
                        Double resultData = Double.valueOf(new StringBuilder(data).reverse().toString());
                        message.what = 3;
                        weight = (double) Math.round(resultData * 10) / 10;
                        uiHandler.sendMessage(message);
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private RecycleAdapter mAdapter;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BluetoothLeService.LocalBinder) service).getService();
            bleService.initialize();
            bleService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleService = null;
        }
    };

    public static CutClothOutFragment newInstance() {
        return new CutClothOutFragment();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cut_cloth_out_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        isBlueTooth = getArguments().getBoolean("isBlueTooth");
        if (isBlueTooth) {
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            init();
        }

        myList = new ArrayList();
        myList.add(new CutOutBean());
        noList = new ArrayList<>();
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.cut_cloth_out_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        scrollView.setFillViewport(true);
        text3.setText(String.valueOf(0));

    }

    @Override
    protected void addListener() {
        scanResultHandler = new ScanResultHandler(this);
        init2D();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        connectBluetooth();
    }

    private void init() {
        uiHandler = new UIHandler(this);
        Intent serviceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void clearData() {
        myList.clear();
        myList.add(new CutOutBean());
        noList.clear();
    }

    private void init2D() {
        if (!PdaController.init2D(this)) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    private void disConnect2D() {
        if (!PdaController.disConnect2D()) {
            showToast(getResources().getString(R.string.hint_2d_mistake));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        disConnect2D();
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                scanResultHandler.removeMessages(ScanResultHandler.CODE);
                clearData();
                onOffWeight(false);
                mAdapter.setPosition(-255);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.button2:
                showUploadDialog("是否确认出库？");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upLoad();
                    }
                });
                break;
            case R.id.button3:
                if (isBlueTooth)
                    if (isConnect)
                        onOffWeight(!isGetWeight);
                    else {
                        showToast("蓝牙已断开");
                        connectBluetooth();
                    }
                else
                    showToast("请返回界面连接蓝牙设备");
                break;
        }
    }

    private void onOffWeight(boolean flag) {
        if (flag) {
            isGetWeight = flag;
            button3.setText("拒收");
            showToast("打开蓝牙接收");
        } else {
            isGetWeight = flag;
            button3.setText("接收");
            showToast("关闭蓝牙接收");
        }
    }

    private void connectBluetooth() {
        if (isBlueTooth) {
            getActivity().registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
            if (bleService != null) {
                bleService.connect(address);
            }
        }
    }

    private void upLoad() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        ArrayList<CutOutBean> arrayList = new ArrayList<>();
        for (int i = 1; i < myList.size(); i++) {
            if (myList.get(i).isUpload()) {
                arrayList.add(myList.get(i));
            }
        }
        if (arrayList.size() > 0) {
            uploadDialog.lockView();
            scanResultHandler.postDelayed(r, TIME);
            jsonObject.put("data", arrayList);
            final String json = jsonObject.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/postCutOut", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "postInventory;" + e.getMessage());
                            Toast.makeText(getActivity(), "上传信息失败；" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(BaseReturn response) {
                        try {
                            AppLog.write(getActivity(), "cutout", "userId:" + User.newInstance().getId() + response.toString(), AppLog.TYPE_INFO);
                            uploadDialog.openView();
                            hideUploadDialog();
                            scanResultHandler.removeCallbacks(r);
                            if (response.getStatus() == 1) {
                                showToast("上传成功");
                                clearData();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                showToast("上传失败");
                                showConfirmDialog("上传失败，" + response.getMessage());
                                Sound.faillarm();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            showConfirmDialog("请至少上传一个有效单号");
    }

    @Override
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        if (!noList.contains(code)) {
            JSONObject obj = new JSONObject();
            obj.put("product_applypid", code);
            final String json = obj.toJSONString();
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/cutOut/getCutOutLineByOutpId", new OkHttpClientManager.ResultCallback<JSONObject>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (App.LOGCAT_SWITCH) {
                            Log.i(TAG, "getEpc;" + e.getMessage());
                            Toast.makeText(getActivity(), "扫描查布区布匹失败" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInteger("status") == 1) {
                                showToast(jsonObject.getString("message"));
                                CutOutBean cutOutBean = jsonObject.getObject("data", CutOutBean.class);
                                if (cutOutBean != null) {
                                    if (!noList.contains(cutOutBean.getProduct_applypid())) {
                                        noList.add(cutOutBean.getProduct_applypid());
                                        myList.add(cutOutBean);
                                        if (isBlueTooth & isConnect)
                                            onOffWeight(true);
                                        mAdapter.setPosition(myList.size() - 1);
                                        text3.setText(String.valueOf(myList.size() - 1));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                showConfirmDialog(jsonObject.getString("message"));
                            }
                        } catch (Exception e) {

                        }
                    }
                }, json);
            } catch (IOException e) {
                Log.i(TAG, "");
            }
        }
    }

    @Override
    public void callback(byte[] bytes) {
        Message msg = scanResultHandler.obtainMessage();
        msg.what = ScanResultHandler.CODE;
        msg.obj = new String(bytes);
        scanResultHandler.sendMessage(msg);
    }

    /**
     * @Description: TODO(处理蓝牙服务)这段不需要理解，是厂家那边定的，照搬就可以。
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        String uuid = null;
        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        // 特征数据(隶属于某一级服务下面的特征值集合)
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
        // 部分层次，所有特征值集合
//        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。
            gattServiceData.add(currentServiceData);
            Log.i("Service uuid", "Service uuid:" + uuid);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString().equals(HEART_RATE_MEASUREMENT)) {
                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    delayHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            bleService.readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    bleService.setCharacteristicNotification(gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    // mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }

                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    Log.i("descriptor UUID", "---descriptor UUID:" + descriptor.getUuid());
                    bleService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }
                gattCharacteristicGroupData.add(currentCharaData);
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
//            mGattCharacteristics.add(charas);
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (position != 0) {
            mAdapter.setPosition(position);
            mAdapter.notifyDataSetChanged();
        }
    }


    private static class UIHandler extends Handler {
        WeakReference<Fragment> weakReference;

        public UIHandler(Fragment fragment) {
            weakReference = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            CutClothOutFragment fragment = (CutClothOutFragment) weakReference.get();
            switch (msg.what) {
                case 1:
                    fragment.showToast("蓝牙连接成功");
                    fragment.isConnect = true;
                    break;
                case 2:
                    fragment.showConfirmDialog("蓝牙未连接");
                    fragment.isConnect = false;
                    break;
                case 3:
                    int position = fragment.mAdapter.getPosition();
                    if (position > 0 && position < fragment.myList.size()) {
                        fragment.myList.get(position).setCut_weight(fragment.weight);
                        fragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<CutOutBean> {
        private Context context;
        private int position = -255;

        public RecycleAdapter(RecyclerView v, Collection<CutOutBean> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void convert(RecyclerHolder holder, final CutOutBean item, final int position) {
            if (item != null) {
                CheckBox checkBox = holder.getView(R.id.checkbox1);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (position == 0) {
                            for (CutOutBean cutOutBean : myList) {
                                cutOutBean.setUpload(b);
                            }
                        } else {
                            item.setUpload(b);
                        }
                    }
                });
                if (position != 0) {
                    checkBox.setChecked(item.isUpload());
                    final EditText editText = holder.getView(R.id.edit1);
                    editText.setEnabled(true);
                    editText.setText(String.valueOf(item.getCut_weight()));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                String weight = s.toString();
                                weight = weight.replaceAll(" ", "");
                                if (!TextUtils.isEmpty(weight)) {
                                    double a = Double.parseDouble(weight);
                                    item.setCut_weight(a);
                                } else {
                                    item.setCut_weight(0.0);
                                }
                            } catch (Exception e) {
                                item.setCut_weight(0.0);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    holder.setText(R.id.item1, item.getProduct_applyp_model().getVat_no());
                    holder.setText(R.id.item2, item.getProduct_applyp_model().getColor_name());
                    holder.setText(R.id.item3, String.valueOf(item.getProduct_applyp_model().getYard_out()));
                    holder.setText(R.id.item4, String.valueOf(item.getProduct_applyp_model().getQty_kg()));
                    holder.setText(R.id.item5, String.valueOf(item.getProduct_applypid()));
                    LinearLayout layout = holder.getView(R.id.layout1);
                    layout.setBackgroundColor(this.position == position ? getResources().getColor(R.color.colorDialogTitleBG) : getResources().getColor(R.color.colorZERO));
                } else {
                    EditText editText = holder.getView(R.id.edit1);
                    editText.setEnabled(false);
                }
            }
        }
    }
}
