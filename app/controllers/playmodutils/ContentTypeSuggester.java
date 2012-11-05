package controllers.playmodutils;

import java.util.ArrayList;
import java.util.List;

import consts.playmodutils.APIConsts;

import models.playmodutils.ErrorMessage;
import models.playmodutils.ErrorReport;

import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.Http.Response;

import utils.playmodutils.ErrorHelper;
import utils.playmodutils.MIMEParse;
import utils.playmodutils.ThreadVar;

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

	private List<String> mimeTypesSupported = null;
	private String[] supportedResourceVersions = null;
	private String resourceNamespace = null;

	private String requestedContentType;
	private String requestedAcceptHeader;
	private String suggestedContentTypeHeader;
	private String resourceVersion;
	public static final String contentTypeSuffix = "; charset=utf-8";

	public ContentTypeSuggester(String[] supportedResourceVersions, String resourceNamespace) {
		this.supportedResourceVersions = supportedResourceVersions;
		this.resourceNamespace = resourceNamespace;
	}

	public void validateAcceptHeader(Response response, Header acceptHeader) {
		// accept header provided, inspect to see if a specific version
		// has been requested.
		// Examples:
		// --------
		// Accept: application/vnd.yell.deals.category+json
		// Accept: application/vnd.yell.deals.category-v1+json

		// only one value for acceptHeader should be provided

		if (acceptHeader != null && acceptHeader.toString().split("json").length > 2) {
			response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
			ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0004", ErrorHelper.getMessage("CAPI_CLI_ERR_0004"));
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
	public List<String> getMimeTypesSupported() {
		// only construct this list of mime types once for the application
		if (mimeTypesSupported == null) {
			// construct list
			// mimeTypesSupported = Arrays
			// .asList(StringUtils
			// .split("application/vnd.yell.deals.deal-v1+json,application/vnd.yell.deals.deal+json,application/json",
			// ','));
			mimeTypesSupported = new ArrayList<String>();
			for (int i = 0; i < supportedResourceVersions.length; i++) {
				String version = supportedResourceVersions[i];
				// construct content type
				String contentType = "application/" + resourceNamespace + "-" + version + "+json";
				mimeTypesSupported.add(contentType);
			}
			// now append the application/namespace+json catch all
			String contentType = "application/" + resourceNamespace + "+json";
			mimeTypesSupported.add(contentType);
			mimeTypesSupported.add(APIConsts.JSON_CONTENT_TYPE);
			// now append the application/json catch all
			mimeTypesSupported.add(APIConsts.JSON_CONTENT_TYPE);

		}
		return mimeTypesSupported;
	}

	public String getLatestContentType() throws InvalidAPIControllerConfigException {
		// return first content type in the list, the latest and greatest!
		String latestContentType = this.getMimeTypesSupported().get(0);
		// if no content type found this resource controller has not been
		// configured correctly
		if (latestContentType == null) {
			String message = ErrorHelper.getMessage("CAPI_SRV_ERR_0004");
			Logger.error(message);
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
	public void suggestContentType() throws InvalidAPIControllerConfigException {

		Header acceptHeader = request.headers.get("accept");

		// GET or DELETE requests MUST provide an Accept header
		if (request.method.equalsIgnoreCase("GET") | request.method.equalsIgnoreCase("DELETE")) {
			// check for accept header
			if (acceptHeader == null) {
				// error missing accept header
				response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
				ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0005", ErrorHelper.getMessage("CAPI_CLI_ERR_0005"));
				ErrorReport report = new ErrorReport(message);
				renderJSON(report);
			} else {
				// accept header provided
				this.validateAcceptHeader(response, acceptHeader);
				requestedAcceptHeader = acceptHeader.value();
				try {
					suggestedContentTypeHeader = MIMEParse.bestMatch(this.getMimeTypesSupported(), requestedAcceptHeader);
					if (suggestedContentTypeHeader == null | suggestedContentTypeHeader.equals("")) {
						// unsupported content type
						response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
						ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0006", ErrorHelper.getMessage("CAPI_CLI_ERR_0006",
								requestedAcceptHeader));
						ErrorReport report = new ErrorReport(message);
						renderJSON(report);

					}
				} catch (Exception e) {
					// error deriving content type
					response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
					ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0006", ErrorHelper.getMessage("CAPI_CLI_ERR_0006",
							requestedAcceptHeader));
					ErrorReport report = new ErrorReport(message);
					renderJSON(report);
				}
			}
		}

		// POST or PUT requests MUST provide a Content-Type header
		String contentType = request.contentType;

		// if accept header has been provided, e.g for versioning, default to
		// same as accept header
		// if content type has defaulted to text/html default to same as accept
		// header
		this.validateAcceptHeader(response, acceptHeader);
		if (contentType.equalsIgnoreCase("text/html") || acceptHeader != null)
			contentType = acceptHeader.value();

		if (request.method.equalsIgnoreCase("POST") | request.method.equalsIgnoreCase("PUT")) {
			// check for content type header
			if (contentType == null | contentType.equals("")) {
				// error missing content header
				response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
				ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0007", ErrorHelper.getMessage("CAPI_CLI_ERR_0007"));
				ErrorReport report = new ErrorReport(message);
				renderJSON(report);
			} else {
				// content type provided, inspect to see if a specific version
				// has been requested.
				// Examples:
				// --------
				// Content-Type: application/vnd.yell.deals.category+json
				// Content-Type: application/vnd.yell.deals.category-v1+json

				requestedContentType = contentType;
				try {
					suggestedContentTypeHeader = MIMEParse.bestMatch(this.getMimeTypesSupported(), requestedContentType);
					if (suggestedContentTypeHeader == null | suggestedContentTypeHeader.equals("")) {
						// unsupported content type
						response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
						ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0006", ErrorHelper.getMessage("CAPI_CLI_ERR_0006",
								requestedContentType));
						ErrorReport report = new ErrorReport(message);
						renderJSON(report);

					}
				} catch (Exception e) {
					// error deriving content type
					response.status = APIConsts.HTTP_STATUS_NOT_IMPLEMENTED;
					ErrorMessage message = new ErrorMessage("CAPI_CLI_ERR_0006", ErrorHelper.getMessage("CAPI_CLI_ERR_0006",
							requestedContentType));
					ErrorReport report = new ErrorReport(message);
					renderJSON(report);
				}
			}
		}

		// if suggested content type is generic json
		// default to latest version
		if (suggestedContentTypeHeader.equals(APIConsts.JSON_CONTENT_TYPE)) {
			suggestedContentTypeHeader = this.getLatestContentType();
		}
		// if suggested content type contains no identified version (search for
		// hypen in header)
		// default to latest version
		if (!suggestedContentTypeHeader.contains("-")) {
			suggestedContentTypeHeader = this.getLatestContentType();
		}
		// extract explicit version number of suggested content type if
		// specified
		// if not specified default to latest version
		// application/vnd.yell.deals.deal-v1+json

		int posOfHyphen = suggestedContentTypeHeader.indexOf('-');
		int posOfPlus = suggestedContentTypeHeader.indexOf('+');
		if (posOfHyphen > 0 & posOfPlus > 0) {
			// version
			resourceVersion = suggestedContentTypeHeader.substring(posOfHyphen + 1, posOfPlus);
		} else {
			// can't find version in content type!
			String message = ErrorHelper.getMessage("CAPI_SRV_ERR_0011", suggestedContentTypeHeader);
			Logger.error(message, suggestedContentTypeHeader);
			throw new InvalidAPIControllerConfigException();
		}

		Logger.info("Requested accept header: %s Requested content type: %s - Suggested content type: %s", requestedAcceptHeader,
				requestedContentType, suggestedContentTypeHeader);
		
		ThreadVar.setSuggestedContentTypeHeader(suggestedContentTypeHeader);
		ThreadVar.setRequestedAcceptHeader(requestedAcceptHeader);
		ThreadVar.setRequestedContentType(requestedContentType);
		ThreadVar.setResourceVersion(resourceVersion);

	}
}
