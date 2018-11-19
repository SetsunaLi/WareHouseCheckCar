package com.example.mumu.warehousecheckcar.picture;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

/**
 * Created by mumu on 2018/8/11.
 */

public class CutToBitmap {

    /** 图片转化Bitmap 按照控件大小**/
    public static Bitmap changeToBitmap(Resources res,View view, int resId) {
        int imageViewWidth = view.getWidth();
        int imageViewHeight = view.getHeight();
        return decodeResourceBySampleRate(res, resId,
                imageViewWidth, imageViewHeight);
    }

    /** 重置图片Bitmap大小 **/
    public static Bitmap resetBitmap(View view, Bitmap oldBitmap) {
        int viewWidth = view.getMeasuredWidth();
        int bitHeight = view.getMeasuredHeight();
        // 第一种方法
        return Bitmap.createScaledBitmap(oldBitmap, viewWidth, bitHeight, true);
        // 第二种方法
        // Matrix matrix = new Matrix();
        // matrix.postScale(viewWidth, bitHeight);
        // Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0,
        // oldBitmap.getWidth(),oldBitmap.getHeight(), matrix, true);
        // return newbm;
    }
    /** 图片转化Bitmap 按照自定义大小**/
    public static Bitmap decodeResourceBySampleRate(Resources res, int resId,
                                                    int imageViewWidth, int imageViewHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        int sampleRate = calSampleRate(options, imageViewWidth, imageViewHeight);
        options.inSampleSize = sampleRate;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(res, resId, options);
        return bm;
    }

    public static int calSampleRate(BitmapFactory.Options options,
                                    int imageViewWidth, int imageViewHeight) {
        int sampleRate = 1;

        if (imageViewWidth == 0 || imageViewHeight == 0) {
            return sampleRate;
        }
        int imageRawWidth = options.outWidth;
        int imageRawHeight = options.outHeight;
        if (imageRawWidth > imageViewWidth || imageRawHeight > imageViewHeight) {
            sampleRate = 2;
            int halfImageRawWidth = imageRawWidth / sampleRate;
            int halfImageRawHeight = imageRawHeight / sampleRate;
            while ((halfImageRawWidth / sampleRate) >= imageViewWidth
                    || (halfImageRawHeight / sampleRate) >= imageViewHeight) {
                sampleRate = sampleRate * 2;
            }
        }
        return sampleRate;

    }


}