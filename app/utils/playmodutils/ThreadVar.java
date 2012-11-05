package utils.playmodutils;

public final class ThreadVar {
	
	private static ThreadLocal<String> suggestedContentTypeHeader = new ThreadLocal<String>();
	private static ThreadLocal<String> requestedAcceptHeader = new ThreadLocal<String>();
	private static ThreadLocal<String> requestedContentType = new ThreadLocal<String>();
	private static ThreadLocal<String> resourceVersion = new ThreadLocal<String>();
	
	public static String getSuggestedContentTypeHeader() {
		return suggestedContentTypeHeader.get();
	}
	
	public static void setSuggestedContentTypeHeader(String str) {
		suggestedContentTypeHeader.set(str);
	}
	
	public static String getRequestedAcceptHeader() {
		return requestedAcceptHeader.get();
	}
	
	public static void setRequestedAcceptHeader(String str) {
		requestedAcceptHeader.set(str);
	}
	
	public static String getRequestedContentType() {
		return requestedContentType.get();
	}
	
	public static void setRequestedContentType(String str) {
		requestedContentType.set(str);
	}
	
	public static String getResourceVersion() {
		return resourceVersion.get();
	}
	
	public static void setResourceVersion(String str) {
		resourceVersion.set(str);
	}
}
