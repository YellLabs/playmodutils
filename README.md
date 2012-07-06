Play Framework Common Utilities Module
======================================

Common utilities for play projects in a play module


t.b.c

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




