package com.example.mumu.warehousecheckcar.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

/***
 *created by ${mumu}
 *on 2019/9/23
 */
public class BaseDrawerLayout extends DrawerLayout {
    public BaseDrawerLayout(Context context) {
        super(context);
    }

    public BaseDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
