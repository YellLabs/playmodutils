package utils.playmodutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import play.Logger;
import play.Play;
import play.vfs.VirtualFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.playmodutils.SourceVersion;

public class SourceVersionHelper {

	public static String fetchDataFromFileAsString(String relativePath) {
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
	
	public static String[] fetchDataFromFileAsStringArray(String relativePath) {
		// fetch data from file
		VirtualFile virtFile = VirtualFile.fromRelativePath(relativePath);
		File realFile = virtFile.getRealFile();
		List<String> stringList = new ArrayList();

		try {
			String textLine;

			BufferedReader br = new BufferedReader(new FileReader(
					realFile));

			while ((textLine = br.readLine()) != null) {
				stringList.add(textLine);
			}
		}

		catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		String[] strings = stringList.toArray(new String[0]);
		return strings;
	}
	
	
	public static String[] getDependenciesFromFile() {
		
		String[] ymlStringArray = fetchDataFromFileAsStringArray("/conf/dependencies.yml");
		
		return ymlStringArray;

	}
	
	public static SourceVersion getSourceVersionFromCIDeploy() {
		SourceVersion sourceVersion = new SourceVersion();

		String jsonString = fetchDataFromFileAsString("/ci_props.json");
		if (jsonString==null)
			return null;
		
		sourceVersion = parseCIProps(jsonString);
		
		sourceVersion.dependencies = getDependenciesFromFile();

		return sourceVersion;
	}

	public static SourceVersion getSourceVersionFromLocalDeploy() {
		SourceVersion sourceVersion = new SourceVersion();

		String jsonString = fetchDataFromFileAsString("/version");
		if (jsonString==null)
			return null;
		sourceVersion = parseVersionProps(jsonString);

		sourceVersion.dependencies = getDependenciesFromFile();
		
		return sourceVersion;

	}
	
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

	public static SourceVersion parseCIProps(String ciPropsJson) {

		/* {"status": "ok", "name": "eventsapi", "sourceControlSystem": "git", "sourceControlBranch": null, "jobUrl": null, "jobName": null, "buildNumber": null, "version": "1.0.2", "sourceControlRevision": null, "jenkinsU
rl": null} */
		
		SourceVersion sourceVersion = new SourceVersion();
		JsonParser jsonParser = new JsonParser();
		if(ciPropsJson == null)
		{
			// no props provided
			sourceVersion.status = "no version props";
			return sourceVersion;
		}
		JsonObject jsonObject = jsonParser.parse(ciPropsJson).getAsJsonObject();

		sourceVersion.name = getValueIfExists(jsonObject,"name");
		sourceVersion.status = "ok";
		sourceVersion.version = getValueIfExists(jsonObject,"version");
		sourceVersion.buildNumber = getValueIfExists(jsonObject,"buildNumber");
		sourceVersion.jobName = getValueIfExists(jsonObject,"jobName");
		sourceVersion.jenkinsUrl = getValueIfExists(jsonObject,"jenkinsUrl");
		sourceVersion.sourceControlSystem = getValueIfExists(jsonObject,"sourceControlSystem");
		sourceVersion.sourceControlRevision = getValueIfExists(jsonObject,"sourceControlRevision");
		sourceVersion.sourceControlBranch = getValueIfExists(jsonObject,"sourceControlBranch");
	    
		// construct build URL
		/* http://uskopcibld01.yellglobal.net:8080/job/1_EventsApi_BAU/325/ */
		if(sourceVersion.jenkinsUrl != null && sourceVersion.jobName!=null && sourceVersion.buildNumber != null)
		{
			sourceVersion.buildUrl = String.format("%sjob/%s/%s/",sourceVersion.jenkinsUrl,sourceVersion.jobName,sourceVersion.buildNumber);
		}
		return sourceVersion;
	}

	

	public static SourceVersion parseVersionProps(String versionJson) {

	/*	{"url": "origin git@github.com:YellLabs/eventsapi.git", "rev": "0.1.1-180-g928f3c6", "type": "git", "branch": "master"}  */
		
		SourceVersion sourceVersion = new SourceVersion();
		
		sourceVersion.name = (String) Play.configuration.get("application.name");
		sourceVersion.version = (String) Play.configuration.get("application.version");
		
		JsonParser jsonParser = new JsonParser();
		if(versionJson == null)
		{
			// no props provided
			sourceVersion.status = "no version props";
			return sourceVersion;
		}
		
		JsonObject jsonObject = jsonParser.parse(versionJson).getAsJsonObject();

		
		sourceVersion.status = "ok";
		
		sourceVersion.buildNumber = null;

		sourceVersion.jobName = null;

		sourceVersion.jenkinsUrl = null;

		sourceVersion.sourceControlSystem = getValueIfExists(jsonObject,"type");
		sourceVersion.sourceControlRevision = getValueIfExists(jsonObject,"rev");
		sourceVersion.sourceControlBranch = getValueIfExists(jsonObject,"branch");

		// construct build URL
		/* https://github.com/YellLabs/eventsapi/commit/0.1.1-180-g928f3c6 
		 * from 
		 * "url": "origin git@github.com:YellLabs/eventsapi.git" & "rev": "0.1.1-180-g928f3c6" */
		String url = getValueIfExists(jsonObject,"url");
		if(url!=null && sourceVersion.sourceControlRevision != null)
		{
			url = url.replace("origin git@github.com:", "https://github.com/");
			// remove .git ref
			url = url.replace(".git", "");
			sourceVersion.buildUrl = url+"/commit/"+sourceVersion.sourceControlRevision;
		}
			
		
	    
		return sourceVersion;
	}
	
	

}
