package com.example.mumu.warehousecheckcar.fragment.cut;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.mumu.warehousecheckcar.App;
import com.example.mumu.warehousecheckcar.Constant;
import com.example.mumu.warehousecheckcar.LDBE_UHF.Sound;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.adapter.BasePullUpRecyclerAdapter;
import com.example.mumu.warehousecheckcar.client.OkHttpClientManager;
import com.example.mumu.warehousecheckcar.entity.BaseReturn;
import com.example.mumu.warehousecheckcar.entity.User;
import com.example.mumu.warehousecheckcar.fragment.CodeFragment;
import com.example.mumu.warehousecheckcar.second.RecyclerHolder;
import com.example.mumu.warehousecheckcar.utils.LogUtil;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import com.example.mumu.warehousecheckcar.zxing.CaptureActivity;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mumu.warehousecheckcar.App.TIME;

/***
 *created by 快递单号绑定
 *on 2020/4/18
 */
public class ExpressageNoBindingFragment extends CodeFragment {
    @BindView(R.id.fixeedittext1)
    FixedEditText fixeedittext1;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.headNo)
    LinearLayout headNo;
    @BindView(R.id.imgbutton)
    ImageButton imgbutton;
    @BindView(R.id.recyle)
    RecyclerView recyle;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.imagebutton2)
    ImageButton imagebutton2;

    private ArrayList<String> myList;
    private RecycleAdapter mAdapter;
    private int flag = 1;//0为空，1为快点单，2为申请单

    public static ExpressageNoBindingFragment newInstance() {
        return new ExpressageNoBindingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.cut_scanner));

        View view = inflater.inflate(R.layout.expressage_no_binding_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        myList = new ArrayList<>();
        myList.add("");
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RecycleAdapter(recyle, myList, R.layout.out_applyno_item);
        mAdapter.setContext(getActivity());
        mAdapter.setState(BasePullUpRecyclerAdapter.STATE_NO_MORE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyle.setLayoutManager(llm);
        recyle.setAdapter(mAdapter);
        imagebutton2.setVisibility(App.isPDA ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void addListener() {
        mAdapter.setCameraClick(new OnCameraClick() {
            @Override
            public void startCamera(int count, Object t) {
                startActivityForResult(new Intent(getActivity(), CaptureActivity.class), Constant.REQUEST_CAMERA_CODE);
            }
        });
        init2D();
        fixeedittext1.setFocusable(true);//设置输入框可聚集
        fixeedittext1.setFocusableInTouchMode(true);//设置触摸聚焦
        fixeedittext1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                flag = 1;
                return false;
            }
        });
    }

    private void clearData() {
        myList.clear();
        myList.add("");
        fixeedittext1.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constant.REQUEST_CAMERA_CODE:
                    String code = data.getStringExtra(CaptureActivity.KEY_DATA);
                    code = code.replaceAll(" ", "");
                    if (flag == 1) {
                        fixeedittext1.setText(code);
                    } else if (flag == 2) {
                        int position = mAdapter.getPosition();
                        if (position < myList.size())
                            myList.set(position, code);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
            }

        }
    }

    @OnClick({R.id.imgbutton, R.id.button2, R.id.imagebutton2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgbutton:
                myList.add("");
                mAdapter.select(myList.size() - 1);
                mAdapter.notifyDataSetChanged();
                recyle.scrollToPosition(myList.size() - 1);
                break;
            case R.id.button2:
                showUploadDialog("是否确上传快递单");
                setUploadYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit();
                        uploadDialog.lockView();
                        handler.postDelayed(r, TIME);
                    }
                });
                break;
            case R.id.imagebutton2:
                flag = 1;
                startActivityForResult(new Intent(getActivity(), CaptureActivity.class), Constant.REQUEST_CAMERA_CODE);
                break;
        }
    }

    private void submit() {
        String courierNo = fixeedittext1.getText().toString();
        if (!TextUtils.isEmpty(courierNo)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("courierNo", courierNo);
            jsonObject.put("outNo", myList);
            jsonObject.put("userId", User.newInstance().getId());
            final String json = jsonObject.toJSONString();
            try {
                LogUtil.i(getResources().getString(R.string.log_exp_binding), json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OkHttpClientManager.postJsonAsyn(App.IP + ":" + App.PORT + "/shYf/sh/express/expressBoundOutNo", new OkHttpClientManager.ResultCallback<BaseReturn>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (e instanceof ConnectException)
                            showConfirmDialog("链接超时");
                        try {
                            LogUtil.e(getResources().getString(R.string.log_exp_binding_result), e.getMessage(), e);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponse(BaseReturn response) {
                        try {
                            LogUtil.i(getResources().getString(R.string.log_exp_binding_result), "userId:" + User.newInstance().getId() + response.toString());
                            uploadDialog.openView();
                            hideUploadDialog();
                            handler.removeCallbacks(r);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void codeResult(String code) {
        code = code.replaceAll(" ", "");
        if (flag == 1) {
            fixeedittext1.setText(code);
        } else if (flag == 2) {
            int position = mAdapter.getPosition();
            if (position < myList.size())
                myList.set(position, code);
            mAdapter.notifyDataSetChanged();
        }
    }


    interface OnCameraClick {
        public void startCamera(int count, Object t);
    }

    class RecycleAdapter extends BasePullUpRecyclerAdapter<String> {
        private Context context;
        private int position = -255;
        private OnCameraClick cameraClick;

        public void setCameraClick(OnCameraClick cameraClick) {
            this.cameraClick = cameraClick;
        }

        public RecycleAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);

        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setHeader(View mHeaderView) {
            super.setHeader(mHeaderView);
        }

        public void select(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public void convert(RecyclerHolder holder, final String item, final int position) {
            final FixedEditText editNo = (FixedEditText) holder.getView(R.id.fixeedittext1);
            editNo.setFocusable(true);//设置输入框可聚集
            editNo.setFocusableInTouchMode(true);//设置触摸聚焦
            editNo.clearFocus();
            if (this.position == position) {
                editNo.requestFocus();
                editNo.setSelection(editNo.getText().length());
            }
            editNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        editNo.setSelection(editNo.getText().length());
                        flag = 2;
                        select(position);
                    }
                }
            });
            editNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    myList.set(position, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            editNo.setTag(position);
            editNo.setText(item);
            ImageButton imageButton = (ImageButton) holder.getView(R.id.imagebutton1);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myList.size() != 1) {
                        myList.remove(position);
                        RecycleAdapter.this.position = -255;
                        flag = 0;
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            ImageButton imageButton2 = (ImageButton) holder.getView(R.id.imagebutton2);
            if (!App.isPDA) {
                imageButton2.setVisibility(View.VISIBLE);
            } else
                imageButton2.setVisibility(View.GONE);
            imageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag = 2;
                    select(position);
                    if (cameraClick != null)
                        cameraClick.startCamera(position, item);
                }
            });
        }
    }
}
