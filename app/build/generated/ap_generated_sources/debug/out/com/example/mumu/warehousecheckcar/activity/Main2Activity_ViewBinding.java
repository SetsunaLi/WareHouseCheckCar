// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Main2Activity_ViewBinding<T extends Main2Activity> implements Unbinder {
  protected T target;

  @UiThread
  public Main2Activity_ViewBinding(T target, View source) {
    this.target = target;

    target.mFrame = Utils.findRequiredViewAsType(source, R.id.content_frame, "field 'mFrame'", FrameLayout.class);
    target.navView = Utils.findRequiredViewAsType(source, R.id.nav_view, "field 'navView'", NavigationView.class);
    target.drawerLayout = Utils.findRequiredViewAsType(source, R.id.drawer_layout, "field 'drawerLayout'", DrawerLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mFrame = null;
    target.navView = null;
    target.drawerLayout = null;

    this.target = null;
  }
}
