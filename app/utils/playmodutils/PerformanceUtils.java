package utils.playmodutils;

import play.Logger;

public class PerformanceUtils {
	
	public static long getStartTime(){
		return System.currentTimeMillis();
	}

	public static void logInfoTimeTaken(long startTime, String perfName) {

		Logger.info(buildLogString(startTime,perfName));

	}

	public static void logDebugTimeTaken(long startTime, String perfName) {

		Logger.debug(buildLogString(startTime,perfName));

	}

	private static String buildLogString(long startTime, String perfName){
		long endTime = System.currentTimeMillis();
		long elapsedMillis = (endTime - startTime);
		long elapsedSecs = (endTime - startTime) / 1000;
		long remainderMillis = elapsedMillis % 1000;
		long elapsedMins = elapsedSecs / 60;
		long remainderSecs = elapsedSecs % 60;
		return "logTimeTaken [" + perfName + "] " + elapsedMins
				+ " mins " + remainderSecs + " secs "
				+ remainderMillis + " ms.)";
	}
}
