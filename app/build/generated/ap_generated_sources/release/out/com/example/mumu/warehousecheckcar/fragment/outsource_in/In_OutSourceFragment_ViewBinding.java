// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.outsource_in;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class In_OutSourceFragment_ViewBinding<T extends In_OutSourceFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  private View view2131296313;

  @UiThread
  public In_OutSourceFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.item1 = Utils.findRequiredViewAsType(source, R.id.item1, "field 'item1'", TextView.class);
    target.item2 = Utils.findRequiredViewAsType(source, R.id.item2, "field 'item2'", TextView.class);
    target.item3 = Utils.findRequiredViewAsType(source, R.id.item3, "field 'item3'", TextView.class);
    target.item4 = Utils.findRequiredViewAsType(source, R.id.item4, "field 'item4'", TextView.class);
    target.item5 = Utils.findRequiredViewAsType(source, R.id.item5, "field 'item5'", TextView.class);
    target.item6 = Utils.findRequiredViewAsType(source, R.id.item6, "field 'item6'", TextView.class);
    target.item7 = Utils.findRequiredViewAsType(source, R.id.item7, "field 'item7'", TextView.class);
    target.item8 = Utils.findRequiredViewAsType(source, R.id.item8, "field 'item8'", TextView.class);
    target.item9 = Utils.findRequiredViewAsType(source, R.id.item9, "field 'item9'", TextView.class);
    target.item10 = Utils.findRequiredViewAsType(source, R.id.item10, "field 'item10'", TextView.class);
    target.item11 = Utils.findRequiredViewAsType(source, R.id.item11, "field 'item11'", TextView.class);
    target.item12 = Utils.findRequiredViewAsType(source, R.id.item12, "field 'item12'", TextView.class);
    target.item13 = Utils.findRequiredViewAsType(source, R.id.item13, "field 'item13'", TextView.class);
    target.item14 = Utils.findRequiredViewAsType(source, R.id.item14, "field 'item14'", TextView.class);
    target.checkbox1 = Utils.findRequiredViewAsType(source, R.id.checkbox1, "field 'checkbox1'", CheckBox.class);
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

    target.item1 = null;
    target.item2 = null;
    target.item3 = null;
    target.item4 = null;
    target.item5 = null;
    target.item6 = null;
    target.item7 = null;
    target.item8 = null;
    target.item9 = null;
    target.item10 = null;
    target.item11 = null;
    target.item12 = null;
    target.item13 = null;
    target.item14 = null;
    target.checkbox1 = null;
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
