// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.repaif_in;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RepaifInDetailFragment_ViewBinding<T extends RepaifInDetailFragment> implements Unbinder {
  protected T target;

  @UiThread
  public RepaifInDetailFragment_ViewBinding(T target, View source) {
    this.target = target;

    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    target.checkbox1 = Utils.findRequiredViewAsType(source, R.id.checkbox1, "field 'checkbox1'", CheckBox.class);
    target.item1 = Utils.findRequiredViewAsType(source, R.id.item1, "field 'item1'", TextView.class);
    target.edit1 = Utils.findRequiredViewAsType(source, R.id.edit1, "field 'edit1'", EditText.class);
    target.item3 = Utils.findRequiredViewAsType(source, R.id.item3, "field 'item3'", TextView.class);
    target.layout1 = Utils.findRequiredViewAsType(source, R.id.layout1, "field 'layout1'", LinearLayout.class);
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.text2 = null;
    target.checkbox1 = null;
    target.item1 = null;
    target.edit1 = null;
    target.item3 = null;
    target.layout1 = null;
    target.recyle = null;
    target.text1 = null;

    this.target = null;
  }
}
