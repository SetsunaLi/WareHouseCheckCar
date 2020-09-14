// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AboutFragment_ViewBinding<T extends AboutFragment> implements Unbinder {
  protected T target;

  @UiThread
  public AboutFragment_ViewBinding(T target, View source) {
    this.target = target;

    target.appTitle = Utils.findRequiredViewAsType(source, R.id.appTitle, "field 'appTitle'", TextView.class);
    target.companyTitle = Utils.findRequiredViewAsType(source, R.id.companyTitle, "field 'companyTitle'", TextView.class);
    target.appVersionTitle = Utils.findRequiredViewAsType(source, R.id.appVersionTitle, "field 'appVersionTitle'", TextView.class);
    target.periodicReport = Utils.findRequiredViewAsType(source, R.id.periodicReport, "field 'periodicReport'", TextView.class);
    target.appVersionRow = Utils.findRequiredViewAsType(source, R.id.appVersionRow, "field 'appVersionRow'", TableRow.class);
    target.sledTitle = Utils.findRequiredViewAsType(source, R.id.sledTitle, "field 'sledTitle'", TextView.class);
    target.moduleVersionTitle = Utils.findRequiredViewAsType(source, R.id.moduleVersionTitle, "field 'moduleVersionTitle'", TextView.class);
    target.moduleVersion = Utils.findRequiredViewAsType(source, R.id.moduleVersion, "field 'moduleVersion'", TextView.class);
    target.moduleVersionRow = Utils.findRequiredViewAsType(source, R.id.moduleVersionRow, "field 'moduleVersionRow'", TableRow.class);
    target.radioVersionTitle = Utils.findRequiredViewAsType(source, R.id.radioVersionTitle, "field 'radioVersionTitle'", TextView.class);
    target.radioVersion = Utils.findRequiredViewAsType(source, R.id.radioVersion, "field 'radioVersion'", TextView.class);
    target.radioVersionRow = Utils.findRequiredViewAsType(source, R.id.radioVersionRow, "field 'radioVersionRow'", TableRow.class);
    target.copyRight = Utils.findRequiredViewAsType(source, R.id.copyRight, "field 'copyRight'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.appTitle = null;
    target.companyTitle = null;
    target.appVersionTitle = null;
    target.periodicReport = null;
    target.appVersionRow = null;
    target.sledTitle = null;
    target.moduleVersionTitle = null;
    target.moduleVersion = null;
    target.moduleVersionRow = null;
    target.radioVersionTitle = null;
    target.radioVersion = null;
    target.radioVersionRow = null;
    target.copyRight = null;

    this.target = null;
  }
}
