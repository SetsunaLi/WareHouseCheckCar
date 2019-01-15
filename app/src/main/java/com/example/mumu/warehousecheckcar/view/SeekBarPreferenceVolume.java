package com.example.mumu.warehousecheckcar.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;


/**
 * Created by mumu on 2018/5/22.
 */

public class SeekBarPreferenceVolume extends Preference implements SeekBar.OnSeekBarChangeListener {
    private static final String androidns="http://schemas.android.com/apk/res/android";

    private SeekBar mSeekBar;
    private TextView titleText,mValueText;
    private ImageView imgView;
    private Context mContext;
    //title-title
    private String mDialogMessage,title;
    //mMax-max\imgSrc-imgSrc\mValue-value
    private int mDefault, mMax, mValue,imgSrc = 0;

    public SeekBarPreferenceVolume(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        mDialogMessage = attrs.getAttributeValue(androidns,"dialogMessage");
        mDefault = attrs.getAttributeIntValue(androidns,"defaultValue", 20);

        title=attrs.getAttributeValue(androidns,"title");
        mMax = attrs.getAttributeIntValue(androidns,"max", 100);

//        <!--自定义控件布局属性-->
            int count=attrs.getAttributeCount();
            for (int i = 0; i < count; i++){
                //获取attr的资源ID
                int attrResId = attrs.getAttributeNameResource(i);
                switch (attrResId){
                    case R.attr.myvalue:
                        mValue = attrs.getAttributeIntValue(i,20);
                        break;
                    case R.attr.myimg:
                        imgSrc = attrs.getAttributeResourceValue(i, 0);
                        break;
                }
            }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View view=inflater.inflate(R.layout.ring_volume_prefs,null);
        mValueText = (TextView)view.findViewById(R.id.volume_value);
        mSeekBar = (SeekBar)view.findViewById(R.id.volume_seekbar);
        mSeekBar.setOnSeekBarChangeListener(this);
        titleText=(TextView)view.findViewById(R.id.volume_title);
        imgView=(ImageView)view.findViewById(R.id.volume_img);

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
        mValueText.setText(String.valueOf(mValue));
        return view;

    }

    @Override
    protected void onBindView(View v) {
        super.onBindView(v);
        mSeekBar.setMax(mMax);
            mSeekBar.setProgress(mValue);
        if (mValue!=-1)
            mValueText.setText(String.valueOf(mValue));
        if (title!=null)
            titleText.setText(title+"");
        if (imgSrc!=0&&imgSrc!=-1)
            imgView.setImageResource(imgSrc);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        else
            mValue = (Integer)defaultValue;
    }
    private  int callValue;
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        callValue=value;
        String t = String.valueOf(value);
        mValueText.setText(t);
       /* if (shouldPersist())
            persistInt(callValue);*/
        callChangeListener(new Integer(callValue));
//        setProgress(callValue);
    }
    public void onStartTrackingTouch(SeekBar seek) {
    }
    public void onStopTrackingTouch(SeekBar seek) {
        if (shouldPersist())
            persistInt(callValue);
    }

    public void setMax(int max) { mMax = max; }
    public int getMax() { return mMax; }

    public void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null)
            mSeekBar.setProgress(progress);
        if (shouldPersist())
            persistInt(progress);
        callChangeListener(new Integer(progress));
    }
    public int getProgress() { return mValue; }
}
