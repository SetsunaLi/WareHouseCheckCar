// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.find;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FindVatNoFragment_ViewBinding<T extends FindVatNoFragment> implements Unbinder {
  protected T target;

  private View view2131296302;

  private View view2131296303;

  private View view2131296313;

  private View view2131296304;

  private View view2131296321;

  @UiThread
  public FindVatNoFragment_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.layout2 = Utils.findRequiredViewAsType(source, R.id.layout2, "field 'layout2'", LinearLayout.class);
    target.recyle = Utils.findRequiredViewAsType(source, R.id.recyle, "field 'recyle'", RecyclerView.class);
    target.scrollView = Utils.findRequiredViewAsType(source, R.id.scrollView, "field 'scrollView'", HorizontalScrollView.class);
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    target.text3 = Utils.findRequiredViewAsType(source, R.id.text3, "field 'text3'", TextView.class);
    target.text4 = Utils.findRequiredViewAsType(source, R.id.text4, "field 'text4'", TextView.class);
    view = Utils.findRequiredView(source, R.id.button0, "field 'button0' and method 'onViewClicked'");
    target.button0 = Utils.castView(view, R.id.button0, "field 'button0'", Button.class);
    view2131296302 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button1, "field 'button1' and method 'onViewClicked'");
    target.button1 = Utils.castView(view, R.id.button1, "field 'button1'", Button.class);
    view2131296303 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button2, "field 'button2' and method 'onViewClicked'");
    target.button2 = Utils.castView(view, R.id.button2, "field 'button2'", Button.class);
    view2131296313 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.autoText1 = Utils.findRequiredViewAsType(source, R.id.autoText1, "field 'autoText1'", AutoCompleteTextView.class);
    target.autoText2 = Utils.findRequiredViewAsType(source, R.id.autoText2, "field 'autoText2'", AutoCompleteTextView.class);
    target.autoText3 = Utils.findRequiredViewAsType(source, R.id.autoText3, "field 'autoText3'", AutoCompleteTextView.class);
    view = Utils.findRequiredView(source, R.id.button10, "field 'button10' and method 'onViewClicked'");
    target.button10 = Utils.castView(view, R.id.button10, "field 'button10'", Button.class);
    view2131296304 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button9, "field 'button9' and method 'onViewClicked'");
    target.button9 = Utils.castView(view, R.id.button9, "field 'button9'", Button.class);
    view2131296321 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.dlAssets = Utils.findRequiredViewAsType(source, R.id.drawer_layout, "field 'dlAssets'", DrawerLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.layout2 = null;
    target.recyle = null;
    target.scrollView = null;
    target.text2 = null;
    target.text3 = null;
    target.text4 = null;
    target.button0 = null;
    target.button1 = null;
    target.button2 = null;
    target.autoText1 = null;
    target.autoText2 = null;
    target.autoText3 = null;
    target.button10 = null;
    target.button9 = null;
    target.dlAssets = null;

    view2131296302.setOnClickListener(null);
    view2131296302 = null;
    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;
    view2131296304.setOnClickListener(null);
    view2131296304 = null;
    view2131296321.setOnClickListener(null);
    view2131296321 = null;

    this.target = null;
  }
}
