package playmodutils.unit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import models.playmodutils.GeoPoint;

import org.junit.Before;
import org.junit.Test;

import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;
import utils.playmodutils.GeoHelper;


// useful resources 
// http://www.getlatlon.com/ get lat lon of a location
// http://www.darrinward.com/cgi-bin/map-lat-long-points.php plot points on a map by lat lon
// http://www.csgnetwork.com/gpsdistcalc.html  GPS distance calculator

public class GeoTest extends UnitTest {

	@Before
	public void setUp() {
	    //Fixtures.deleteDatabase();
	    //Fixtures.loadModels("data.yml");
	}
    
    @Test
    public void testCalcDistanceBetweenPointsKM() {

    	double fromLat=0;
    	double fromLon=0;
    	double toLat=0;
    	double toLon=0;
    	// distance from same point should be zero
    	double distance = GeoHelper.calcDistanceBetweenPoints(fromLat, fromLon, toLat, toLon,'K');
    	assertEquals(0.0, distance,0.00001);
    	
    	// Yell Labs
    	fromLat = 51.52144143859368;
    	fromLon = -0.11306047439575195;
    	
    	// One Reading Central
    	toLat = 51.45823201083629;
    	toLon = -0.9679555892944336;
    	// check in km
    	distance = GeoHelper.calcDistanceBetweenPoints(fromLat, fromLon, toLat, toLon,'K');
    	assertEquals(59.602, distance,0.05);
    	// check in nautical miles for the hell of it
    	distance = GeoHelper.calcDistanceBetweenPoints(fromLat, fromLon, toLat, toLon,'N');
    	assertEquals(32.161, distance,0.05);
    	
    }
    
    @Test
    public void testCalcBoundingBox() {

    	// Yell Labs
    	double lat = 51.52144143859368;
    	double lon = -0.11306047439575195;
    	int searchDistanceKm = 10;
    	// distance from same point should be zero
    	List<GeoPoint> points = GeoHelper.calcBoundingBox(lat, lon, searchDistanceKm);
    	assertNotNull(points);
    	// check points returned are valid.
    	
    	// calc distance between corners on 10km box
    	double distance = GeoHelper.calcDistanceBetweenPoints(points.get(0).getLat(), points.get(0).getLon(), points.get(1).getLat(), points.get(1).getLon(), 'K');
    	double cornerToCornerDist = Math.sqrt((searchDistanceKm*searchDistanceKm)+(searchDistanceKm*searchDistanceKm)); // hypotenuse = sqrt of (side a squared + side b squared)
    	assertEquals(cornerToCornerDist,distance,0.05);

    	searchDistanceKm = 50;
    	// distance from same point should be zero
    	points = GeoHelper.calcBoundingBox(lat, lon, searchDistanceKm);
    	assertNotNull(points);
    	// check points returned are valid.
    	
    	// calc distance between corners on 50km box
    	distance = GeoHelper.calcDistanceBetweenPoints(points.get(0).getLat(), points.get(0).getLon(), points.get(1).getLat(), points.get(1).getLon(), 'K');
    	cornerToCornerDist = Math.sqrt((searchDistanceKm*searchDistanceKm)+(searchDistanceKm*searchDistanceKm)); // hypotenuse = sqrt of (side a squared + side b squared)
    	assertEquals(cornerToCornerDist,distance,0.08);

    	
    }
   }
