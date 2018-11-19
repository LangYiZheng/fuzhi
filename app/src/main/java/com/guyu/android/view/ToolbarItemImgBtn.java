package com.guyu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ToolbarItemImgBtn extends ImageButton
{
  public ToolbarItemImgBtn(Context paramContext)
  {
    super(paramContext);
  }

  public ToolbarItemImgBtn(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public ToolbarItemImgBtn(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
}

/* Location:           C:\Users\Administrator\Desktop\一张图查询\classes_dex2jar.jar
 * Qualified Name:     com.mtkj.land.view.ToolbarItemImgBtn
 * JD-Core Version:    0.5.3
 */