package com.example.mumu.warehousecheckcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;

/***
 *created by mumu
 *on 2019/11/25
 */
public class MsgView extends LinearLayout {
    private TextView tvTitle, tvMsg;
    private String titleText, titleMsg;

    public MsgView(Context context) {
        this(context, null);
    }

    public MsgView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.text_msg_layout, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MsgView);
        titleText = typedArray.getString(R.styleable.MsgView_titleText);
        titleMsg = typedArray.getString(R.styleable.MsgView_msgText);
        typedArray.recycle();

        tvTitle = findViewById(R.id.titleText);
        tvMsg = findViewById(R.id.msgText);
        setTitleText(titleText);
        setMsgText(titleMsg);
    }

    public void setTitleText(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    public void setMsgText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvMsg.setText(text);
        }
    }
}
