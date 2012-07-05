package utils.playmodutils;

import java.util.ArrayList;
import java.util.List;

/* This class contains methods for Geospatial calculations */
public class GeoHelper {


public static double calcDistanceBetweenPoints(double lat1, double lon1, double lat2, double lon2, char unit) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
		  dist = Math.acos(dist);
		  dist = Math.toDegrees(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684;
		    }
		  return (dist);
		}

		public static double WGS84EarthRadius(double lat)
		{
				// http://en.wikipedia.org/wiki/Earth_radius
				double WGS84_a = 6378137.0; // Major semiaxis [m]
				double WGS84_b = 6356752.3; // Minor semiaxis [m]
				double An = WGS84_a*WGS84_a * Math.cos(lat);
				double Bn = WGS84_b*WGS84_b * Math.sin(lat);
				double Ad = WGS84_a * Math.cos(lat);
				double Bd = WGS84_b * Math.sin(lat);
				return Math.sqrt( (An*An + Bn*Bn)/(Ad*Ad + Bd*Bd) );
		}
		
		// will return two points
		public static List<GeoPoint> calcBoundingBox(double lat, double lon, int searchDistanceKm) {
			
			// calc bounding box co-ords
	        			
			double latInRadians = Math.toRadians(lat);
	        double lonInRadians = Math.toRadians(lon);
	        double halfSide = 1000*searchDistanceKm/2;

	        // Radius of Earth at given latitude
	        double radius = GeoHelper.WGS84EarthRadius(latInRadians);
	        // Radius of the parallel at given latitude
	        double pradius = radius*Math.cos(latInRadians);

	        double latMinInRadians = latInRadians - halfSide/radius;
	        double latMaxInRadians = latInRadians + halfSide/radius;
	        double lonMinInRadians = lonInRadians - halfSide/pradius;
	        double lonMaxInRadians = lonInRadians + halfSide/pradius;
	        // convert back to degrees for query
	        double boxFromLat = Math.toDegrees(latMinInRadians);
	        double boxToLat = Math.toDegrees(latMaxInRadians);
	        double boxFromLon = Math.toDegrees(lonMinInRadians);
	        double boxToLon = Math.toDegrees(lonMaxInRadians);
	        
	        GeoPoint fromPoint = new GeoPoint(boxFromLat, boxFromLon);
	        GeoPoint toPoint = new GeoPoint(boxToLat, boxToLon);

	        List<GeoPoint> points = new ArrayList<GeoPoint>();
	        points.add(fromPoint);
	        points.add(toPoint);
	        
	        return points;
		}

        public static double convertKmToRadians(Integer searchDistanceKm) {
			// convert distance in km to a value in radians
			
			// calc circumference of earth
			// = 2 * PI * r (6371lm)
			//double circ = 2.0 * Math.PI * 6371.0;
			// divide distance by circumference of earth
			// = result/ 40030.17.... 
			//double percent = searchDistanceKm/circ;
			// multiply result by 360
			// = result * 360  (so if distance was 40030 result = 360)
			//double degrees = percent * 360.0;
			// convert degrees to a radians value
			// = result * PI/180
			//double radians = degrees * Math.PI/180.0;
			//  distanceInKm/(2 * math.pi * 6371)*360*(math.pi/180)

			
			double radians = (double)searchDistanceKm/(2.0 * Math.PI * 6371.0) * 360.0 * (Math.PI/180.0);
			
			
			return radians;
		}

}
