package com.example.mumu.warehousecheckcar.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;

/***
 *created by 
 *on 2020/9/2
 */
public class CommandDailog extends DialogFragment {
    private static String msg;
    public Button no, yes;
    public EditText password;
    private TextView dialogText;
    private View.OnClickListener onYesClickListener;
    private View.OnClickListener onNoClickListener;

    public static CommandDailog newInstance(String msg) {
        CommandDailog.msg = msg;
        return new CommandDailog();
    }


    public void setOnYesClickListener(View.OnClickListener onClickListener) {
        this.onYesClickListener = onClickListener;
        if (yes != null)
            yes.setOnClickListener(onYesClickListener);
    }

    public void setOnNoClickListener(View.OnClickListener onClickListener) {
        this.onNoClickListener = onClickListener;
        if (no != null)
            no.setOnClickListener(onNoClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_command, null);
        initView(view);
        initListener();
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    private void initView(View view) {
        no = view.findViewById(R.id.dialog_no);
        yes = view.findViewById(R.id.dialog_yes);
        dialogText = view.findViewById(R.id.dialog_text);
        dialogText.setText(msg);
        password = view.findViewById(R.id.password);
    }

    private void initListener() {
        if (onYesClickListener != null) {
            yes.setOnClickListener(onYesClickListener);
        }
        if (onNoClickListener != null) {
            no.setOnClickListener(onNoClickListener);
        } else
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnYesClickListener(null);
                    hideDialog();
                }
            });
    }

    public void changeText(String msg) {

        dialogText.setText(msg);
    }

    public void openView() {
        if (no != null && !no.isEnabled()) {
            no.setEnabled(true);
        }
        if (yes != null && !yes.isEnabled()) {
            yes.setEnabled(true);
        }
    }

    public void lockView() {
        if (no != null && no.isEnabled()) {
            no.setEnabled(false);
        }
        if (yes != null && yes.isEnabled()) {
            yes.setEnabled(false);
        }
    }

    public String getPassword() {
        return password.getText().toString();
    }

    protected void hideDialog() {
        if (!isHidden()) {
            dismiss();
        }
    }
}