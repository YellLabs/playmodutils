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

Installation
------------

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

Deploying the Module
--------------------

Now the zip file and supporting dependencies.yml file need to be transmitted to the packages server using scp.

    # send module zip file
    scp dist/playmodutils-0.1.0.zip eziya@puppet.dev.yelllabs.int:/srv/www/nginx/packages.yellgroup.com/html/play/yell


    # send dependencies.yml
    scp conf/dependencies.yml eziya@puppet.dev.yelllabs.int:/srv/www/nginx/packages.yellgroup.com/html/play/yell/playmodutils-0.1.0.dependencies.yml

If this doesn't work then try to scp to your home directory on the packages server, then ssh there and move the files:
   copy local files to puppet server:
   e.g
   scp placesapiadapter-0.1.7.zip ilalli@puppet.dev.yelllabs.int:/home/ilalli

   ssh puppet.dev.yelllabs.int

   Then move the files to the web directory

   mv placesapiadapter-0.1.7.zip /srv/www/nginx/packages.yellgroup.com/html/play/yell/


Checking the Deployment
-----------------------
If you browse to http://packages.yellgroup.com/play/yell/ your zip file and dependencies file should be visible.


Troubleshooting
===============
* If you are not receiving error message descriptions, 
  make sure you have included the application.langs settings in your application.conf file
* You need to start memcached on the host pointed by the memcached.host config

* Any issues with installing a module locally its probably worth deleting your ivy cache in ~.ivy2/cache 
