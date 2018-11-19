package com.guyu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout
  implements Checkable
{
  private static final int CHECKABLE_CHILD_INDEX = 1;
  private Checkable child;

  public CheckableLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public boolean isChecked()
  {
    return this.child.isChecked();
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.child = ((Checkable)getChildAt(1));
  }

  public void setChecked(boolean paramBoolean)
  {
    this.child.setChecked(paramBoolean);
  }

  public void toggle()
  {
    this.child.toggle();
  }
}