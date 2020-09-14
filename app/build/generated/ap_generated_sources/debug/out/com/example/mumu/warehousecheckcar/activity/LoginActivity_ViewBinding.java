// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding<T extends LoginActivity> implements Unbinder {
  protected T target;

  private View view2131296428;

  @UiThread
  public LoginActivity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.loginProgress = Utils.findRequiredViewAsType(source, R.id.login_progress, "field 'loginProgress'", ProgressBar.class);
    target.username = Utils.findRequiredViewAsType(source, R.id.username, "field 'username'", AutoCompleteTextView.class);
    target.password = Utils.findRequiredViewAsType(source, R.id.password, "field 'password'", EditText.class);
    view = Utils.findRequiredView(source, R.id.login_button, "field 'loginButton' and method 'onViewClicked'");
    target.loginButton = Utils.castView(view, R.id.login_button, "field 'loginButton'", Button.class);
    view2131296428 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.emailLoginForm = Utils.findRequiredViewAsType(source, R.id.email_login_form, "field 'emailLoginForm'", LinearLayout.class);
    target.checkbox1 = Utils.findRequiredViewAsType(source, R.id.checkbox1, "field 'checkbox1'", CheckBox.class);
    target.checkbox2 = Utils.findRequiredViewAsType(source, R.id.checkbox2, "field 'checkbox2'", CheckBox.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.loginProgress = null;
    target.username = null;
    target.password = null;
    target.loginButton = null;
    target.emailLoginForm = null;
    target.checkbox1 = null;
    target.checkbox2 = null;

    view2131296428.setOnClickListener(null);
    view2131296428 = null;

    this.target = null;
  }
}
