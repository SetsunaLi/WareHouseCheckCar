// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.forward;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ApplyNoListFragment_ViewBinding<T extends ApplyNoListFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  private View view2131296313;

  @UiThread
  public ApplyNoListFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.checkbox1 = Utils.findRequiredViewAsType(source, R.id.checkbox1, "field 'checkbox1'", CheckBox.class);
    target.item1 = Utils.findRequiredViewAsType(source, R.id.item1, "field 'item1'", TextView.class);
    target.item2 = Utils.findRequiredViewAsType(source, R.id.item2, "field 'item2'", TextView.class);
    target.layout1 = Utils.findRequiredViewAsType(source, R.id.layout1, "field 'layout1'", LinearLayout.class);
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.button1, "field 'button1' and method 'onViewClicked'");
    target.button1 = Utils.castView(view, R.id.button1, "field 'button1'", Button.class);
    view2131296303 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button2, "field 'button2' and method 'onViewClicked'");
    target.button2 = Utils.castView(view, R.id.button2, "field 'button2'", Button.class);
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

    target.checkbox1 = null;
    target.item1 = null;
    target.item2 = null;
    target.layout1 = null;
    target.recyle = null;
    target.button1 = null;
    target.button2 = null;

    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
