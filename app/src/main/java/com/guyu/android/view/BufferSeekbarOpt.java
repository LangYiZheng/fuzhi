package com.guyu.android.view;

import com.guyu.android.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.geometry.Unit.UnitType;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.guyu.android.gis.activity.MainActivity;
import com.guyu.android.gis.app.GisQueryApplication;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;



public class BufferSeekbarOpt {

	private MainActivity activity;
	private SeekBar mSeekBar;
	private EditText mBufferValueEditText;
	private double m_nMiddleValue = 0.0D;
	private MapView map;
	private GraphicsLayer resultGeomLayer;
	private Geometry bufferGeometry;
	private Polygon bufferResultGeometry;
	private Boolean initOK = false;

	public BufferSeekbarOpt(MapView paramMap, MainActivity paramActivity) {
		this.map = paramMap;
		this.activity = paramActivity;
		this.mSeekBar = ((SeekBar) paramActivity.findViewById(R.id.seek_buffer));
		this.mSeekBar.setEnabled(false);
		this.mSeekBar.setOnSeekBarChangeListener(new MOnSeekBar());
		this.mSeekBar.setProgress((int) (10.0D * this.m_nMiddleValue));
		this.mBufferValueEditText = ((EditText) paramActivity
				.findViewById(R.id.et_buffer_value_setting));
		this.mBufferValueEditText
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramView) {

					}
				});
	}

	public void init() {
		if (!initOK) {
			initOK = true;
			this.mSeekBar.setProgress(0);
			this.mSeekBar.setEnabled(false);
			resultGeomLayer = new GraphicsLayer();
			this.map.addLayer(resultGeomLayer);
		}
	}

	public Geometry getBufferGeometry() {
		return bufferGeometry;
	}

	public void setBufferGeometrys(Geometry bufferGeometry) {
		this.bufferGeometry = bufferGeometry;
		if (this.mSeekBar.isEnabled()) {
			doBuffer();
		}
	}

	public void setSeekBarEnabled(Boolean enable) {
		if (!enable) {
			this.mSeekBar.setProgress(0);
			if (resultGeomLayer != null) {
				resultGeomLayer.removeAll();
			}

		}
		this.mSeekBar.setEnabled(enable);

	}

	public Polygon getBufferResultGeometry() {
		if (m_nMiddleValue == 0.0D) {
			return (Polygon) bufferGeometry;
		} else {
			return bufferResultGeometry;
		}

	}

	private final double metersToDegrees(double distanceInMeters) {
		return distanceInMeters / 111319.5;
	}

	public void doBuffer() {
		resultGeomLayer.removeAll();
		try {
			// 配置文件里有坐标系配置，使用坐标系里面的，否则使用地图的

			Polygon p = null;
			if (GisQueryApplication.getApp().getProjectconfig().getSpatialreference() != null) {
				Unit unit = GisQueryApplication.getApp().getProjectconfig()
						.getSpatialreference().getUnit();

				double adjustedAccuracy = m_nMiddleValue;
				if (unit.getUnitType() == UnitType.ANGULAR) {
					adjustedAccuracy = metersToDegrees(m_nMiddleValue);
				} else {
					unit = Unit.create(LinearUnit.Code.METER);
				}
				p = GeometryEngine
						.buffer(bufferGeometry,
								GisQueryApplication.getApp().getProjectconfig()
										.getSpatialreference(),
								adjustedAccuracy, unit);
			} else {
				Unit unit = map.getSpatialReference().getUnit();

				double adjustedAccuracy = m_nMiddleValue;
				if (unit.getUnitType() == UnitType.ANGULAR) {
					adjustedAccuracy = metersToDegrees(m_nMiddleValue);
				} else {
					unit = Unit.create(LinearUnit.Code.METER);
				}
				p = GeometryEngine.buffer(bufferGeometry,
						map.getSpatialReference(), adjustedAccuracy, unit);
			}

			bufferResultGeometry = p;
			SimpleFillSymbol sfs = new SimpleFillSymbol(Color.GREEN);
			sfs.setOutline(new SimpleLineSymbol(Color.RED, 4,
					com.esri.core.symbol.SimpleLineSymbol.STYLE.SOLID));
			sfs.setAlpha(25);
			Graphic g = new Graphic(p, sfs);
			resultGeomLayer.addGraphic(g);

		} catch (Exception ex) {
			Log.d("Test buffer", ex.getMessage());

		}

	}

	class MOnSeekBar implements SeekBar.OnSeekBarChangeListener {
		public void onProgressChanged(SeekBar paramSeekBar, int paramInt,
				boolean paramBoolean) {
			double d = paramSeekBar.getProgress() / 10.0D;
			BufferSeekbarOpt.this.m_nMiddleValue = d;
			if (BufferSeekbarOpt.this.mBufferValueEditText == null)
				return;
			BufferSeekbarOpt.this.mBufferValueEditText.setText("缓冲" + d + "米");
		}

		public void onStartTrackingTouch(SeekBar paramSeekBar) {
		}

		public void onStopTrackingTouch(SeekBar paramSeekBar) {
			BufferSeekbarOpt.this.doBuffer();
		}
	}

	public void dispose() {
		if (initOK) {
			initOK = false;
			resultGeomLayer.removeAll();
			map.removeLayer(resultGeomLayer);
			resultGeomLayer = null;
		}

	}
}
