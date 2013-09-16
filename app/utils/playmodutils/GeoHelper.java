package utils.playmodutils;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.util.Arrays.asList;

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
			dist = sin(toRadians(lat1)) * sin(toRadians(lat2)) + cos(toRadians(lat1)) * cos(toRadians(lat2))
					* cos(toRadians(theta));
		}
		dist = toDegrees(acos(dist));
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		}else if (unit == 'm') {
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

	/**
	 * @deprecated Use {@link #calcBoundingBox(double, double, double)} instead
	 */
	@Deprecated
	public static List<GeoPoint> calcBoundingBox(double lat, double lon, int searchDistanceKm) {
		return calcBoundingBox(lat, lon, new Float(searchDistanceKm));
	}

	// will return two points
	public static List<GeoPoint> calcBoundingBox(double lat, double lon, double searchDistanceKm) {

		// calc bounding box co-ords
		double latInRadians = toRadians(lat);
		double lonInRadians = toRadians(lon);
		double halfSide = 1000 * searchDistanceKm / 2;

		// Radius of Earth at given latitude
		double radius = GeoHelper.WGS84EarthRadius(latInRadians);
		// Radius of the parallel at given latitude
		double pradius = radius * cos(latInRadians);

		double latFromInRadians = latInRadians - halfSide / radius;
		double lonFromInRadians = lonInRadians - halfSide / pradius;
		double latToInRadians = latInRadians + halfSide / radius;
		double lonToInRadians = lonInRadians + halfSide / pradius;

		GeoPoint fromPoint = new GeoPoint(toDegrees(latFromInRadians), toDegrees(lonFromInRadians));
		GeoPoint toPoint = new GeoPoint(toDegrees(latToInRadians), toDegrees(lonToInRadians));

		return asList(fromPoint,toPoint);
	}

	public static double convertKmToRadians(Integer searchDistanceKm) {
		// convert distance in km to a value in radians
		return searchDistanceKm / (2.0 * PI * 6371.0) * 360.0 * (PI / 180.0);
	}

	/**
	 * radians to degrees conversion examples: 
	 * 2*pi (about 6.28) radians = 260 degrees
	 * pi   (about 3.14) radians = 180 degrees
	 * pi/6 (about 0.52) radians = 30 degrees
	 * 
	 */
	public static double convertMtrsToRadians(Integer searchDistanceMtrs, Double lat) {
		double searchDistanceKm = searchDistanceMtrs/1000.0;
		return searchDistanceKm/(111.12 * Math.abs(Math.cos(Math.toRadians(lat))));	
	}
	
}
