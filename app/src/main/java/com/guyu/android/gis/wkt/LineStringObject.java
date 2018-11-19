package com.guyu.android.gis.wkt;

import java.util.HashMap;
import java.util.List;

public class LineStringObject {

	private List<List<Double[]>> paths;
	private HashMap<String, Integer> spatialReference;

	public List<List<Double[]>> getPaths() {
		return paths;
	}

	public void setPaths(List<List<Double[]>> paths) {
		this.paths = paths;
	}

	public HashMap<String, Integer> getSpatialReference() {
		return spatialReference;
	}

	public void setSpatialReference(HashMap<String, Integer> spatialReference) {
		this.spatialReference = spatialReference;
	}

}
