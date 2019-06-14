package com.example.mumu.warehousecheckcar.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.entity.UpdateBean;
import com.example.mumu.warehousecheckcar.listener.UpdateDialogOperate;

public class UpdateDialog extends Dialog implements View.OnClickListener {

    TextView ver;
    Button update_ok;
    private UpdateDialogOperate aDialogOperate; // 操作接口
    private Context context;
    private UpdateBean upadtebean;


    public UpdateDialog(@NonNull Context context) {
        super(context, R.style.common_dialog);
//        super(context);
        this.context = context;
        this.setContentView(R.layout.update_dialog_layout);
        ver = (TextView) findViewById(R.id.update_ver);
        update_ok = (Button) findViewById(R.id.update_btn_ok);
        update_ok.setOnClickListener(this);
    }
    public void setData(UpdateBean upadtebean, boolean flag, UpdateDialogOperate aDialogOperate) {
        this.aDialogOperate = aDialogOperate;
        this.upadtebean = upadtebean;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        update_ok.setVisibility(View.VISIBLE);
        ver.setText(upadtebean.getVersion_name());
        this.setCancelable(false);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update_btn_cancle:
                aDialogOperate.executeCancel("");
                break;
            case R.id.update_btn_ok:
                aDialogOperate.executeCommit("");
                break;
        }

    }
}
