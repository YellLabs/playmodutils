package playmodutils.unit;

import java.util.List;

import models.playmodutils.SourceVersion;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import utils.playmodutils.SourceVersionHelper;

/* these test are to check the status page functionality is working correctly */

public class BuildPropsTests extends UnitTest {

	@Before
	public void setUp() {
		

	}


    @Test
    public void retrieveSourceVersionFromCIPropsFile() {
    	// Test that source version props are recevied from the ci_props.json file
    	
    	String ciPropsJson = "{\"status\": \"ok\", \"name\": \"eventsapi\", \"sourceControlSystem\": \"git\", \"jobUrl\": \"http://somewhere\", \"jobName\": \"1_EventsApi_BAU\", \"buildNumber\": \"123\", \"version\": \"1.0.2\", \"sourceControlBranch\": \"master\", \"sourceControlRevision\": \"531dcfb0ec8e66e6503538a93a3964e4ed28a1f1\", \"jenkinsUrl\": \"http://uskopcibld01.yellglobal.net:8080/\"}";

    	SourceVersion sourceVersion = SourceVersionHelper.parseCIProps(ciPropsJson);
    	
    	assertNotNull(sourceVersion);
    	assertNotNull(sourceVersion.name);
    	assertEquals("eventsapi",sourceVersion.name);
    	assertNotNull(sourceVersion.status);
    	assertEquals("ok",sourceVersion.status);
    	assertNotNull(sourceVersion.version);
    	assertEquals("1.0.2",sourceVersion.version);
    	assertNotNull(sourceVersion.buildNumber);
    	assertEquals("123",sourceVersion.buildNumber);
    	assertNotNull(sourceVersion.jobName);
    	assertEquals("1_EventsApi_BAU",sourceVersion.jobName);
    	assertNotNull(sourceVersion.jenkinsUrl);
    	assertEquals("http://uskopcibld01.yellglobal.net:8080/",sourceVersion.jenkinsUrl);
    	assertNotNull(sourceVersion.sourceControlSystem);
    	assertEquals("git",sourceVersion.sourceControlSystem);
    	assertNotNull(sourceVersion.sourceControlRevision);
    	assertEquals("531dcfb0ec8e66e6503538a93a3964e4ed28a1f1",sourceVersion.sourceControlRevision);
    	assertNotNull(sourceVersion.sourceControlBranch);
    	assertEquals("master",sourceVersion.sourceControlBranch);
       	assertNotNull(sourceVersion.buildUrl);
    	assertEquals("http://uskopcibld01.yellglobal.net:8080/job/1_EventsApi_BAU/123/",sourceVersion.buildUrl);   	
    }

    @Test
    public void retrieveSourceVersionFromCIPropsFileWithNulls() {
    	// Test that source version props are recevied from the ci_props.json file
    	
    	String ciPropsJson = "{\"status\": null, \"name\": null, \"sourceControlSystem\": null, \"jobUrl\": null, \"jobName\": null, \"buildNumber\": null, \"version\": null, \"sourceControlBranch\": null, \"sourceControlRevision\": null, \"jenkinsUrl\": null}";

    	SourceVersion sourceVersion = SourceVersionHelper.parseCIProps(ciPropsJson);
    	
    	assertNotNull(sourceVersion);
    	assertNull(sourceVersion.name);
    	assertNull(sourceVersion.status);
    	assertNull(sourceVersion.version);
    	assertNull(sourceVersion.buildNumber);
    	assertNull(sourceVersion.jobName);
    	assertNull(sourceVersion.jenkinsUrl);
    	assertNull(sourceVersion.sourceControlSystem);
    	assertNull(sourceVersion.sourceControlRevision);
    	assertNull(sourceVersion.sourceControlBranch);
    	
    }
    
    @Test
    public void retrieveSourceVersionFromVersionPropsFile() {
    	// Test that source version props are recevied from the ci_props.json file
    	
    	String versionPropsJson = "{\"url\": \"origin git@github.com:YellLabs/eventsapi.git\", \"rev\": \"0.1.1-180-g928f3c6\", \"type\": \"git\", \"branch\": \"master\"}";

    	SourceVersion sourceVersion = SourceVersionHelper.parseVersionProps(versionPropsJson);
    	
    	assertNotNull(sourceVersion);
    	assertNotNull(sourceVersion.name);
    	// assertEquals("eventsapi",sourceVersion.name); // can't check value - project specific
    	assertNotNull(sourceVersion.status);
    	assertEquals("ok",sourceVersion.status);
    	assertNotNull(sourceVersion.version);
    	assertNotNull(sourceVersion.sourceControlSystem);
    	assertEquals("git",sourceVersion.sourceControlSystem);
    	assertNotNull(sourceVersion.sourceControlRevision);
    	assertEquals("0.1.1-180-g928f3c6",sourceVersion.sourceControlRevision);
    	assertNotNull(sourceVersion.sourceControlBranch);
    	assertEquals("master",sourceVersion.sourceControlBranch);
    	assertNotNull(sourceVersion.buildUrl);
    	assertEquals("https://github.com/YellLabs/eventsapi/commit/0.1.1-180-g928f3c6",sourceVersion.buildUrl);  
    	
    }
    
    @Test
    public void retrieveSourceVersionFromVersionPropsFilewithNulls() {
    	// Test that source version props are recevied from the ci_props.json file
    	
    	String versionPropsJson = "{\"url\": null, \"rev\": null, \"type\": null, \"branch\": null}";

    	SourceVersion sourceVersion = SourceVersionHelper.parseVersionProps(versionPropsJson);
    	
    	assertNotNull(sourceVersion);
    	assertNotNull(sourceVersion.name);
    	assertNotNull(sourceVersion.version);
    	assertNotNull(sourceVersion.status);
    	assertEquals("ok",sourceVersion.status);
    	assertNull(sourceVersion.sourceControlSystem);
    	assertNull(sourceVersion.sourceControlRevision);
    	assertNull(sourceVersion.sourceControlBranch);
    	assertNull(sourceVersion.buildUrl);
    	
    }
    
    @Test
    public void retrieveDependenciesFromFile() {
    	// Test dependencies.yml is being retrieved
    	

    	String[] dependenciesYml = SourceVersionHelper.getDependenciesFromFile();
    	
    	assertNotNull(dependenciesYml);
    	
    }
}
