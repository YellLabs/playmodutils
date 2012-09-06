package controllers.playmodutils;

import controllers.playmodutils.BaseRestController;

public class TestRESTController extends BaseRestController{

	public static void normalResponse() {
		String jsonString = "{\"test\" : \"result\"}";
		
		renderJSON(jsonString);
	}

	public static void notFoundResponse() {
		notFound();
	}

	public static void notFoundResponseWithString() {
		notFound("not found this!");
	}

	public static void errorResponse() {
		error();
	}
	
	public static void errorResponseWithException() {
		error(new Exception("It's all gone wrong!"));
	}
}
