package com.guyu.android.gis.config;

import com.esri.core.geometry.Geometry;


public class AreaInfo
{
  public static Integer TYPE_CITY;
  public static Integer TYPE_CONTY;
  public static Integer TYPE_PROVINCE =0;//省级";
  public static Integer TYPE_STREET;
  public static Integer TYPE_TOWN;
  public String getParentID() {
	return parentID;
}

public void setParentID(String parentID) {
	this.parentID = parentID;
}

public static Integer TYPE_VILLAGE;
 
  public Geometry geometry = null;
  
  
  public Integer type;
  private String cantonId ;
  private String cantonName = "";
  private String parentID;
  static
  {
    TYPE_CITY =1;// "市级";
    TYPE_CONTY =2;// "县级";
    TYPE_TOWN = 3;//"镇级";
    TYPE_STREET =4;// "街道";
    TYPE_VILLAGE = 5;//"村级";
  }

  public AreaInfo()
  {
  }

  public AreaInfo(Integer paramString)
  {
    this.type = paramString;
  }

  public AreaInfo(String paramString1, String paramString2, Integer paramString3)
  {
    this.cantonId = paramString1;
    this.cantonName = paramString2;
    this.type = paramString3;
  }

  public AreaInfo(String paramString1, String paramString2, Integer paramString3, Geometry paramGeometry)
  {
    this.cantonId = paramString1;
    this.cantonName = paramString2;
    this.type = paramString3;
    this.geometry = paramGeometry;
  }

  public String getCantonId()
  {
    return this.cantonId;
  }

  public String getCantonName()
  {
    return this.cantonName;
  }

  public Integer getType()
  {
    return this.type;
  }

  public boolean isCity()
  {
      return this.type == TYPE_CITY;
  }

  public boolean isCounty()
  {
      return this.type == TYPE_CONTY;
  }

  public boolean isPro()
  {
      return this.type == TYPE_PROVINCE;
  }

  public boolean isStreet()
  {
      return this.type == TYPE_STREET || this.type == TYPE_TOWN;
  }

  public void setCantonId(String paramString)
  {
    this.cantonId = paramString;
  }

  public void setCantonName(String paramString)
  {
    this.cantonName = paramString;
  }

  public void setType(Integer paramString)
  {
    this.type = paramString;
  }
}