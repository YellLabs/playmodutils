package utils.playmodutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import models.playmodutils.SourceVersion;
import play.Play;
import play.vfs.VirtualFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

		sourceVersion.name = JSONHelper.getValueIfExists(jsonObject,"name");
		sourceVersion.status = "ok";
		sourceVersion.version = JSONHelper.getValueIfExists(jsonObject,"version");
		sourceVersion.buildNumber = JSONHelper.getValueIfExists(jsonObject,"buildNumber");
		sourceVersion.jobName = JSONHelper.getValueIfExists(jsonObject,"jobName");
		sourceVersion.jenkinsUrl = JSONHelper.getValueIfExists(jsonObject,"jenkinsUrl");
		sourceVersion.sourceControlSystem = JSONHelper.getValueIfExists(jsonObject,"sourceControlSystem");
		sourceVersion.sourceControlRevision = JSONHelper.getValueIfExists(jsonObject,"sourceControlRevision");
		sourceVersion.sourceControlBranch = JSONHelper.getValueIfExists(jsonObject,"sourceControlBranch");
	    
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

		sourceVersion.sourceControlSystem = JSONHelper.getValueIfExists(jsonObject,"type");
		sourceVersion.sourceControlRevision = JSONHelper.getValueIfExists(jsonObject,"rev");
		sourceVersion.sourceControlBranch = JSONHelper.getValueIfExists(jsonObject,"branch");

		// construct build URL
		/* https://github.com/YellLabs/eventsapi/commit/0.1.1-180-g928f3c6 
		 * from 
		 * "url": "origin git@github.com:YellLabs/eventsapi.git" & "rev": "0.1.1-180-g928f3c6" */
		String url = JSONHelper.getValueIfExists(jsonObject,"url");
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
