Installing Forge
================

Go to [JBoss Forge home page](http://forge.jboss.org), download Forge distribution, and follow installation instructions.


Updating Scaffold with latest model
===================================

First step is to prepare model entities for Forge Scaffold plugin by copying them from common module over here to scaffold module.

    $ cd scaffold
    $ cp -r ../common/src/main/java/org/eventjuggler/model src/main/java/org/eventjuggler

Now, delete current scaffold to avoid any inconsistencies that may result from changes in entities.

    $ rm -rf src/main/java/org/eventjuggler/view
    $ rm -rf src/main/webapp

Then, start forge inside scaffold directory:

    $ forge

Ignore any ModuleNotFoundException errors

    [eventjuggler-scaffold] scaffold $ scaffold setup

Hit Enter as an answer to every question.

    [eventjuggler-scaffold] scaffold $ scaffold from-entity org.eventjuggler.model.*

Again, hit Enter as an answer to every question.

    [eventjuggler-scaffold] scaffold $ exit

That's it. Now undo changes Forge did to pom.xml

    $ git checkout pom.xml

And remove entities we copied from common module at the beginning - they will be packaged with eventjuggler-common.jar.

    $ rm -rf src/main/java/org/eventjuggler/model


Building and deploying
======================

    $ mvn clean install
    $ mvn jboss-as:deploy

Access the scaffold at: [http://localhost:8080/eventjuggler-scaffold](http://localhost:8080/eventjuggler-scaffold)
