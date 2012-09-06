package playmodutils.functional;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

/* Tests for templates */

public class TemplateTest extends FunctionalTest {

	@Before
	public void setUp() {
		
		
	}

	@Test
	public void test404Response() {

		Request request = newRequest();
		request.url = "/test/notFoundResponse";
		// accept json header
		request.headers.put("accept", new Header(
						"accept", "application/json"));

		
		request.method = "GET";

		Response response = GET(request, request.url);


		assertIsNotFound(response);
		// check that response is in JSON format
		assertEquals("application/json",response.contentType);
		assertTrue(response.out.toString().contains("404"));
	}

	@Test
	public void test404ResponseWithString() {

		Request request = newRequest();
		request.url = "/test/notFoundResponseWithString";
		// accept json header
		request.headers.put("accept", new Header(
						"accept", "application/json"));

		
		request.method = "GET";

		Response response = GET(request, request.url);


		assertIsNotFound(response);
		// check that response is in JSON format
		assertEquals("application/json",response.contentType);
		assertTrue(response.out.toString().contains("404"));
		assertTrue(response.out.toString().contains("not found this!"));
	}

	@Test
	public void test500Response() {

		Request request = newRequest();
		request.url = "/test/errorResponse";
		// accept json header
		request.headers.put("accept", new Header(
						"accept", "application/json"));

		
		request.method = "GET";

		Response response = GET(request, request.url);


		assertTrue(500==response.status);
		// check that response is in JSON format
		assertEquals("application/json",response.contentType);
		assertTrue(response.out.toString().contains("500"));
	}

	@Test
	public void test500ResponseWithException() {

		Request request = newRequest();
		request.url = "/test/errorResponseWithException";
		// accept json header
		request.headers.put("accept", new Header(
						"accept", "application/json"));

		
		request.method = "GET";

		Response response = GET(request, request.url);


		assertTrue(500==response.status);
		// check that response is in JSON format
		assertEquals("application/json",response.contentType);
		assertTrue(response.out.toString().contains("500"));
		assertTrue(response.out.toString().contains("It's all gone wrong!"));
	}

}
