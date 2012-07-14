package consts.playmodutils;

/* Constants for API's in general */

public final class APIConsts {

	// message operations
	public static final String CREATE_OPERATION = "CREATE";
	public static final String UPDATE_OPERATION = "UPDATE";
	public static final String DELETE_OPERATION = "DELETE";
	
	public static final String ANNOTATE_OPERATION = "ANNOTATE";

	// Http status codes not implemented by play framework
	public static final int HTTP_STATUS_NOT_IMPLEMENTED = 406;
	
	// others
	public static final String JSON_CONTENT_TYPE = "application/json";
}
