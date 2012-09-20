package playmodutils.unit;

import static java.lang.Math.sqrt;

import java.util.List;

import models.playmodutils.GeoPoint;

import org.junit.Test;

import play.test.UnitTest;
import utils.playmodutils.GeoHelper;

// useful resources 
// http://www.getlatlon.com/ get lat lon of a location
// http://www.darrinward.com/cgi-bin/map-lat-long-points.php plot points on a map by lat lon
// http://www.csgnetwork.com/gpsdistcalc.html  GPS distance calculator

public class GeoTest extends UnitTest {

	private final double DELTA = 0.05;
	
	private final double ANY_LAT = 51.52144143859368;
	private final double ANY_LON = -0.11306047439575195;
	private final int SEARCH_DISTANCE_KM = 10;
	private final double SEARCH_DISTANCE_KM_NOT_INT = .5;

	@Test
	public void testCalcDistanceBetweenPointsInKM() {
		// Yell Labs
		double fromLat = 51.52144143859368;
		double fromLon = -0.11306047439575195;

		// One Reading Central
		double toLat = 51.45823201083629;
		double toLon = -0.9679555892944336;
		// check in km
		double distance = GeoHelper.calcDistanceBetweenPoints(fromLat, fromLon, toLat, toLon, 'K');
		assertEquals(59.602, distance, DELTA);
	}

	@Test
	public void testCalcDistanceBetweenPointsInNauticalMiles() {
		// Yell Labs
		double fromLat = 51.52144143859368;
		double fromLon = -0.11306047439575195;

		// One Reading Central
		double toLat = 51.45823201083629;
		double toLon = -0.9679555892944336;
		// check in nautical miles for the hell of it
		double distance = GeoHelper.calcDistanceBetweenPoints(fromLat, fromLon, toLat, toLon, 'N');
		assertEquals(32.161, distance, DELTA);
	}

	@Test
	public void testThatThereIsNoDistanceBetweenAPointAndItself() {
		double latitude = 50.8524;
		double longitude = -1.1813;
		double distance = GeoHelper.calcDistanceBetweenPoints(latitude, longitude, latitude, longitude, 'm');
		assertEquals(0.0, distance, DELTA);
	}

	@Test
	public void testThatCalcBoundingBoxWithIntDistanceKmReturnsTwoPoints() {
		List<GeoPoint> points = GeoHelper.calcBoundingBox(ANY_LAT, ANY_LON, SEARCH_DISTANCE_KM);
		assertEquals(points.size(), 2);
	}

	@Test
	public void testThatCalcBoundingBoxWithIntDistanceKmReturnsPointsThatAreCornersOfASquareOfTheRightSize() {
		List<GeoPoint> points = GeoHelper.calcBoundingBox(ANY_LAT, ANY_LON, SEARCH_DISTANCE_KM);
		
		double calculatedDistance = GeoHelper.calcDistanceBetweenPoints(points.get(0).getLat(), points.get(0).getLon(), points
				.get(1).getLat(), points.get(1).getLon(), 'K');
		// hypotenuse = sqrt of (side a squared + side b squared)
		double expectedDistanceBetweenCorners = sqrt((SEARCH_DISTANCE_KM * SEARCH_DISTANCE_KM) + (SEARCH_DISTANCE_KM * SEARCH_DISTANCE_KM));
		assertEquals(expectedDistanceBetweenCorners, calculatedDistance, DELTA);
	}
	
	@Test
	public void testThatCalcBoundingBoxWithDecimalDistanceKmReturnsTwoPoints() {
		List<GeoPoint> points = GeoHelper.calcBoundingBox(ANY_LAT, ANY_LON, SEARCH_DISTANCE_KM_NOT_INT);
		assertEquals(points.size(), 2);
	}

	@Test
	public void testThatCalcBoundingBoxWithDecimalDistanceKmReturnsPointsThatAreCornersOfASquareOfTheRightSize() {
		List<GeoPoint> points = GeoHelper.calcBoundingBox(ANY_LAT, ANY_LON, SEARCH_DISTANCE_KM_NOT_INT);
		
		double calculatedDistance = GeoHelper.calcDistanceBetweenPoints(points.get(0).getLat(), points.get(0).getLon(), points
				.get(1).getLat(), points.get(1).getLon(), 'K');
		// hypotenuse = sqrt of (side a squared + side b squared)
		double expectedDistanceBetweenCorners = sqrt((SEARCH_DISTANCE_KM_NOT_INT * SEARCH_DISTANCE_KM_NOT_INT) + (SEARCH_DISTANCE_KM_NOT_INT * SEARCH_DISTANCE_KM_NOT_INT));
		assertEquals(expectedDistanceBetweenCorners, calculatedDistance, DELTA);
	}

}
