// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.cut;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BlueToothConnectFragment_ViewBinding<T extends BlueToothConnectFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  private View view2131296315;

  @UiThread
  public BlueToothConnectFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.button1, "field 'button1' and method 'onViewClicked'");
    target.button1 = Utils.castView(view, R.id.button1, "field 'button1'", Button.class);
    view2131296303 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button3, "field 'button3' and method 'onViewClicked'");
    target.button3 = Utils.castView(view, R.id.button3, "field 'button3'", Button.class);
    view2131296315 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.list = Utils.findRequiredViewAsType(source, R.id.list, "field 'list'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.button1 = null;
    target.button3 = null;
    target.list = null;

    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296315.setOnClickListener(null);
    view2131296315 = null;

    this.target = null;
  }
}
