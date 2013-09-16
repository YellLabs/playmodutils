package controllers.playmodutils;

import static consts.playmodutils.APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
import static consts.playmodutils.APIConsts.JSON_CONTENT_TYPE;
import static utils.playmodutils.ErrorHelper.getMessage;

import java.util.ArrayList;
import java.util.List;

import models.playmodutils.ErrorMessage;
import models.playmodutils.ErrorReport;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.Http.Response;
import utils.playmodutils.MIMEParse;
import exceptions.playmodutils.InvalidAPIControllerConfigException;

/**
 * This class provides common code for determining the content type to render
 * from our controllers, particularly to support versioning A
 * ContentTypeSuggester is constructed per Controller with its own values for
 * resource name space and versions/mimeTypes supported, and the public fields
 * are set on interception of the request: Usage is always as follows for each
 * controller.

 */
public class ContentTypeSuggester extends Controller {

	public static final String CONTENT_TYPE_SUFFIX = "; charset=utf-8";

	/**
	 * This method constructs a list of accepted mime types for the resource
	 * from the follow parameters - latestResourceVersion (This is the version
	 * to be used if none specified) - supportedResourceVersions (This is the
	 * entire list of supported versions including latest) - resourceNamespace
	 * (This is the namespace prefix added to uniquely identify the resource)
	 * 
	 * @param supportedResourceVersions
	 * @param resourceNamespace
	 * @return
	 */
	public static List<String> getMimeTypesSupported(String[] supportedResourceVersions, String resourceNamespace) {
		List<String> mimeTypesSupported = new ArrayList<String>();
		for (String version : supportedResourceVersions) {
			mimeTypesSupported.add("application/" + resourceNamespace + "-" + version + "+json");
		}
		// now append the application/namespace+json catch all
		mimeTypesSupported.add("application/" + resourceNamespace + "+json");
		// now append the application/json catch all
		mimeTypesSupported.add(JSON_CONTENT_TYPE);
		return mimeTypesSupported;
	}

	/**
	 * This methods is used to retrieve the accept header from the client
	 * request. The client header can request a particular content type
	 * representation of the API for each resource The variables
	 * requestedContentType and suggestedContentType will be set at the end of
	 * the method if successful. If unsuccessful the client will be informed.
	 * 
	 * suggestedContentType can then be used to render appropriate responses
	 * back to the client.
	 * 
	 * @param supportedResourceVersions
	 * @param resourceNamespace
	 * @return
	 * @throws InvalidAPIControllerConfigException
	 */
	public static SuggestedVersion suggestContentType(String[] supportedResourceVersions, String resourceNamespace) throws InvalidAPIControllerConfigException {

		List<String> mimeTypesSupported = ContentTypeSuggester.getMimeTypesSupported(supportedResourceVersions, resourceNamespace);

		String acceptHeader = ContentTypeSuggester.validateAcceptHeader(response, request.headers.get("accept")); 
		
		String suggestedContentTypeHeader = null;
		
		if (request.method.equalsIgnoreCase("GET") || request.method.equalsIgnoreCase("DELETE")) {
			suggestedContentTypeHeader = getSuggestedContentTypeHeader(acceptHeader, mimeTypesSupported);			
		}
		else if (request.method.equalsIgnoreCase("POST") || request.method.equalsIgnoreCase("PUT")) {
			
			// check for content type header
			if (nullOrEmpty(request.contentType) || request.contentType.equals("text/html")) {
				
				// error missing content header
				response.status = HTTP_STATUS_NOT_IMPLEMENTED;
				renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0007", getMessage("CAPI_CLI_ERR_0007"))));
				
			} else {
				
				// content type provided, inspect to see if a specific version has been requested.
				// Examples:
				// --------
				// Content-Type: application/vnd.yell.deals.category+json
				// Content-Type: application/vnd.yell.deals.category-v1+json
					
				suggestedContentTypeHeader = getSuggestedContentTypeHeader(request.contentType, mimeTypesSupported);
			}
		}

		// if suggested content type is generic json or contains no identified
		// version (search for hyphen in header) default to latest version
		if (suggestedContentTypeHeader.equals(JSON_CONTENT_TYPE) || !suggestedContentTypeHeader.contains("-")) {
			suggestedContentTypeHeader = mimeTypesSupported.get(0);
		}
		
		// extract explicit version number of suggested content type if specified
		// if not specified, default to latest version
		String resourceVersion = getResourceVersion(suggestedContentTypeHeader);

		Logger.info("Requested accept header: %s Requested content type: %s - Suggested content type: %s", acceptHeader, request.contentType, suggestedContentTypeHeader);

		return new SuggestedVersion(suggestedContentTypeHeader, acceptHeader, request.contentType, resourceVersion);
	}

	// =============================================================
	
	private static String getSuggestedContentTypeHeader(String requestHeader, List<String> mimeTypesSupported) {
		String suggestedContentTypeHeader = null;
		try {
			suggestedContentTypeHeader = MIMEParse.bestMatch(mimeTypesSupported, requestHeader);
			if (nullOrEmpty(suggestedContentTypeHeader)) {
				// unsupported content type
				response.status = HTTP_STATUS_NOT_IMPLEMENTED;
				renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0006", getMessage("CAPI_CLI_ERR_0006", requestHeader))));
			}

		} catch (Exception e) {
			// error deriving content type
			response.status = HTTP_STATUS_NOT_IMPLEMENTED;
			renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0006", getMessage("CAPI_CLI_ERR_0006", requestHeader))));
		}

		return suggestedContentTypeHeader;
	}

	private static String validateAcceptHeader(Response response, Header acceptHeader) {
		
		// accept header MUST be provided
		if (acceptHeader == null) {
			// error missing accept header
			response.status = HTTP_STATUS_NOT_IMPLEMENTED;
			renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0005", getMessage("CAPI_CLI_ERR_0005"))));
		}

		// accept header provided, inspect to see if a specific version
		// has been requested.
		// Examples:
		// --------
		// Accept: application/vnd.yell.deals.category+json
		// Accept: application/vnd.yell.deals.category-v1+json

		// only one value for acceptHeader should be provided
		if (acceptHeader != null && acceptHeader.toString().split("json").length > 2) {
			response.status = HTTP_STATUS_NOT_IMPLEMENTED;
			Controller.renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0004", getMessage("CAPI_CLI_ERR_0004"))));
		}
		
		return acceptHeader.value();
		
	}
	
	private static String getResourceVersion(String suggestedContentTypeHeader) throws InvalidAPIControllerConfigException {
		int posOfHyphen = suggestedContentTypeHeader.indexOf('-');
		int posOfPlus = suggestedContentTypeHeader.indexOf('+');
		if (posOfHyphen > 0 & posOfPlus > 0) {
			// version
			return suggestedContentTypeHeader.substring(posOfHyphen + 1, posOfPlus);
		} else {
			// can't find version in content type!
			String message = getMessage("CAPI_SRV_ERR_0011", suggestedContentTypeHeader);
			Logger.error(message, suggestedContentTypeHeader);
			throw new InvalidAPIControllerConfigException();
		}
	}

	private static boolean nullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
}
