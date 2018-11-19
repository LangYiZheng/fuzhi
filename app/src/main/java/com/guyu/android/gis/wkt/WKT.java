package com.guyu.android.gis.wkt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class WKT {

	/**
	 * 点 转换 JSON
	 * 
	 * @param wkt
	 * @param wkid
	 * @return
	 */
	public String getPOINTWktToJson(String wkt, int wkid) {

		String[] strHead = wkt.split("\\(");
		String strContent = strHead[1].substring(0, strHead[1].length() - 1);
		String[] strResult = strContent.split(" ");

		PointObject pointObject = new PointObject();
		pointObject.setX(Double.parseDouble(strResult[0]));
		pointObject.setY(Double.parseDouble(strResult[1]));

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		pointObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(pointObject);

	}

	/**
	 * 多点 转换 JSON
	 * 
	 * @param wkt
	 * @param wkid
	 * @return
	 */
	public String getMULTIPOINTWktToJson(String wkt, int wkid) {

		MultiPointObject multiIPointObject = new MultiPointObject();

		String ToTailWkt = wkt.substring(0, wkt.length() - 1);
		String[] strHead = ToTailWkt.split("\\(\\(");
		String strMiddle = strHead[1].substring(0, strHead[1].length() - 1);
		String[] strMiddles = strMiddle.split(",");

		List<Double[]> list = new ArrayList<Double[]>();

		for (int i = 0; i < strMiddles.length; i++) {

			if (i == 0) {

				String item = strMiddles[i].substring(0,
						strMiddles[i].length() - 1);
				String[] items = item.split(" ");
				Double[] listResult = new Double[] {
						Double.parseDouble(items[0]),
						Double.parseDouble(items[1]) };

				list.add(listResult);

			} else if (i == strMiddles.length) {

				String item = strMiddles[i]
						.substring(1, strMiddles[i].length());
				String[] items = item.split(" ");
				Double[] listResult = new Double[] {
						Double.parseDouble(items[0]),
						Double.parseDouble(items[1]) };

				list.add(listResult);

			} else {

				String strItem = strMiddles[i].trim();
				String item = strItem.substring(1, strItem.length() - 1);
				String[] items = item.split(" ");
				Double[] listResult = new Double[] {
						Double.parseDouble(items[0]),
						Double.parseDouble(items[1]) };

				list.add(listResult);

			}

		}

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		multiIPointObject.setPoints(list);
		multiIPointObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(multiIPointObject);

	}

	/**
	 * 线 转换 JSON
	 * 
	 * @param wkt
	 * @param wkid
	 * @return
	 */
	public String getLINESTRINGWktToJson(String wkt, int wkid) {

		LineStringObject lineStringObject = new LineStringObject();

		List<List<Double[]>> lists = new ArrayList<List<Double[]>>();
		List<Double[]> list = new ArrayList<Double[]>();

		String[] strHead = wkt.split("\\(");
		String strContent = strHead[1].substring(0, strHead[1].length() - 1);
		String[] strResult = strContent.split(",");

		for (int i = 0; i < strResult.length; i++) {

			String itme = strResult[i].trim();
			String[] items = itme.split(" ");
			Double[] listResult = new Double[] { Double.parseDouble(items[0]),
					Double.parseDouble(items[1]) };
			list.add(listResult);

		}

		lists.add(list);

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		lineStringObject.setPaths(lists);
		lineStringObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(lineStringObject);

	}

	/**
	 * 多线 转换 JSON
	 * 
	 * @param wkt
	 * @param wkid
	 * @return
	 */
	public String getMULTILINESTRINGWktToJson(String wkt, int wkid) {

		MultLinesStringObject lineStringObject = new MultLinesStringObject();

		List<List<Double[]>> lists = new ArrayList<List<Double[]>>();

		String ToTailWkt = wkt.substring(0, wkt.length() - 1);
		String[] strHead = ToTailWkt.split("\\(", 2);

		String[] strList = strHead[1].split("\\),\\(");

		for (int i = 0; i < strList.length; i++) {

			String item = strList[i].trim();
			item = item.substring(1, item.length() - 1);
			String[] items = item.split(",");

			List<Double[]> list = new ArrayList<Double[]>();

			for (int j = 0; j < items.length; j++) {

				String jItem = items[j].trim();
				String[] jItems = jItem.split(" ");

				Double[] listResult = new Double[] {
						Double.parseDouble(jItems[0]),
						Double.parseDouble(jItems[1]) };

				list.add(listResult);

			}

			lists.add(list);

		}

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		lineStringObject.setRings(lists);
		lineStringObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(lineStringObject);

	}

	public String getPOLYGONWktToJson(String wkt, int wkid) {

		PolygonObject polygonObject = new PolygonObject();

		List<List<Double[]>> lists = new ArrayList<List<Double[]>>();

		String ToTailWkt = wkt.substring(0, wkt.length() - 1);
		String[] strHead = ToTailWkt.split("\\(", 2);

		String[] strList = strHead[1].split("\\),\\(");

		for (int i = 0; i < strList.length; i++) {

			String item = strList[i].trim();
			item = item.substring(1, item.length() - 1);
			String[] items = item.split(",");

			List<Double[]> list = new ArrayList<Double[]>();

			for (int j = 0; j < items.length; j++) {

				String jItem = items[j].trim();
				jItem = jItem.replaceAll("\\(", "");
				jItem = jItem.replaceAll("\\)", "");
				String[] jItems = jItem.split(" ");

				Double[] listResult = new Double[] {
						Double.parseDouble(jItems[0]),
						Double.parseDouble(jItems[1]) };

				list.add(listResult);

			}

			lists.add(list);

		}

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		polygonObject.setRings(lists);
		polygonObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(polygonObject);
	}

	public String getMULTIPOLYGONWktToJson(String wkt, int wkid) {

		PolygonObject polygonObject = new PolygonObject();

		List<List<Double[]>> lists = new ArrayList<List<Double[]>>();

		String ToTailWkt = wkt.substring(0, wkt.length() - 1);
		String[] strHead = ToTailWkt.split("\\(", 2);
		ToTailWkt = strHead[1].substring(0, strHead[1].length() - 1);
		String[] strHeads = ToTailWkt.split("\\(", 2);

		String[] strList = strHeads[1].split("\\),\\(");

		if (strList.length == 1) {

			for (int i = 0; i < strList.length; i++) {

				String item = strList[i].trim();
				item = item.substring(1, item.length() - 1);
				String[] items = item.split(",");

				List<Double[]> list = new ArrayList<Double[]>();

				for (int j = 0; j < items.length; j++) {
					String jItem = items[j].trim();
					jItem = jItem.replaceAll("\\(", "");
					jItem = jItem.replaceAll("\\)", "");
					String[] jItems = jItem.split(" ");

					Double[] listResult = new Double[] {
							Double.parseDouble(jItems[0]),
							Double.parseDouble(jItems[1]) };

					list.add(listResult);

				}

				lists.add(list);

			}

		} else {

			for (int i = 0; i < strList.length; i++) {

				String item = strList[i].trim();
				item = item.substring(1, item.length() - 1);
				String[] items = item.split(",");

				List<Double[]> list = new ArrayList<Double[]>();

				for (int j = 1; j < items.length; j++) {
					String jItem = items[j].trim();
					jItem = jItem.replaceAll("\\(", "");
					jItem = jItem.replaceAll("\\)", "");
					String[] jItems = jItem.split(" ");

					Double[] listResult = new Double[] {
							Double.parseDouble(jItems[0]),
							Double.parseDouble(jItems[1]) };

					list.add(listResult);

				}

				lists.add(list);

			}

		}

		HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
		spatialReference.put("wkid", wkid);

		polygonObject.setRings(lists);
		polygonObject.setSpatialReference(spatialReference);

		Gson gson = new Gson();

		return gson.toJson(polygonObject);

	}

}
