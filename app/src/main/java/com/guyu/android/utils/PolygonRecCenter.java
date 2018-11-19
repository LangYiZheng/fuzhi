package com.guyu.android.utils;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;

public class PolygonRecCenter {

	public static Point getPoint(Polygon polygon) {
		Point point = new Point();
		double pointMinX = 0.0000000000D;
		double pointMinY = 0.0000000000D;
		double pointMaxX = 0.0000000000D;
		double pointMaxY = 0.0000000000D;
		for (int i = 0; i < polygon.getPointCount(); i++) {
			Point pointTempi = polygon.getPoint(i);
			if (i > 1) {
				if (pointMinX > pointTempi.getX()) {
					pointMinX = pointTempi.getX();
				}

				if (pointMinY > pointTempi.getY()) {
					pointMinY = pointTempi.getY();
				}

				if (pointMaxX < pointTempi.getX()) {
					pointMaxX = pointTempi.getX();
				}

				if (pointMaxY < pointTempi.getY()) {
					pointMaxY = pointTempi.getY();
				}
			} else {
				pointMinX = pointTempi.getX();
				pointMinY = pointTempi.getY();
				pointMaxX = pointTempi.getX();
				pointMaxY = pointTempi.getY();
			}
		}
		point.setX((pointMinX + pointMaxX) / 2);
		point.setY((pointMinY + pointMaxY) / 2);

		return point;
	}

}
