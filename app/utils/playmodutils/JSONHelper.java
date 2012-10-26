package utils.playmodutils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSONHelper {
	public static String getValueIfExists(JsonObject jsonObject, String element) {
		if(jsonObject==null)
		{
			// if no object provided
			return null;
		}
		
		JsonElement jsonElement = jsonObject.get(element);
		if(jsonElement==null)
		{
			// if element not found
			return null;
		}
		
		if(!jsonElement.isJsonNull())
		{
			// if element found
			return jsonElement.getAsString();
		}
		else
		{
			// if element found but value is null
			return null;
		}
	}
}
