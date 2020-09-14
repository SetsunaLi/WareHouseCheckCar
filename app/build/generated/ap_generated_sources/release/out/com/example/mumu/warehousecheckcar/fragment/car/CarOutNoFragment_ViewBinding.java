// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.car;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CarOutNoFragment_ViewBinding<T extends CarOutNoFragment> implements Unbinder {
  protected T target;

  private View view2131296395;

  private View view2131296313;

  @UiThread
  public CarOutNoFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.imgbutton, "field 'imgbutton' and method 'onViewClicked'");
    target.imgbutton = Utils.castView(view, R.id.imgbutton, "field 'imgbutton'", ImageButton.class);
    view2131296395 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
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

    target.imgbutton = null;
    target.recyle = null;
    target.button2 = null;

    view2131296395.setOnClickListener(null);
    view2131296395 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
