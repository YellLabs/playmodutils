package com.yell.civitas.play.utils.template;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import play.Play;
import play.templates.JavaExtensions;

public class YellTemplateExtensions extends JavaExtensions {

    public static String escapeJavaScriptYell(String str) {
    	// call standard string escape method
    	String escapedString = escapeJavaScript(str);
    	// now replace and escaped single quotes with a non-escaped single quote
    	String nonEscaped = escapedString.replace("\\'", "'");
    	// and unicode code for the pound-sign with a pound sign
    	nonEscaped = nonEscaped.replace("\\u00A3", "£");
        return nonEscaped;
    }
    
    public static String dateFormat(Date date) {
    	if (date==null) 
    		return null;
    	
    	// the date format defaults to ISO8601 with ±HHMM offset
    	String iso8601Pattern = Play.configuration.getProperty("date.format", "yyyy-MM-dd'T'HH:mm:ssZ");
    	
    	SimpleDateFormat df = new SimpleDateFormat(iso8601Pattern);
    	df.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return df.format(date);
    }
}

