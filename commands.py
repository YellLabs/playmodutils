from play.utils import *
import sys, traceback
#from poster.encode import multipart_encode
#from poster.streaminghttp import register_openers
import urllib
import urllib2
import yaml
from subprocess import call

# Here you can create play commands that are specific to the module, and extend existing commands

MODULE = 'playmodutils'

# Commands that are specific to your module

COMMANDS = ['playmodutils:hello', 'deploy-artifact']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
    print '~ Printing command args'
    print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
    print '~ '
    print "~ app:%s" % app
    print "~ args:%s" % args
    print "~ env:%s" %env
    print
        
    if command == "playmodutils:hello":
        print "~ Hello"

    if command == "deploy-artifact":
        
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
        print '~ Committing to Artifactory server'
        print '~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
        print '~ '

        app.check()
        artifactory_url = app.readConf('artifactory_url')
        artifactory_user = app.readConf('artifactory_user')
        artifactory_password = app.readConf('artifactory_password')

        # check artifactory url
        if artifactory_url:
            print '~ Found artifactory at url: %s' % artifactory_url
        else:
            print '~ No artifactory server configured'
            print '~    artifactory_url'
            print '~ '
            sys.exit(-1)
        
        # check artifactory credentials
        if (not artifactory_user) or artifactory_user=="" or (not artifactory_password) or artifactory_password=="":
            print '~ No artifactory Username/Password configured'
            print '~ Set up the following on your application.conf:'
            print '~    artifactory_user'
            print '~    artifactory_password'
            print '~ '
            sys.exit(-2)
        else:
            print '~ Found artifactory credentials. User=[%s] Password=[%s]' % (artifactory_user, artifactory_password)

        #Getting module version from dependencies file
        deps_file = os.path.join(app.path, 'conf', 'dependencies.yml')
        if os.path.exists(deps_file):
            f = open(deps_file)
            deps = yaml.load(f.read())
            #Is this a Play~ module?
            if "self" in deps:
                d = deps["self"].split(" ")
                module_version = d.pop()
                app_name = d.pop()
            else:
                app_name = app.name()
                print '~ This is not a Play module'
                module_version = app.readConf('application.version')
            if not module_version:
                print '~ No application.version found in application.conf file'
                print '~ '
                module_version = raw_input('~ Provide version number to be pushed to Nexus:')
                
            f.close
            
        else:
            # this should never happen as this is part of a module 
            # so the client app can call this script only if has a dependencies.yml file
            print '~ dependencies.yml file not found.'
            print
            sys.exit(-3)
            
        if module_version:
            print '~ Module version : %s' % module_version
            print '~ '
        else:
            print '~ No module version configured in the conf/dependencies.yml file'
            print '~ Configure your dependencies file properly'
            print
            sys.exit(-4)

        dist_dir = os.path.join(app.path, 'dist')
        artifact_file = os.path.join(app.path, 'dist', '%s-%s.zip' % (app_name, module_version) )
        if not os.path.exists(artifact_file):
            print '~ '
            print '~ Error: artifact zip file dist/%s-%s.zip not found.' % (app_name, module_version)
            print '~ Try "play build-module" command first'
            print '~ '
            sys.exit(-5)


        if "-a" in args:
            #We won't ask the user if he wants to commit
            resp = "Y"
        else:
            resp = raw_input('~ Do you want to post %s to Artifactory? (Y/N) ' % artifact_file)
            
        if resp == 'Y':
            try:
                zip_url = '%s/play-release-local/yell/%s/%s/%s-%s.zip' % (artifactory_url, app_name, module_version, app_name, module_version)
                deps_url = '%s/play-release-local/yell/%s/%s/%s-%s-dependencies.yml' % (artifactory_url, app_name, module_version, app_name, module_version)
            
                # ================================================
                # ================================================
                # ================================================
                
                print '~ '
                print '~ Sending %s to %s' % (artifact_file, zip_url)
                
                command = "curl -XPUT --user %s:%s %s --data-binary @%s" % (artifactory_user, artifactory_password, zip_url, artifact_file) # --verbose
                print '~ executing command:'
                print '~ %s' % command
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
                    sys.exit(-6)

                # ================================================
                # ================================================
                # ================================================
                print '~ '
                print '~ Sending %s to %s' % (deps_file, deps_url)
                
                command = "curl -XPUT --user %s:%s %s --data-binary @%s" % (artifactory_user, artifactory_password, deps_url, deps_file) # --verbose
                print '~ executing command:'
                print '~ %s' % command
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
                    sys.exit(-6)
                    
                                        
            except:
                traceback.print_exc()
                pass
            
        else:
            print '~ '
            print '~ Skipping %s' % artifact_file
            

# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")


# This will be executed after any command (new, run...)

def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "new":
        pass
            