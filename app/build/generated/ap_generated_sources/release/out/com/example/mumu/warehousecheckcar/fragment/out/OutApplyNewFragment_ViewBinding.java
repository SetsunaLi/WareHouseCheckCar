// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.out;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OutApplyNewFragment_ViewBinding<T extends OutApplyNewFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  private View view2131296313;

  @UiThread
  public OutApplyNewFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
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
    target.edit1 = Utils.findRequiredViewAsType(source, R.id.edit1, "field 'edit1'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.recyle = null;
    target.text1 = null;
    target.text2 = null;
    target.button1 = null;
    target.button2 = null;
    target.edit1 = null;

    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
