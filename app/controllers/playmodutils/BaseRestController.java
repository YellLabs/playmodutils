package controllers.playmodutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import models.playmodutils.ErrorMessage;
import models.playmodutils.ErrorReport;
import play.Play;
import play.mvc.After;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Header;
import utils.playmodutils.ErrorHelper;

/* This is a generic Rest Controller to make interaction with the other API's a straightforwards process */

public class BaseRestController extends Controller {

	public static String suggestedContentTypeHeader;
	public static String requestedContentType;
	public static String requestedAcceptHeader;
	public static String resourceVersion;

	protected static void validatePagingParams(Integer startPage, Integer count) {
		validateLimits(startPage, count);
		try {
			if ((startPage!= null && (startPage < 1 || count==null))  || (count != null && (count < 1 || startPage==null))) renderBadPaging();
		}
		catch (Exception e) {
			 renderBadPaging();
		}
	}

	private static void validateLimits(Integer startPage, Integer count) {
		//validate that total count falls within int range
		//will render bad paging if values of count and startpage exceed 46340. 
		//This value should be a more than sufficient number of results when searching for nearby deals. 
		if ((startPage != null && startPage > Math.sqrt(Integer.MAX_VALUE)) || (count != null && count > Math.sqrt(Integer.MAX_VALUE))) {
			renderBadPaging();
		}
	}

	protected static void validatePagingParams(int totalCount, Integer startPage, int numPages) {
		validateLimits(startPage, totalCount);
		if (totalCount > 0 && (startPage!=null) && (startPage > numPages)) {
			renderBadPaging();
		}		
	}
	
	protected static void renderBadPaging() {
		response.status = Http.StatusCode.BAD_REQUEST;
		ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0033",
				ErrorHelper.getMessage("CAPI_CLI_ERR_0033"));
		ErrorReport report = new ErrorReport(message);
		renderJSON(report);
	}
	
	public static Gson getGsonParser() {
		GsonBuilder b = new GsonBuilder();
		// b.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		b.setDateFormat((String) Play.configuration.get("date.format"));
		Gson gson = b.create();

		return gson;
	}
	
	/**
	 * 
	 * @return the value currently assigned to application.version, which is bootstrapped from the version and dependencies files. 
	 * 		This can be provided to a /info or similar GET REST method to expose both the module versions and the deployed tag of the codebase 
	 */
	protected static String getVersionInfo() {
		return (String) Play.configuration.get("application.version");
	}
	
	/*
	 * Append access control header to all responses
	 */
	@After
	public static void appendAccessandContentTypeHeaders() {
		response.accessControl(Play.configuration
				.getProperty("access-control-allow-origin","*"));
		// append Vary: Accept to all responses
		response.headers.put("Vary", new Header("Vary","Accept"));
		
		response.contentType = suggestedContentTypeHeader+ContentTypeSuggester.contentTypeSuffix;
	}	
}