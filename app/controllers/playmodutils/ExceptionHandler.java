package controllers.playmodutils;

import static play.mvc.Http.StatusCode.BAD_REQUEST;
import static utils.playmodutils.ErrorHelper.errorReport;
import play.Logger;
import play.mvc.Catch;
import play.mvc.Controller;
import utils.playmodutils.ErrorHelper;

public class ExceptionHandler extends Controller {
	@Catch(Exception.class)
	public static void internalError(Exception ex) {
		Logger.error("Unexpected error: %s", ex);
		response.status = BAD_REQUEST;
		renderJSON(errorReport("CAPI_CLI_ERR_0009", ex.getMessage()));
	}
}
