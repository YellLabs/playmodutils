package utils.playmodutils;

import controllers.playmodutils.SuggestedVersion;

public final class ThreadCopy {
	
	private static ThreadLocal<String> suggestedContentTypeHeader = new ThreadLocal<String>();
	private static ThreadLocal<String> requestedAcceptHeader = new ThreadLocal<String>();
	private static ThreadLocal<String> requestedContentType = new ThreadLocal<String>();
	private static ThreadLocal<String> resourceVersion = new ThreadLocal<String>();
	
	public static String getSuggestedContentTypeHeader() {
		return suggestedContentTypeHeader.get();
	}
	
	public static String getRequestedAcceptHeader() {
		return requestedAcceptHeader.get();
	}
	
	public static String getRequestedContentType() {
		return requestedContentType.get();
	}
	
	public static String getResourceVersion() {
		return resourceVersion.get();
	}
	
	public static void set(SuggestedVersion suggestedVersion) {
		suggestedContentTypeHeader.set(suggestedVersion.suggestedContentTypeHeader);
		requestedAcceptHeader.set(suggestedVersion.requestedAcceptHeader);
		requestedContentType.set(suggestedVersion.requestedContentType);
		resourceVersion.set(suggestedVersion.resourceVersion);
	}
}
