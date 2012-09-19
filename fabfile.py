from fabric.api import env, runs_once
import sys, traceback
from subprocess import call

try:
    from yellfabric import defaults
    from yellfabric.operations import *
    from yellfabric.play import *
except ImportError:
    # We're not being called from a virtual env with yellfabric installed
    print '\nERROR: Could not import yellfabric\n'
    pass

# global environment settings
env.lang = "play"
env.project_name = "playmodutils"
env.project_version= "0.1.8"
env.scm_type = "git"
env.scm_url = "git@github.com:YellLabs/playmodutils.git"
env.rsync_exclude = ["modules/", "lib/", "tmp/", "logs/"]
env.sudo_user = "labs.deploy"

env.custom_config_files = [
    { "source": "conf/dependencies.yml.template", "dest": "conf/dependencies.yml"}
]

# variables required to render local settings
env.settings_vars = [
    "project_name",
    "project_version",
    "artifactory_url",
    "artifactory_user",
    "artifactory_password",
    "custom_config_files",   
]

@runs_once
def ci_vm():
    """
    Continuous integration server deployment settings.
    """

    env.artifactory_url = "http://uskopciaft01.yellglobal.net:8080/artifactory"
    env.artifactory_user = "play"
    env.artifactory_password = "{DESede}MrCcGAFjDwyyxo6R83ZkMw=="
                    
    fab_setup_paths()
   

@runs_once
def stg():
    """
    Civitas staging environment
    """    
    load_config()


@runs_once
def lva():
    """
    Civitas live A environment
    """
    load_config()

@runs_once
def lvb():
    """
    Civitas live B environment
    """
    load_config()


def load_config():
    env.artifactory_url = "http://ukawsdsaft01.eu01.aws.yb.int:8080/artifactory"
    env.artifactory_user = "play"
    env.artifactory_password = "not_used"
    fab_setup_paths()


def deploy_artifacts():
    artifact_file = os.path.join('.', 'dist', '%s-%s.zip' % (env.project_name, env.project_version) )
    zip_url = '%s/play-release-local/yell/%s/%s/%s-%s.zip' % (env.artifactory_url, env.project_name, env.project_version, env.project_name, env.project_version)
    upload_file_to_artifactory(artifact_file, zip_url, env.artifactory_user, env.artifactory_password)
    
    deps_file = os.path.join('.', 'conf', 'dependencies.yml')
    deps_url = '%s/play-release-local/yell/%s/%s/%s-%s-dependencies.yml' % (env.artifactory_url, env.project_name, env.project_version, env.project_name, env.project_version)
    upload_file_to_artifactory(deps_file, deps_url, env.artifactory_user, env.artifactory_password)   


def upload_file_to_artifactory(file, file_url, artifactory_user, artifactory_password):
    try:
        print '~ '
        print '~ '
        print '~ Sending %s to %s' % (file, file_url)
        
        command = "curl -XPUT --user %s:%s %s --data-binary @%s" % (artifactory_user, artifactory_password, file_url, file) # --verbose
        
        print '~ executing command:'
        print '~ %s' % command
        print '~ '
        print '~ uploading.....'
        result = call(command, shell=True)
        
        if result == 0:
            print '~'
            print '~ command successful'
            print '~'
        else :
            print '~'
            print '~ upload using curl has failed, stop deploying.'
            print '~'
            sys.exit(-10)
    except:
        traceback.print_exc()
        sys.exit(-11)
 
                
