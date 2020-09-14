// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.forward;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForwardingMsgFragment_ViewBinding<T extends ForwardingMsgFragment> implements Unbinder {
  protected T target;

  private View view2131296303;

  @UiThread
  public ForwardingMsgFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.button1, "field 'button1' and method 'onViewClicked'");
    target.button1 = Utils.castView(view, R.id.button1, "field 'button1'", Button.class);
    view2131296303 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.autoText1 = Utils.findRequiredViewAsType(source, R.id.autoText1, "field 'autoText1'", AutoCompleteTextView.class);
    target.autoText2 = Utils.findRequiredViewAsType(source, R.id.autoText2, "field 'autoText2'", AutoCompleteTextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.button1 = null;
    target.autoText1 = null;
    target.autoText2 = null;

    view2131296303.setOnClickListener(null);
    view2131296303 = null;

    this.target = null;
  }
}
