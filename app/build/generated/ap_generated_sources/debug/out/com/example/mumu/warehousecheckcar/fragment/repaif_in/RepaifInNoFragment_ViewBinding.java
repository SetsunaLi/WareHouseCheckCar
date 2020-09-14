// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.repaif_in;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RepaifInNoFragment_ViewBinding<T extends RepaifInNoFragment> implements Unbinder {
  protected T target;

  private View view2131296313;

  @UiThread
  public RepaifInNoFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.fixeedittext2 = Utils.findRequiredViewAsType(source, R.id.fixeedittext2, "field 'fixeedittext2'", FixedEditText.class);
    view = Utils.findRequiredView(source, R.id.button2, "field 'button2' and method 'onViewClicked'");
    target.button2 = Utils.castView(view, R.id.button2, "field 'button2'", Button.class);
    view2131296313 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.autoText1 = Utils.findRequiredViewAsType(source, R.id.autoText1, "field 'autoText1'", AutoCompleteTextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.fixeedittext2 = null;
    target.button2 = null;
    target.autoText1 = null;

    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
