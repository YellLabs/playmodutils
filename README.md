Play Framework Common Utilities Module
======================================

Common utilities for play projects in a play module.

The common module provides the following functionality.

-BaseAPIAdapter

-BaseRestController
This is a base controller for REST apis that provides paging and validation.

-ContentTypeSuggestor

-Bootstrap (job)
Returns the version type of a project

-ErrorHelper

-GeoHelper

-GsonBinder

-MIMEParser

-RequestHelper

-Date & Time Validation

Changelog
=========
What's new in version 0.1.16
----------------------------
* Added new controller ExceptionHandler that can be used to improve the error message for all exceptions thrown from controllers. To use it append 

    @With(ExceptionHandler.class)

to the controller.

What's new in version 0.1.15
----------------------------
* Fixed concurrency problems in Controllers.

Users of this version must remove ContentTypeSuggester from controllers and change method *getSuggestedContentType()* to be:


    @Before
    public static void getSuggestedContentType() throws InvalidAPIControllerConfigException {
          ThreadCopy.set(ContentTypeSuggester.suggestContentType(supportedResourceVersions, resourceNamespace));
    }

To access the old controller fields *suggestedContentTypeHeader*, *requestedAcceptHeader*, *requestedContentType* or *resourceVersion*, you must use the new Thread-safe version like:

    ThreadCopy.getResourceVersion()

What's new in version 0.1.13
----------------------------
* Tidied up routes file to be more consistent. 

What's new in version 0.1.12
----------------------------
* Removed YellTemplateExtensions.quoteJSON method. This was not being used and caused a project dependency 

  JSON import org.codehaus.jettison.json.JSONObject;

  org.codehaus.jettison -> jettison 1.3.2

* Removed Bootstrap job to populate "application.version" property

* Removed getVersionInfo() method from BaseRestController

* Added getBuildInfo() method to BaseRestContoller
  This replaces the old getVersionInfo() method, but instead of returning a string it renders a JSON response.
  
  getBuildInfo retrieves information from "ci_props.json" if the code was deployed from a jenkins build or "version" if deployed from a local fab deploy_play command.

  Projects using this module need to define a property of "application.version" in application.conf
  


What's new in version 0.1.10
----------------------------
* (RM#4307) Restored escaping of single quotes (regression intrduced in 0.1.9)

What's new in version 0.1.9
----------------------------
* Display pound sign instead of its unicode representation 

What's new in version 0.1.8
----------------------------
* Bug solved: GeoHelper returned NaN instead of 0.0 sometimes when asked for the distance between a point and itself.
* Bounding box size can be less than one kilometer now

What's new in version 0.1.7
----------------------------
Added StatusItem model which will be used by any components or apps to list status information.

What's new in version 0.1.6
----------------------------

The BaseRestController now has the notFound() and error() methods to alway return 
json formatted 404 and 500 error responses.  Based on a redmine ticket.

* WARNING!!!! If you update to this version of the component you also need to make a minor change 
    to your applications conf/routes file. Add the following two lines:

        # load routes from playmodutils for functional tests
        GET     /test     module:playmodutils

These are required so the functional tests for the module run successfully.

What's new in version 0.1.5
----------------------------

Added a custom command in commands.py file to push dependencies.yml and modules zip file to artifactory.

### Pre-Requisites

1. conf/dependencies.yml file must be present in the module/app folder 
and its first line should contain something like

        self: yell -> placesapi 0.1.8-RC9

    or alternatively you should specify the version in application.version property in application.conf

2. configs required in the application.conf.template/fabfile
example

        artifactory_url=http://uskopciaft01.yellglobal.net:8080/artifactory
        artifactory_user=play
        artifactory_password={DESede}MrCcGAFjDwyyxo6R83ZkMw==

* Note: if 1. or 2. are missing, the script will complain about this and stop


### Instructions about how to push new modules to artifactory

1. render templates for the desired env

        fab ci_vm render_settings_template

2. build module first 

        play build-module --require=1.2.3

3. sync dependencies

        play deps --sync

4. push to artifactory

        play deploy-artifact
        Confirm with "Y" when asked or use the "-a" option


Installation
============

The common module can now be installed by just updating your dependencies.yml
    
dependencies.yml
----------------

Amend your dependencies.yml to reference the module.

    # Application dependencies

    require:
        - play
        - yell -> playmodutils 0.1.0
    repositories:
       - modules:

            type: chain
            using:
                - repoModules:
                    type: http
                    artifact: http://packages.yellgroup.com/play/[organisation]/[module]-[revision].zip
                    descriptor: http://packages.yellgroup.com/play/[organisation]/[module]-[revision].dependencies.yml

            contains:
               - yell -> playmodutils *

application.conf
----------------
The module requires a number of settings in the application.conf file to work correctly

    # Date format - change to ISO format for rendering JSON responses
    # ~~~~~
    date.format=yyyy-MM-dd'T'HH:mm:ssZ
    
    # i18n
    # ~~~~~
    # Define locales used by your application.
    # You can then place localized messages in conf/messages.{locale} files
    application.langs=en,es
    
    # push to artifactory
    artifactory_url=http://uskopciaft01.yellglobal.net:8080/artifactory
    artifactory_user=play
    artifactory_password={DESede}MrCcGAFjDwyyxo6R83ZkMw==

Development Tips
================
If you are working on the source code of the module and want to install it without pushing a release to the
packages server you can amend your projects dependencies file to reference a local instance.

Eg.

    - localModules:
            type:      local
            artifact: /home/<your_id>/tempmodules/[organisation]/[module]-[revision]
            descriptor: /home/your_id>/tempmodules/[organisation]/[module]-[revision]/conf/dependencies.yml 
    - repoModules:
            type: http
            artifact: http://packages.yellgroup.com/play/[organisation]/[module]-[revision].zip
            descriptor: http://packages.yellgroup.com/play/[organisation]/[module]-[revision].dependencies.yml


This will cause ivy to look in the local directory first.  Note the local directory is just the source code
Not a ZIP file.

Building and Hosting a Module
=============================

For the module to be used by other play applications the source code must reside on the
puppet packages server.

    puppet.dev.yelllabs.int

The module must be build first and then deploy to the packages server

Building the Module
-------------------

First edit the dependencies.yml file of the module to contain the correct version number.
eg.
    self: yell -> commonrestapi 0.1

    require:
        - play
        - play -> router 1.2 
        - play -> deadbolt 1.4.2 

This module will be built as version 0.1 and requires "play", "router" and "deadbolt" dependencies
If the module has itself been synced with its own dependencies during development, remember to clear out its modules directory before rebuilding it:

type

    play build-module
    
You will be prompted for the required version of the play framework

type

    1.2.3

There will now be a zip file in the projects dist directory

Troubleshooting
===============
* If you are not receiving error message descriptions, 
  make sure you have included the application.langs settings in your application.conf file
* You need to start memcached on the host pointed by the memcached.host config

* Any issues with installing a module locally its probably worth deleting your ivy cache in ~.ivy2/cache 
