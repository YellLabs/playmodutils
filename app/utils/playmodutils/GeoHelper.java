package utils.playmodutils;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import java.util.ArrayList;
import java.util.List;

import models.playmodutils.GeoPoint;

/* This class contains methods for Geospatial calculations */
public class GeoHelper {

	public static double calcDistanceBetweenPoints(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist;
		if (lat1 == lat2 && lon1 == lon2) {
			dist = 1; // sin^2(x) + cos^2(x) = 1
		} else {
			dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * cos(toRadians(theta));
		}
		dist = toDegrees(acos(dist));
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		}
		if (unit == 'm') {
			dist = dist * 1609.344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	public static double WGS84EarthRadius(double lat) {
		// http://en.wikipedia.org/wiki/Earth_radius
		double WGS84_a = 6378137.0; // Major semiaxis [m]
		double WGS84_b = 6356752.3; // Minor semiaxis [m]
		double An = WGS84_a * WGS84_a * cos(lat);
		double Bn = WGS84_b * WGS84_b * sin(lat);
		double Ad = WGS84_a * cos(lat);
		double Bd = WGS84_b * sin(lat);
		return sqrt((An * An + Bn * Bn) / (Ad * Ad + Bd * Bd));
	}

	// will return two points
	public static List<GeoPoint> calcBoundingBox(double lat, double lon, int searchDistanceKm) {

		// calc bounding box co-ords

		double latInRadians = toRadians(lat);
		double lonInRadians = toRadians(lon);
		double halfSide = 1000 * searchDistanceKm / 2;

		// Radius of Earth at given latitude
		double radius = GeoHelper.WGS84EarthRadius(latInRadians);
		// Radius of the parallel at given latitude
		double pradius = radius * cos(latInRadians);

		double latMinInRadians = latInRadians - halfSide / radius;
		double latMaxInRadians = latInRadians + halfSide / radius;
		double lonMinInRadians = lonInRadians - halfSide / pradius;
		double lonMaxInRadians = lonInRadians + halfSide / pradius;
		// convert back to degrees for query
		double boxFromLat = toDegrees(latMinInRadians);
		double boxToLat = toDegrees(latMaxInRadians);
		double boxFromLon = toDegrees(lonMinInRadians);
		double boxToLon = toDegrees(lonMaxInRadians);

		GeoPoint fromPoint = new GeoPoint(boxFromLat, boxFromLon);
		GeoPoint toPoint = new GeoPoint(boxToLat, boxToLon);

		List<GeoPoint> points = new ArrayList<GeoPoint>();
		points.add(fromPoint);
		points.add(toPoint);

		return points;
	}

	public static double convertKmToRadians(Integer searchDistanceKm) {
		// convert distance in km to a value in radians

		return searchDistanceKm / (2.0 * PI * 6371.0) * 360.0 * (PI / 180.0);

	}

}
