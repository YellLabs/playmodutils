package utils.playmodutils;

import java.util.List;

import play.i18n.Messages;

import play.mvc.Http.Header;
import play.mvc.Http.Request;

import exceptions.playmodutils.HeaderNotFoundException;
/* 
 * This is a helper class of utility functions for consistent logging of error messages
 */
public class RequestHelper {

	
    /** Get a value from request header
     * @param request Request object with headers in
     * @param name Header name
     * @return Value of the header
     * @throws HeaderNotFoundException
     */
    public static String getHeaderValue(Request request, String name) throws HeaderNotFoundException {
        Header header = request.headers.get(name);
        // return AIM user id, or null if not found.
        // convert to a numeric id
        if (header == null)
            throw new HeaderNotFoundException();

        return header.value();
    }
    
    /** Get a list of values from request header
     * @param request Request object with headers in
     * @param name Header name
     * @return Values of the header
     * @throws HeaderNotFoundException
     */    
    public static List<String> getHeaderValues(Request request, String name) throws HeaderNotFoundException {
        Header header = request.headers.get(name);
        // return AIM user id, or null if not found.
        // convert to a numeric id
        if (header == null)
            throw new HeaderNotFoundException();

        return header.values;
    }
}
