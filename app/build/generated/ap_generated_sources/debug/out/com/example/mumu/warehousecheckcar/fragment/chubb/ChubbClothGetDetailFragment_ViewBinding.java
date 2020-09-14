// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.chubb;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChubbClothGetDetailFragment_ViewBinding<T extends ChubbClothGetDetailFragment> implements Unbinder {
  protected T target;

  @UiThread
  public ChubbClothGetDetailFragment_ViewBinding(T target, View source) {
    this.target = target;

    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.text1 = Utils.findRequiredViewAsType(source, R.id.text1, "field 'text1'", TextView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.recyle = null;
    target.text1 = null;
    target.text2 = null;

    this.target = null;
  }
}
