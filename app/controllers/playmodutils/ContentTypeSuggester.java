package controllers.playmodutils;

import static consts.playmodutils.APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
import static consts.playmodutils.APIConsts.JSON_CONTENT_TYPE;

import java.util.ArrayList;
import java.util.List;

import models.playmodutils.ErrorMessage;
import models.playmodutils.ErrorReport;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.Http.Response;
import static utils.playmodutils.ErrorHelper.getMessage;
import utils.playmodutils.MIMEParse;
import utils.playmodutils.ThreadCopy;
import exceptions.playmodutils.InvalidAPIControllerConfigException;

/**
 * This class provides common code for determining the content type to render
 * from our controllers, particularly to support versioning A
 * ContentTypeSuggester is constructed per Controller with its own values for
 * resource name space and versions/mimeTypes supported, and the public fields
 * are set on interception of the request: Usage is always as follows for each
 * controller:
 * 
 * private static ContentTypeSuggester contentTypeSuggester = new
 * ContentTypeSuggester(supportedResourceVersions, resourceNamespace);
 * 
 * 
 * @Before public static void getSuggestedContentType() throws
 *         InvalidAPIControllerConfigException {
 *         contentTypeSuggester.suggestContentType(suggestedContentTypeHeader,
 *         requestedAcceptHeader, requestedContentType, resourceVersion);
 *         suggestedContentTypeHeader =
 *         contentTypeSuggester.suggestedContentTypeHeader;
 *         requestedAcceptHeader = contentTypeSuggester.requestedAcceptHeader;
 *         requestedContentType = contentTypeSuggester.requestedContentType;
 *         resourceVersion = contentTypeSuggester.resourceVersion; }
 * 
 * @author indy
 * 
 */
public class ContentTypeSuggester extends Controller {

	public static final String CONTENT_TYPE_SUFFIX = "; charset=utf-8";

	private static void validateAcceptHeader(Response response, Header acceptHeader) {
		// accept header provided, inspect to see if a specific version
		// has been requested.
		// Examples:
		// --------
		// Accept: application/vnd.yell.deals.category+json
		// Accept: application/vnd.yell.deals.category-v1+json

		// only one value for acceptHeader should be provided

		if (acceptHeader != null && acceptHeader.toString().split("json").length > 2) {
			response.status = HTTP_STATUS_NOT_IMPLEMENTED;
			ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0004", getMessage("CAPI_CLI_ERR_0004"));
			ErrorReport report = new ErrorReport(message);
			Controller.renderJSON(report);
		}
	}

	/*
	 * This method constructs a list of accepted mime types for the resource
	 * from the follow parameters - latestResourceVersion (This is the version
	 * to be used if none specified) - supportedResourceVersions (This is the
	 * entire list of supported versions including latest) - resourceNamespace
	 * (This is the namespace prefix added to uniquely identify the resouce)
	 */
	private static List<String> getMimeTypesSupported(String[] supportedResourceVersions, String resourceNamespace) {
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

	private static String getLatestContentType(List<String> mimeTypesSupported) throws InvalidAPIControllerConfigException {
		// return first content type in the list, the latest and greatest!
		String latestContentType = mimeTypesSupported.get(0);
		// if no content type found this resource controller has not been
		// configured correctly
		if (latestContentType == null) {
			Logger.error(getMessage("CAPI_SRV_ERR_0004"));
			throw new InvalidAPIControllerConfigException();
		}
		return latestContentType;
	}

	/*
	 * This methods is used to retrieve the accept header from the client
	 * request. The client header can request a particular content type
	 * representation of the API for each resource The variables
	 * requestedContentType and suggestedContentType will be set at the end of
	 * the method if successful. If unsuccessful the client will be informed.
	 * 
	 * suggestedContentType can then be used to render appropriate responses
	 * back to the client.
	 */
	public static SuggestedVersion suggestContentType(String[] supportedResourceVersions, String resourceNamespace)
			throws InvalidAPIControllerConfigException {

		String requestedAcceptHeader = null;
		String suggestedContentTypeHeader = null;

		Header acceptHeader = request.headers.get("accept");

		List<String> mimeTypesSupported = ContentTypeSuggester.getMimeTypesSupported(supportedResourceVersions, resourceNamespace);

		// GET or DELETE requests MUST provide an Accept header
		if (request.method.equalsIgnoreCase("GET") | request.method.equalsIgnoreCase("DELETE")) {
			// check for accept header
			if (acceptHeader == null) {
				// error missing accept header
				response.status = HTTP_STATUS_NOT_IMPLEMENTED;
				renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0005", getMessage("CAPI_CLI_ERR_0005"))));
			} else {
				// accept header provided
				ContentTypeSuggester.validateAcceptHeader(response, acceptHeader);
				requestedAcceptHeader = acceptHeader.value();
				suggestedContentTypeHeader = getSuggestedContentTypeHeader(requestedAcceptHeader, mimeTypesSupported);
			}
		}

		// POST or PUT requests MUST provide a Content-Type header
		String contentType = request.contentType;

		// if accept header has been provided, e.g for versioning, default to
		// same as accept header
		// if content type has defaulted to text/html default to same as accept
		// header
		ContentTypeSuggester.validateAcceptHeader(response, acceptHeader);
		if (contentType.equalsIgnoreCase("text/html") || acceptHeader != null)
			contentType = acceptHeader.value();

		String requestedContentType = null;
		if (request.method.equalsIgnoreCase("POST") | request.method.equalsIgnoreCase("PUT")) {
			// check for content type header
			if (nullOrEmpty(contentType)) {
				// error missing content header
				response.status = HTTP_STATUS_NOT_IMPLEMENTED;
				renderJSON(new ErrorReport(new ErrorMessage("CAPI_CLI_ERR_0007", getMessage("CAPI_CLI_ERR_0007"))));
			} else {
				// content type provided, inspect to see if a specific version
				// has been requested.
				// Examples:
				// --------
				// Content-Type: application/vnd.yell.deals.category+json
				// Content-Type: application/vnd.yell.deals.category-v1+json

				requestedContentType = contentType;
				suggestedContentTypeHeader = getSuggestedContentTypeHeader(requestedContentType, mimeTypesSupported);
			}
		}

		// if suggested content type is generic json or contains no identified
		// version (search for hypen in header) default to latest version
		if (suggestedContentTypeHeader.equals(JSON_CONTENT_TYPE) || !suggestedContentTypeHeader.contains("-")) {
			suggestedContentTypeHeader = ContentTypeSuggester.getLatestContentType(mimeTypesSupported);
		}
		// extract explicit version number of suggested content type if
		// specified
		// if not specified default to latest version
		// application/vnd.yell.deals.deal-v1+json

		String resourceVersion = getResourceVersion(suggestedContentTypeHeader);

		Logger.info("Requested accept header: %s Requested content type: %s - Suggested content type: %s", requestedAcceptHeader,
				requestedContentType, suggestedContentTypeHeader);

		return new SuggestedVersion(suggestedContentTypeHeader, requestedAcceptHeader, requestedContentType, resourceVersion);
	}

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

	private static boolean nullOrEmpty(String str) {
		return str == null | str.equals("");
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
}
