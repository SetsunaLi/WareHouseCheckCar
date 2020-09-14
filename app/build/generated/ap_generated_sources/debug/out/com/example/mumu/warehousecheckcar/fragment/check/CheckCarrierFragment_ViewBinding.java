// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.check;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CheckCarrierFragment_ViewBinding<T extends CheckCarrierFragment> implements Unbinder {
  protected T target;

  private View view2131296313;

  @UiThread
  public CheckCarrierFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.relativelayout = Utils.findRequiredViewAsType(source, R.id.relativelayout, "field 'relativelayout'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.button2, "field 'button2' and method 'onViewClicked'");
    target.button2 = Utils.castView(view, R.id.button2, "field 'button2'", Button.class);
    view2131296313 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.edittext1 = Utils.findRequiredViewAsType(source, R.id.edittext1, "field 'edittext1'", EditText.class);
    target.edittext2 = Utils.findRequiredViewAsType(source, R.id.edittext2, "field 'edittext2'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.relativelayout = null;
    target.button2 = null;
    target.edittext1 = null;
    target.edittext2 = null;

    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
