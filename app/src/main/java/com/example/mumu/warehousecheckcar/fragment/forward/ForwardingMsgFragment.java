package com.example.mumu.warehousecheckcar.fragment.forward;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.SearchAdapter;
import com.example.mumu.warehousecheckcar.application.App;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.EventBusMsg;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForwardingMsgFragment extends BaseFragment {
    final String TAG = "ForwardingMsgFragment";
    private static ForwardingMsgFragment fragment;

    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.autoText1)
    AutoCompleteTextView autoText1;
    @BindView(R.id.autoText2)
    AutoCompleteTextView autoText2;

    public static ForwardingMsgFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingMsgFragment();
        return fragment;
    }

    private SearchAdapter carAdapter;
    private SearchAdapter peoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forwarding_msg_layout, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("发运");
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        carAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText1.setAdapter(carAdapter);
        peoAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText2.setAdapter(peoAdapter);
    }

    @Override
    protected void addListener() {
        autoText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 2) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/getLicensePlateList?licensePlate=" + str, new OkHttpClientManager.ResultCallback<BaseReturnArray<String>>() {
                        @Override
                        public void onError(Request request, Exception e) {
                        }

                        @Override
                        public void onResponse(BaseReturnArray<String> baseReturnArray) {
                            try {
                                if (baseReturnArray != null && baseReturnArray.getStatus() == 1) {
                                    if (baseReturnArray.getData() != null && baseReturnArray.getData().size() > 0) {
                                        carAdapter.updataList(baseReturnArray.getData());
                                    }
                                }

                            } catch (Exception e) {

                            }
                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        autoText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 1) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/getDriverNameList?driverName=" + str, new OkHttpClientManager.ResultCallback<BaseReturnArray<String>>() {
                        @Override
                        public void onError(Request request, Exception e) {
                        }

                        @Override
                        public void onResponse(BaseReturnArray<String> baseReturnArray) {
                            try {
                                if (baseReturnArray != null && baseReturnArray.getStatus() == 1) {
                                    if (baseReturnArray.getData() != null && baseReturnArray.getData().size() > 0) {
                                        peoAdapter.updataList(baseReturnArray.getData());
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        EventBus.getDefault().unregister(this);
    }

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";

    @OnClick(R.id.button1)
    public void onViewClicked() {
        String carNo = autoText1.getText().toString() + "";
        String name = autoText2.getText().toString() + "";
        carNo = carNo.replaceAll(" ", "");
        name = name.replaceAll(" ", "");
        if (carNo != null && !carNo.equals("")) {
            EventBus.getDefault().postSticky(new EventBusMsg(0x00, new CarMsg(carNo, name), 0));
            Fragment fragment = ForwardingNoFragment.newInstance();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT).addToBackStack(null);
            transaction.show(fragment);
            transaction.commit();
        } else
            Toast.makeText(getActivity(), "车牌号不能为空", Toast.LENGTH_SHORT).show();
    }


    static class CarMsg implements Serializable {
        /**
         * 车牌号
         */
        private String carNo;
        /**
         * s司机姓名
         */
        private String carName;


        public CarMsg(String carNo, String carName) {
            this.carNo = carNo;
            this.carName = carName;
        }

        public String getCarNo() {
            return carNo;
        }

        public void setCarNo(String carNo) {
            this.carNo = carNo;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }
    }
}
