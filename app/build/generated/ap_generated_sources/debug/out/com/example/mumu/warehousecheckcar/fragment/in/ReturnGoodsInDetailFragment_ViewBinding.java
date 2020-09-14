// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.in;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReturnGoodsInDetailFragment_ViewBinding<T extends ReturnGoodsInDetailFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  private View view2131296313;

  @UiThread
  public ReturnGoodsInDetailFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.text3 = Utils.findRequiredViewAsType(source, R.id.text3, "field 'text3'", TextView.class);
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    view = Utils.findRequiredView(source, R.id.button1, "method 'onViewClicked'");
    view2131296303 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button2, "method 'onViewClicked'");
    view2131296313 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.text3 = null;
    target.recyle = null;
    target.text1 = null;
    target.text2 = null;

    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
