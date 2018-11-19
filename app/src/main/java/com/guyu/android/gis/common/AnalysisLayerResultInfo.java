package com.guyu.android.gis.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.esri.android.map.Layer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.guyu.android.gis.config.LayerConfig;

public class AnalysisLayerResultInfo {
	private Layer fealyr;// 图层对象
	private String layerName;// 图层名称
	private double area;// 占地面积
	private Polygon polygonForAnalysis;// 分析图形
	private FeatureSet analysisFs;// 压覆结果集FeatureSet
	private FeatureResult analysisFr;//压覆结果集FeatureResult
	private LayerConfig layerConfig;
	private SpatialReference analysisSR;
	private ArrayList<String> originalfields = null;
	private ArrayList<String> displayfields = null;
	private ArrayList<AnalysisIntersectionResultInfo> intersections = new ArrayList<AnalysisIntersectionResultInfo>();

	/**
	 * 分析ArcGISFeatureLayer压覆结果
	 * @param callBack
	 */
	public void doResultAnalysis_Graphics(CallBack callBack) {
		intersections.clear();
		if (analysisFs == null) {
			originalfields = new ArrayList<String>();
			displayfields = new ArrayList<String>();
			return;
		}
		Graphic gra[] = analysisFs.getGraphics();
		double area_intersect = 0.0D;
		for (int i = 0; i < gra.length; i++) {
			Graphic graphic = gra[i];
			Map mapObj = graphic.getAttributes();
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo = new AnalysisIntersectionResultInfo();
			if (originalfields == null) {
				originalfields = new ArrayList<String>();
				displayfields = new ArrayList<String>();
				Object[] fields = mapObj.keySet().toArray();
				String[] mOriginalfields = layerConfig.getOriginalfields();
				String[] mDisplayfields = layerConfig.getDisplayfields();
				//如果有配置字段显示顺序，按配置来
				if (mOriginalfields != null && mOriginalfields.length > 0
						&& mOriginalfields.length == mDisplayfields.length) {
					for (int j = 0; j < mOriginalfields.length; j++) {
						originalfields.add(mOriginalfields[j]);
						displayfields.add(mDisplayfields[j]);
					}
				} else {
					for (int j = 0; j < fields.length; j++) {
						originalfields.add("" + fields[j]);
						displayfields.add("" + fields[j]);
					}
				}

			}
			Geometry intersectionGeometry = GeometryEngine.intersect(
					polygonForAnalysis, graphic.getGeometry(), analysisSR);
			double d = GeometryEngine.geodesicArea(intersectionGeometry,
					analysisSR,
					(AreaUnit) Unit.create(AreaUnit.Code.SQUARE_METER));
			analysisIntersectionResultInfo.setArea(d);
			analysisIntersectionResultInfo.setGeometry(intersectionGeometry);
			analysisIntersectionResultInfo.setAttributes(mapObj);
			area_intersect += d;
			intersections.add(analysisIntersectionResultInfo);
		}
		setArea(area_intersect);
		Log.i("当前分析图层【" + fealyr.getName() + "】", "占用面积---->" + area_intersect);
		callBack.execute();
	}
	/**
	 * 分析FeatureLayer压覆结果
	 * @param callBack
	 */
	public void doResultAnalysis_Features(CallBack callBack) {
		intersections.clear();
		if (analysisFr == null) {
			originalfields = new ArrayList<String>();
			displayfields = new ArrayList<String>();
			return;
		}
		Iterator<Object> iterator_feature = analysisFr.iterator();
		double area_intersect = 0.0D;
		while(iterator_feature.hasNext())
		{
			Feature tmFeature = (Feature)iterator_feature.next();
			Map mapObj = tmFeature.getAttributes();
			AnalysisIntersectionResultInfo analysisIntersectionResultInfo = new AnalysisIntersectionResultInfo();
			if (originalfields == null) {
				originalfields = new ArrayList<String>();
				displayfields = new ArrayList<String>();
				Object[] fields = mapObj.keySet().toArray();
				String[] mOriginalfields = layerConfig.getOriginalfields();
				String[] mDisplayfields = layerConfig.getDisplayfields();
				//如果有配置字段显示顺序，按配置来
				if (mOriginalfields != null && mOriginalfields.length > 0
						&& mOriginalfields.length == mDisplayfields.length) {
					for (int j = 0; j < mOriginalfields.length; j++) {
						originalfields.add(mOriginalfields[j]);
						displayfields.add(mDisplayfields[j]);
					}
				} else {
					for (int j = 0; j < fields.length; j++) {
						originalfields.add("" + fields[j]);
						displayfields.add("" + fields[j]);
					}
				}

			}
			Geometry intersectionGeometry = GeometryEngine.intersect(
					polygonForAnalysis, tmFeature.getGeometry(), analysisSR);
			double d = GeometryEngine.geodesicArea(intersectionGeometry,
					analysisSR,
					(AreaUnit) Unit.create(AreaUnit.Code.SQUARE_METER));
			analysisIntersectionResultInfo.setArea(d);
			analysisIntersectionResultInfo.setGeometry(intersectionGeometry);
			analysisIntersectionResultInfo.setAttributes(mapObj);
			area_intersect += d;
			intersections.add(analysisIntersectionResultInfo);
		}		
		setArea(area_intersect);
		Log.i("当前分析图层【" + fealyr.getName() + "】", "占用面积---->" + area_intersect);
		callBack.execute();
	}

	public ArrayList<AnalysisIntersectionResultInfo> getIntersections() {
		return intersections;
	}

	public void setIntersections(
			ArrayList<AnalysisIntersectionResultInfo> intersections) {
		this.intersections = intersections;
	}

	

	public ArrayList<String> getOriginalfields() {
		return originalfields;
	}

	public void setOriginalfields(ArrayList<String> originalfields) {
		this.originalfields = originalfields;
	}

	public ArrayList<String> getDisplayfields() {
		return displayfields;
	}

	public void setDisplayfields(ArrayList<String> displayfields) {
		this.displayfields = displayfields;
	}

	public Layer getFealyr() {
		return fealyr;
	}

	public void setFealyr(Layer fealyr) {
		this.fealyr = fealyr;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public Polygon getPolygonForAnalysis() {
		return polygonForAnalysis;
	}

	public void setPolygonForAnalysis(Polygon polygonForAnalysis) {
		this.polygonForAnalysis = polygonForAnalysis;
	}

	public FeatureSet getAnalysisFs() {
		return analysisFs;
	}

	public void setAnalysisFs(FeatureSet analysisFs) {
		this.analysisFs = analysisFs;
	}

	public FeatureResult getAnalysisFr() {
		return analysisFr;
	}
	public void setAnalysisFr(FeatureResult analysisFr) {
		this.analysisFr = analysisFr;
	}
	public LayerConfig getLayerConfig() {
		return layerConfig;
	}

	public void setLayerConfig(LayerConfig layerConfig) {
		this.layerConfig = layerConfig;
	}

	public SpatialReference getAnalysisSR() {
		return analysisSR;
	}

	public void setAnalysisSR(SpatialReference analysisSR) {
		this.analysisSR = analysisSR;
	}
}
