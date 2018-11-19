package com.guyu.android.utils;

import com.esri.android.map.Layer;
import com.esri.android.map.MapView;

public class MapUtils {
	/**
	 * 根据图层名找图层
	 * 
	 * @param layerName
	 *            图层名
	 * @return
	 */
	public static Layer getLayerByName(MapView map,String layerName) {
		Layer[] alllayers = map.getLayers();
		for (int i = 0; i < alllayers.length; i++) {
			if (layerName.equals(alllayers[i].getName())) {
				return alllayers[i];
			}
		}
		return null;
	}
}
