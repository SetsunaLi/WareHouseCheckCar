// Generated code from Butter Knife. Do not modify!
package com.example.mumu.warehousecheckcar.fragment.find;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.mumu.warehousecheckcar.R;
import com.example.mumu.warehousecheckcar.view.FixedEditText;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FindTpNoFragmentf_ViewBinding<T extends FindTpNoFragmentf> implements Unbinder {
  protected T target;

  private View view2131296322;

  private View view2131296301;

  private View view2131296488;

  private View view2131296503;

  private View view2131296302;

  private View view2131296303;

  private View view2131296313;

  private View view2131296417;

  @UiThread
  public FindTpNoFragmentf_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.fixeedittext1 = Utils.findRequiredViewAsType(source, R.id.fixeedittext1, "field 'fixeedittext1'", FixedEditText.class);
    view = Utils.findRequiredView(source, R.id.buttonAdd, "field 'buttonAdd' and method 'onViewClicked'");
    target.buttonAdd = Utils.castView(view, R.id.buttonAdd, "field 'buttonAdd'", Button.class);
    view2131296322 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.button, "field 'button' and method 'onViewClicked'");
    target.button = Utils.castView(view, R.id.button, "field 'button'", Button.class);
    view2131296301 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.layout2 = Utils.findRequiredViewAsType(source, R.id.layout2, "field 'layout2'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.recyle, "field 'recyle' and method 'onViewClicked'");
    target.recyle = Utils.castView(view, R.id.recyle, "field 'recyle'", RecyclerView.class);
    view2131296488 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.scrollView, "field 'scrollView' and method 'onViewClicked'");
    target.scrollView = Utils.castView(view, R.id.scrollView, "field 'scrollView'", HorizontalScrollView.class);
    view2131296503 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.text2 = Utils.findRequiredViewAsType(source, R.id.text2, "field 'text2'", TextView.class);
    target.text3 = Utils.findRequiredViewAsType(source, R.id.text3, "field 'text3'", TextView.class);
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
    view = Utils.findRequiredView(source, R.id.layout1, "field 'layout1' and method 'onViewClicked'");
    target.layout1 = Utils.castView(view, R.id.layout1, "field 'layout1'", LinearLayout.class);
    view2131296417 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.listview = Utils.findRequiredViewAsType(source, R.id.listview, "field 'listview'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.fixeedittext1 = null;
    target.buttonAdd = null;
    target.button = null;
    target.layout2 = null;
    target.recyle = null;
    target.scrollView = null;
    target.text2 = null;
    target.text3 = null;
    target.button0 = null;
    target.button1 = null;
    target.button2 = null;
    target.layout1 = null;
    target.listview = null;

    view2131296322.setOnClickListener(null);
    view2131296322 = null;
    view2131296301.setOnClickListener(null);
    view2131296301 = null;
    view2131296488.setOnClickListener(null);
    view2131296488 = null;
    view2131296503.setOnClickListener(null);
    view2131296503 = null;
    view2131296302.setOnClickListener(null);
    view2131296302 = null;
    view2131296303.setOnClickListener(null);
    view2131296303 = null;
    view2131296313.setOnClickListener(null);
    view2131296313 = null;
    view2131296417.setOnClickListener(null);
    view2131296417 = null;

    this.target = null;
  }
}
