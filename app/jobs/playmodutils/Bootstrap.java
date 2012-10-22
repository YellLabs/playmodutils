package jobs.playmodutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.vfs.VirtualFile;
import utils.playmodutils.ErrorHelper;

@OnApplicationStart
public class Bootstrap extends Job {
	private static final String versionPath = "/version";
	private static final String dependenciesPath = "/conf/dependencies.yml";
	public void doJob() {
		determineVersionInfo();
		
    }
	
	private void determineVersionInfo() {
		VirtualFile versionFile = VirtualFile.fromRelativePath(versionPath);
		VirtualFile dependenciesFile = VirtualFile.fromRelativePath(dependenciesPath);
		File realVersionFile = versionFile.getRealFile();
		File realDependenciesFile = dependenciesFile.getRealFile();
		StringBuilder contents = new StringBuilder();
		try {
			FileReader versionfr = new FileReader(realVersionFile);
			BufferedReader versionInput = new BufferedReader(versionfr);
			FileReader dependenciesfr = new FileReader(realDependenciesFile);
			BufferedReader dependenciesInput = new BufferedReader(dependenciesfr);
			String line = null;
			try {
				while (( line = versionInput.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
			    }
				while (( line = dependenciesInput.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
			    }
			} finally {
				versionInput.close();
				dependenciesInput.close();
			}
		} catch (IOException e) {
			String message = ErrorHelper.getMessage("CAPI_SRV_ERR_0003",
					e.getMessage());
			Logger.error(message);
		}
		Play.configuration.put("application.version", contents.toString());
	}
}
