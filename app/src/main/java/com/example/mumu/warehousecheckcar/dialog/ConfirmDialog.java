package com.example.mumu.warehousecheckcar.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;


/***
 *created by ${mumu}
 *on 2019/8/17
 * @author Administrator
 */
public class ConfirmDialog extends DialogFragment {
    public static ConfirmDialog newInstance(String textMsg) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString("text", textMsg);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getArguments() != null) {
            String textMsg = getArguments().getString("text");
            if (!TextUtils.isEmpty(textMsg)) {
                builder.setMessage(textMsg);
            }
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideDialog();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideDialog();
            }
        });
        return builder.create();
    }

    public void changeText(String msg) {
        Bundle args = new Bundle();
        args.putString("text", msg);
        setArguments(args);
    }

    private void hideDialog() {
        if (!isHidden()) {
            dismiss();
        }
    }
}
