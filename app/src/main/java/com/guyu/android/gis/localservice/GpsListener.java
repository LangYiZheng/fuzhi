package com.guyu.android.gis.localservice;

import com.esri.core.geometry.Point;

public interface GpsListener
{
  void GpsChangedListener(Point paramPoint2D);
}