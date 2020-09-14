// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.outsource_in;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class In_OutSourceDetailFragment_ViewBinding<T extends In_OutSourceDetailFragment> implements Unbinder {
  protected T target;

  @UiThread
  public In_OutSourceDetailFragment_ViewBinding(T target, View source) {
    this.target = target;

    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    target.text3 = Utils.findRequiredViewAsType(source, R.id.text3, "field 'text3'", TextView.class);
    target.text4 = Utils.findRequiredViewAsType(source, R.id.text4, "field 'text4'", TextView.class);
    target.checkbox1 = Utils.findRequiredViewAsType(source, R.id.checkbox1, "field 'checkbox1'", CheckBox.class);
    target.edittext1 = Utils.findRequiredViewAsType(source, R.id.edittext1, "field 'edittext1'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.recyle = null;
    target.text1 = null;
    target.text2 = null;
    target.text3 = null;
    target.text4 = null;
    target.checkbox1 = null;
    target.edittext1 = null;

    this.target = null;
  }
}
