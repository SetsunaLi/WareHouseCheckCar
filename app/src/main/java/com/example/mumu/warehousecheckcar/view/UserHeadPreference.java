package com.example.mumu.warehousecheckcar.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;


/**
 * Created by mumu on 2018/5/29.
 */

public class UserHeadPreference extends Preference {
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    private RelativeLayout userRl;
    private ImageView userIV;
    private TextView userNameTv,userIdTv;
    public UserHeadPreference(Context context, AttributeSet attrs,int defStyle){
        super(context, attrs, defStyle);
        this.mContext = context;
    }
    public UserHeadPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public UserHeadPreference(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    public void setmOnClickListener(View.OnClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }


    @Override
    public View onCreateView(ViewGroup parent){
        super.onCreateView(parent);
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.user_item1,null);
        return view;

    }

    @Override
    public void onBindView(View view){
        super.onBindView(view);
        userRl=(RelativeLayout)view.findViewById(R.id.user_layout);
        userIV=(ImageView)view.findViewById(R.id.headImage);
        userNameTv=(TextView)view.findViewById(R.id.user_name);
        userIdTv=(TextView)view.findViewById(R.id.user_id);

        userRl.setOnClickListener(mOnClickListener);
        userIV.setOnClickListener(mOnClickListener);
        userNameTv.setOnClickListener(mOnClickListener);
    }

    public RelativeLayout getMyLl() {

        return userRl;
    }

    public ImageView getUserIv() {
        return userIV;
    }

    public TextView getUserNameTv() {
        return userNameTv;
    }
    public TextView getUserIdTv(){
        return userIdTv;
    }

}
