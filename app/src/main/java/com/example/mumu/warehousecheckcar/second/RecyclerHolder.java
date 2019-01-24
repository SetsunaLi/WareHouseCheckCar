package com.example.mumu.warehousecheckcar.second;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by mumu on 2018/6/5.
 */

public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener{
    private final SparseArray<View> mViews;
    MyItemOnClickListener mListener;
    MyOnTouchListener myTouchListener;

    public  RecyclerHolder(View itemView) {
        super(itemView);
        //一般不会超过8个吧
        this.mViews = new SparseArray<>(255);
    }
    public RecyclerHolder(View itemView,MyItemOnClickListener mListener, MyOnTouchListener myTouchListener) {
        super(itemView);
        //一般不会超过8个吧
        this.mViews = new SparseArray<>(255);
        this.mListener=mListener;
        this.myTouchListener=myTouchListener;
        itemView.setOnClickListener(this);
        itemView.setOnTouchListener(this);
    }

    public SparseArray<View> getAllView() {
        return mViews;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public TextView getTextView(int viewId) {
        TextView view = (TextView) mViews.get(viewId);
        if (view == null) {
            view = (TextView)itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public RecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }
    /**
     * 为TextView设置字符颜色
     *
     * @param viewId
     * @param color
     * @return
     */
    public RecyclerHolder setTextColor(int viewId, ColorStateList color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public RecyclerHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }
    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public RecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }
    /**
     * 为View设置背景图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public RecyclerHolder setBackground(int viewId, Drawable drawableId) {
        View view = getView(viewId);
        view.setBackground(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public RecyclerHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置网络图片
     * @param context
     * @param viewId
     * @param url
     * @param defaultDrawable  默认图片
     * @param isCircle 是否是圆形
     * @return
     */
    public RecyclerHolder setImageByUrl(Context context,
                                        int viewId,
                                        String url,
                                        int defaultDrawable,
                                        boolean isCircle) {
        ImageView view = getView(viewId);
        if (isCircle) {
            Glide.with(context)
                    .load(url)
//                    .animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //缓存所有的图片
//                    .bitmapTransform(new CropCircleTransformation(context))
                    .placeholder(defaultDrawable)
                    .dontAnimate()
                    .into(view);
        }else {
            Glide.with(context)
                    .load(url)
//                    .animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //缓存所有的图片
                    .placeholder(defaultDrawable)    //占位符 也就是加载中的图片，可放个gif
                    .fitCenter()
                    .into(view);
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if (mListener!=null)
            mListener.onItemOnClick(v,getPosition());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (myTouchListener!=null)
            myTouchListener.onItemTouch(view,motionEvent,getPosition());
        return false;
    }
    public interface MyOnTouchListener{
       void onItemTouch(View view, MotionEvent motionEvent, int postion);
    }
}
