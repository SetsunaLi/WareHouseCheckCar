package com.example.mumu.warehousecheckcar.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/***
 *created by mumu
 *on 2019/11/14
 */
public class Imgutil {
    /**
     * 获取,寻找并裁剪图片
     *
     * @param context
     * @param id
     * @return
     */
    public static Drawable findImgAsSquare(Context context, int id, int sidedp) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        int px = dip2px(context, sidedp);
        drawable.setBounds(0, 0, px, px);
        return drawable;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
