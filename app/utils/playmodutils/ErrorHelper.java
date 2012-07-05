package utils.playmodutils;

import play.i18n.Messages;


/* 
 * This is a helper class of utility functions for consistent logging of error messages
 */
public class ErrorHelper {

	/**
     * This is a wrapper around the play framework Message class.
     * Message strings returned have the message string prefixed with an error code for consistent logging.
     * 
     * @param key the message code
     * @param args optional message format arguments
     * @return translated message
     */
    public static String getMessage(Object key, Object... args) {
         return (String)key + ':' + Messages.get(key, args);
         
    }
}
