package com.guyu.android.utils;

import java.io.Serializable;

public class Point2D
  implements Serializable
{
  public double x;
  public double y;

  public Point2D()
  {
    this.x = 0.0D;
    this.y = 0.0D;
  }

  public Point2D(double paramDouble1, double paramDouble2)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Point2D));
    Point2D localPoint2D;
    do
    {
     
      localPoint2D = (Point2D)paramObject;
    }
    while ((this.x != localPoint2D.x) || (this.y != localPoint2D.y));
    return true;
  }
}