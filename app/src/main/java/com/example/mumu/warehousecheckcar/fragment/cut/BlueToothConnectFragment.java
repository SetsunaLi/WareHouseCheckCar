package com.example.mumu.warehousecheckcar.fragment.cut;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BRecyclerAdapter;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TAG_CONTENT_FRAGMENT;

/***
 *created by 
 *on 2020/4/11
 */
public class BlueToothConnectFragment extends BaseFragment implements BRecyclerAdapter.OnItemClickListener {
    public static final String ADDRESS_KEY = "address";
    public static final String NAME_KEY = "name";
    public static final String RSSI_KEY = "rssi";
    private static final int BLUE_TOOTH_ENABLE_CODE = 101;
    private static final int MESSAGE_SCAN_CHANGE = 1;
    private static final int MESSAGE_LIST_CHANGE = 2;
    private final String TAG = BlueToothConnectFragment.class.getSimpleName();
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.list)
    RecyclerView list;
    private boolean enable = true;//判断设备能不能用蓝牙
    private Handler delayHandler;//设置延迟用的handler
    private UIHandler uiHandler;//改UI的Handler
    private boolean flag = true;//判断扫描开关有没有打开
    private ArrayList<BluetoothDevice> addresses = new ArrayList<>();//存地址的地方
    private BluetoothAdapter bluetoothAdapter;//连接ble蓝牙用到的适配器
    private RecycleAdapter mAdapter;
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() != null && device.getAddress() != null && !addresses.contains(device)) {
                addresses.add(device);
                Message message = new Message();
                message.what = MESSAGE_LIST_CHANGE;
                uiHandler.sendMessage(message);
            }
        }
    };

    public static BlueToothConnectFragment newInstance() {
        return new BlueToothConnectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.cut_out));

        View view = inflater.inflate(R.layout.bluetooth_connect_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initBle();
    }

    @Override
    protected void initView(View view) {
        delayHandler = new Handler();
        uiHandler = new UIHandler();
        mAdapter = new RecycleAdapter(list, addresses, R.layout.item_address_list);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);
        list.setAdapter(mAdapter);
        checkPermission();
    }

    @Override
    protected void addListener() {
        mAdapter.setOnItemClickListener(this);
    }

    private void initBle() {
        //判断手机是否支持蓝牙
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            enable = false;
            showToast("设备不支持蓝牙");
        }

        //获取蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //打开蓝牙权限
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBIntent, BLUE_TOOTH_ENABLE_CODE);
        }
    }

    public void checkPermission() {
        int hasWritePermission = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWritePermission != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 9);
        } else {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (flag) {
            bluetoothAdapter.stopLeScan(scanCallback);
            flag = false;
        }
    }

    @OnClick({R.id.button1, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                if (flag) {
                    bluetoothAdapter.stopLeScan(scanCallback);
                    flag = false;
                }

                Bundle bundle = new Bundle();
                bundle.putBoolean("isBlueTooth", false);
                Fragment fragment = CutClothOutFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
                transaction.show(fragment);
                transaction.commit();
                break;
            case R.id.button3:
                if (!enable) {
                    showToast("不支持蓝牙");
                    return;
                }
                addresses.clear();
                showLoadingDialog();
                scanDevice();
                break;
        }
    }

    private void scanDevice() {
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = MESSAGE_SCAN_CHANGE;
                uiHandler.sendMessage(message);
                bluetoothAdapter.stopLeScan(scanCallback);
            }
        }, 10000);
        bluetoothAdapter.startLeScan(scanCallback);
        flag = true;
    }

    @Override
    public void onItemClick(View view, Object data, int position) {
        if (flag) {
            bluetoothAdapter.stopLeScan(scanCallback);
            flag = false;
        }
        BluetoothDevice bluetoothDevice = (BluetoothDevice) data;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isBlueTooth", true);
        bundle.putString("name", bluetoothDevice.getName());
        bundle.putString("address", bluetoothDevice.getAddress());
        Fragment fragment = CutClothOutFragment.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    @SuppressLint("HandlerLeak")
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_SCAN_CHANGE:
                    flag = false;
                    hideLoadingDialog();
                    break;
                case MESSAGE_LIST_CHANGE:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<BluetoothDevice> {
        private Context context;

        public RecycleAdapter(RecyclerView v, Collection<BluetoothDevice> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void convert(RecyclerHolder holder, final BluetoothDevice item, final int position) {
            holder.setText(R.id.name, item.getName());
            holder.setText(R.id.address, item.getAddress());
            holder.setText(R.id.rs, String.valueOf(item.getBondState()));
        }
    }
}
