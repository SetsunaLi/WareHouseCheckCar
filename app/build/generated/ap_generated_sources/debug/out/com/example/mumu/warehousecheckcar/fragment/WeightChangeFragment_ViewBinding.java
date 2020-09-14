// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WeightChangeFragment_ViewBinding<T extends WeightChangeFragment> implements Unbinder {
  protected T target;

  private View view2131296313;

  @UiThread
  public WeightChangeFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    target.text3 = Utils.findRequiredViewAsType(source, R.id.text3, "field 'text3'", TextView.class);
    target.text4 = Utils.findRequiredViewAsType(source, R.id.text4, "field 'text4'", TextView.class);
    target.layout1 = Utils.findRequiredViewAsType(source, R.id.layout1, "field 'layout1'", LinearLayout.class);
    target.edittext1 = Utils.findRequiredViewAsType(source, R.id.edittext1, "field 'edittext1'", EditText.class);
    target.edittext2 = Utils.findRequiredViewAsType(source, R.id.edittext2, "field 'edittext2'", EditText.class);
    target.spinner1 = Utils.findRequiredViewAsType(source, R.id.spinner1, "field 'spinner1'", Spinner.class);
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
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.text1 = null;
    target.text2 = null;
    target.text3 = null;
    target.text4 = null;
    target.layout1 = null;
    target.edittext1 = null;
    target.edittext2 = null;
    target.spinner1 = null;
    target.relativelayout = null;
    target.button2 = null;

    view2131296313.setOnClickListener(null);
    view2131296313 = null;

    this.target = null;
  }
}
