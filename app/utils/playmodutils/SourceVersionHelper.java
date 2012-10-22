package utils.playmodutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import play.Logger;
import play.Play;
import play.vfs.VirtualFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.playmodutils.SourceVersion;

public class SourceVersionHelper {

	public static String fetchDataFromRelativeFile(String relativePath) {
		// fetch data from file
		VirtualFile virtFile = VirtualFile.fromRelativePath(relativePath);
		File realFile = virtFile.getRealFile();
		StringBuilder sb = new StringBuilder();

		try {
			String textLine;

			BufferedReader br = new BufferedReader(new FileReader(
					realFile));

			while ((textLine = br.readLine()) != null) {
				sb.append(textLine);
			}
		}

		catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		String jsonString = sb.toString();
		return jsonString;
	}
	
	public static SourceVersion getSourceVersionFromCIDeploy() {
		SourceVersion sourceVersion = new SourceVersion();

		String jsonString = fetchDataFromRelativeFile("/ci_props.json");
		sourceVersion = parseCIProps(jsonString);

		return sourceVersion;
	}

	public static SourceVersion getSourceVersionFromLocalDeploy() {
		SourceVersion sourceVersion = new SourceVersion();

		String jsonString = fetchDataFromRelativeFile("/version");
		sourceVersion = parseVersionProps(jsonString);

		return sourceVersion;

	}

	public static SourceVersion parseCIProps(String ciPropsJson) {

		/* {"status": "ok", "name": "eventsapi", "sourceControlSystem": "git", "sourceControlBranch": null, "jobUrl": null, "jobName": null, "buildNumber": null, "version": "1.0.2", "sourceControlRevision": null, "jenkinsU
rl": null} */
		
		SourceVersion sourceVersion = new SourceVersion();
		JsonParser jsonParser = new JsonParser();
		JsonObject jobject = jsonParser.parse(ciPropsJson).getAsJsonObject();

		JsonElement jsonElement = jobject.get("name");
		if(!jsonElement.isJsonNull())
			sourceVersion.name = jsonElement.getAsString();

		jsonElement = jobject.get("status");
		if(!jsonElement.isJsonNull())
			sourceVersion.status = jsonElement.getAsString();
		
		jsonElement = jobject.get("version");
		if(!jsonElement.isJsonNull())
			sourceVersion.version = jsonElement.getAsString();

		jsonElement = jobject.get("buildNumber");
		if(!jsonElement.isJsonNull())
			sourceVersion.buildNumber = jsonElement.getAsString();

		jsonElement = jobject.get("jobName");
		if(!jsonElement.isJsonNull())
			sourceVersion.jobName = jsonElement.getAsString();

		jsonElement = jobject.get("jenkinsUrl");
		if(!jsonElement.isJsonNull())
			sourceVersion.jenkinsUrl = jsonElement.getAsString();

		jsonElement = jobject.get("sourceControlSystem");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlSystem = jsonElement.getAsString();

		jsonElement = jobject.get("sourceControlRevision");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlRevision = jsonElement.getAsString();

		jsonElement = jobject.get("sourceControlBranch");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlBranch = jsonElement.getAsString();
	    
		// construct build URL
		/* http://uskopcibld01.yellglobal.net:8080/job/1_EventsApi_BAU/325/ */
		sourceVersion.buildUrl = String.format("%sjob/%s/%s/",sourceVersion.jenkinsUrl,sourceVersion.jobName,sourceVersion.buildNumber);
		return sourceVersion;
	}

	public static SourceVersion parseVersionProps(String versionJson) {

	/*	{"url": "origin git@github.com:YellLabs/eventsapi.git", "rev": "0.1.1-180-g928f3c6", "type": "git", "branch": "master"}  */
		
		SourceVersion sourceVersion = new SourceVersion();
		JsonParser jsonParser = new JsonParser();
		JsonObject jobject = jsonParser.parse(versionJson).getAsJsonObject();

		sourceVersion.name = (String) Play.configuration.get("application.name");
		sourceVersion.version = (String) Play.configuration.get("application.project.version");

		sourceVersion.status = "ok";
		
		sourceVersion.buildNumber = null;

		sourceVersion.jobName = null;

		sourceVersion.jenkinsUrl = null;

		JsonElement jsonElement = jobject.get("type");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlSystem = jsonElement.getAsString();

		jsonElement = jobject.get("rev");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlRevision = jsonElement.getAsString();

		jsonElement = jobject.get("branch");
		if(!jsonElement.isJsonNull())
			sourceVersion.sourceControlBranch = jsonElement.getAsString();

		// construct build URL
		/* https://github.com/YellLabs/eventsapi/commit/0.1.1-180-g928f3c6 
		 * from 
		 * "url": "origin git@github.com:YellLabs/eventsapi.git" & "rev": "0.1.1-180-g928f3c6" */
		jsonElement = jobject.get("url");
		if(!jsonElement.isJsonNull()){
			String url = jsonElement.getAsString();
			url = url.replace("origin git@github.com:", "https://github.com/");
			// remove .git ref
			url = url.replace(".git", "");
			sourceVersion.buildUrl = url+"/commit/"+sourceVersion.sourceControlRevision;
			
		}
			
		
	    
		return sourceVersion;
	}
	
	
}
