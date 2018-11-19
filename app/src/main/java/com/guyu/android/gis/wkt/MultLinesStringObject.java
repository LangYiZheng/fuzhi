package com.guyu.android.gis.wkt;

import java.util.HashMap;
import java.util.List;

public class MultLinesStringObject {

	private List<List<Double[]>> rings;
	private HashMap<String, Integer> spatialReference;

	public List<List<Double[]>> getRings() {
		return rings;
	}

	public void setRings(List<List<Double[]>> rings) {
		this.rings = rings;
	}

	public HashMap<String, Integer> getSpatialReference() {
		return spatialReference;
	}

	public void setSpatialReference(HashMap<String, Integer> spatialReference) {
		this.spatialReference = spatialReference;
	}

}