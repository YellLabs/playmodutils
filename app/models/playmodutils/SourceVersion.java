package models.playmodutils;

/*
 * This class contains details of the currently running code for the project.
 * Attributes can be fetched to two potential source dependent on how the source was deployed.
 * 
 * If the code have been build via jenkins the "render_ci_props" method will be called which generates
 * a file call ci_props.json in the project workspace.  This file is subsequently deplyoyed with the artefacts.
 * 
 * If the code is being developed locally and deployed via a fab deploy_play command the "version" file will be 
 * rendering in the local source directory and copied to the server.
 * 
 * Therefore when populating the SourceVersion object both files should be taken into consideration.
 * 
 * If ci_props.json exists it should be used, otherwise the process should fall back to the version file.
 */

public class SourceVersion{
    
	public String name; 

    public String status; // ok

	public String version; 

    public String buildNumber; 
    
    public String buildUrl;

    public String jobName; 

    public String jenkinsUrl; 

    public String sourceControlSystem;

    public String sourceControlRevision; 

    public String sourceControlBranch; 
    
    public String[] dependencies;
    
    
    
}
/* populated using the 'version' file from YellFabric:
  {"url": "origin git@github.com:YellLabs/eventsapi.git", "rev": "0.1.1-180-g928f3c6", "type": "git", "branch": "master"}
*/