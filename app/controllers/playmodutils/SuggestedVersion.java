package controllers.playmodutils;

import utils.playmodutils.ThreadCopy;

public class SuggestedVersion {
	
	public final String suggestedContentTypeHeader;
	public final String requestedAcceptHeader;
	public final String requestedContentType;
	public final String resourceVersion;
	
	public SuggestedVersion(String suggestedContentTypeHeader, String requestedAcceptHeader, String requestedContentType,
			String resourceVersion) {
		this.suggestedContentTypeHeader = suggestedContentTypeHeader;
		this.requestedAcceptHeader = requestedAcceptHeader;
		this.requestedContentType = requestedContentType;
		this.resourceVersion = resourceVersion;
	}

}
