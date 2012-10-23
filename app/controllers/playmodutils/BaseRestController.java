package controllers.playmodutils;

import models.playmodutils.ErrorMessage;
import models.playmodutils.ErrorReport;
import models.playmodutils.SourceVersion;
import play.Logger;
import play.Play;
import play.mvc.After;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Header;
import play.mvc.results.Error;
import play.mvc.results.NotFound;
import utils.playmodutils.ErrorHelper;
import utils.playmodutils.SourceVersionHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/* This is a generic Rest Controller to make interaction with the other API's a straightforwards process */

public class BaseRestController extends Controller {

	public static String suggestedContentTypeHeader;
	public static String requestedContentType;
	public static String requestedAcceptHeader;
	public static String resourceVersion;
	
	private static SourceVersion sourceVersion;

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
			renderBadPagingLimits();
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

	protected static void renderBadPagingLimits() {
		response.status = Http.StatusCode.BAD_REQUEST;
		ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0008",
		ErrorHelper.getMessage("CAPI_CLI_ERR_0008"));
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
	/*
	 * THIS METHOD HAS BEEN REMOVED - is has been superceded by getBuildInfo which renders a json response
	 * 
	 * protected static String getVersionInfo() {
		return (String) Play.configuration.get("application.version");
	}*/
	/*
	 * This method returns details of source the application was built from 
	 */
	protected static SourceVersion getSourceVersionInfo() {
		
		// if version info is null - populate it first time
		if(sourceVersion==null)
		{
			// try to get details from CI build first
			sourceVersion = SourceVersionHelper.getSourceVersionFromCIDeploy();
			
			if(sourceVersion==null)
			{
				// try from local build
				sourceVersion = SourceVersionHelper.getSourceVersionFromLocalDeploy();
			}
			
		}
		
		return sourceVersion;
	}
	
	public static void getBuildInfo() 
	{
		SourceVersion version = getSourceVersionInfo();
		if(version!=null)
		{
			response.setHeader("content-type","application/json");
			renderJSON(sourceVersion);
		}
		else
		{
			String jsonText = "{\"Error\": \"build info not available\"}";
			response.setHeader("content-type","application/json");
			renderText(jsonText);
		}
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
	
	/**
     * Send a 404 Not Found response
     * @param what The Not Found resource name
     */
    protected static void notFound(String what) {
    	// override request format to json to return json formatted 404 page
    	request.format="json";
        throw new NotFound(what);
    }
    
    /**
     * Send a 404 Not Found response if object is null
     * @param o The object to check
     */
    protected static void notFoundIfNull(Object o) {
    	// override request format to json to return json formatted 404 page
    	request.format="json";
    	if (o == null) {
            notFound();
        }
    }

    /**
     * Send a 404 Not Found response if object is null
     * @param o The object to check
     * @param what The Not Found resource name
     */
    protected static void notFoundIfNull(Object o, String what) {
    	// override request format to json to return json formatted 404 page
    	request.format="json";
    	if (o == null) {
            notFound(what);
        }
    }

    /**
     * Send a 404 Not Found reponse
     */
    protected static void notFound() {
    	// override request format to json to return json formatted 404 page
    	request.format="json";
    	throw new NotFound("");
    }
    /**
     * Send a 5xx Error response
     * @param status The exact status code
     * @param reason The reason
     */
    protected static void error(int status, String reason) {
    	// override request format to json to return json formatted 500 page
    	request.format="json";
    	throw new Error(status, reason);
    }

    /**
     * Send a 500 Error response
     * @param reason The reason
     */
    protected static void error(String reason) {
    	// override request format to json to return json formatted 500 page
    	request.format="json";
    	throw new Error(reason);
    }

    /**
     * Send a 500 Error response
     * @param reason The reason
     */
    protected static void error(Exception reason) {
    	// override request format to json to return json formatted 500 page
    	request.format="json";
    	Logger.error(reason, "error()");
        throw new Error(reason.toString());
    }

    /**
     * Send a 500 Error response
     */
    protected static void error() {
    	// override request format to json to return json formatted 500 page
    	request.format="json";
    	throw new Error("Internal Error");
    }
	
}