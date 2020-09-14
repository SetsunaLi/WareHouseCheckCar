// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.cut;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ExpressageNoBindingFragment_ViewBinding<T extends ExpressageNoBindingFragment> implements Unbinder {
  protected T target;

  private View view2131296395;

  private View view2131296313;

  private View view2131296394;

  @UiThread
  public ExpressageNoBindingFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.fixeedittext1 = Utils.findRequiredViewAsType(source, R.id.fixeedittext1, "field 'fixeedittext1'", FixedEditText.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.headNo = Utils.findRequiredViewAsType(source, R.id.headNo, "field 'headNo'", LinearLayout.class);
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
    view = Utils.findRequiredView(source, R.id.imagebutton2, "field 'imagebutton2' and method 'onViewClicked'");
    target.imagebutton2 = Utils.castView(view, R.id.imagebutton2, "field 'imagebutton2'", ImageButton.class);
    view2131296394 = view;
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

    target.fixeedittext1 = null;
    target.text1 = null;
    target.headNo = null;
    target.imgbutton = null;
    target.recyle = null;
    target.button2 = null;
    target.imagebutton2 = null;

    view2131296395.setOnClickListener(null);
    view2131296395 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;
    view2131296394.setOnClickListener(null);
    view2131296394 = null;

    this.target = null;
  }
}
