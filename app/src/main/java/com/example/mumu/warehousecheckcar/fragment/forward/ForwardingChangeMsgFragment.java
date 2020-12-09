package com.example.mumu.warehousecheckcar.fragment.forward;

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

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.SearchAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturnArray;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.BaseFragment;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 *created by 
 *on 2020/11/18
 */
public class ForwardingChangeMsgFragment extends BaseFragment {
    private static ForwardingChangeMsgFragment fragment;
    final String TAG = "ForwardingMsgFragment";
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.autoText1)
    AutoCompleteTextView autoText1;
    @BindView(R.id.autoText2)
    AutoCompleteTextView autoText2;
    @BindView(R.id.autoText3)
    AutoCompleteTextView autoText3;
    private SearchAdapter carAdapter;
    private SearchAdapter peoAdapter;
    private SearchAdapter compAdapter;
    private int id;
    private String code;

    public static ForwardingChangeMsgFragment newInstance() {
        if (fragment == null) ;
        fragment = new ForwardingChangeMsgFragment();
        return fragment;
    }

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
        id = getArguments().getInt("id", 0);
        code = getArguments().getString("code");
    }

    @Override
    protected void initView(View view) {
        carAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText1.setAdapter(carAdapter);
        peoAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText2.setAdapter(peoAdapter);
        compAdapter = new SearchAdapter(getActivity(), android.R.layout.simple_list_item_1);
        autoText3.setAdapter(compAdapter);
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

        autoText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                final String str = charSequence.toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(str) && str.length() >= 1) {
                    OkHttpClientManager.getAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/getTransportCompany?company=" + str, new OkHttpClientManager.ResultCallback<BaseReturnArray<String>>() {
                        @Override
                        public void onError(Request request, Exception e) {
                        }

                        @Override
                        public void onResponse(BaseReturnArray<String> baseReturnArray) {
                            try {
                                if (baseReturnArray != null && baseReturnArray.getStatus() == 1) {
                                    if (baseReturnArray.getData() != null && baseReturnArray.getData().size() > 0) {
                                        compAdapter.updataList(baseReturnArray.getData());
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

    @OnClick(R.id.button1)
    public void onViewClicked() {
        String carNo = autoText1.getText().toString();
        String name = autoText2.getText().toString();
        String company = autoText3.getText().toString();
        carNo = carNo.replaceAll(" ", "");
        name = name.replaceAll(" ", "");
        company = company.replaceAll(" ", "");
        JSONObject data = new JSONObject();
        data.put("carNo", carNo);
        data.put("driver", name);
        data.put("company", company);
        data.put("code", code);
        data.put("id", id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", User.newInstance().getId());
        jsonObject.put("data", data);
        final String json = jsonObject.toJSONString();
        if (!TextUtils.isEmpty(carNo) && !TextUtils.isEmpty(company)) {
            showUploadDialog("是否修改发运信息");
            setUploadYesClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit(json);
                    uploadDialog.lockView();
                }
            });
        } else
            Toast.makeText(getActivity(), "车牌号或调入公司不能为空", Toast.LENGTH_SHORT).show();
    }

    private void submit(String json) {
        try {
            OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/despatch/changeCarNo", new OkHttpClientManager.ResultCallback<JSONObject>() {
                @Override
                public void onError(Request request, Exception e) {
                    if (e instanceof ConnectException)
                        showConfirmDialog("链接超时");
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        uploadDialog.openView();
                        hideUploadDialog();
                        if (response.getInteger("status") == 1) {
                            showToast("上传成功");
                            getFragmentManager().popBackStack();
                        } else {
                            showToast("上传失败");
                            showConfirmDialog("上传失败" + response.getString("message"));
                            Sound.faillarm();
                        }
                    } catch (Exception e) {

                    }
                }
            }, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
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