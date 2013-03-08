EventJuggler
============


Prerequisites
=============

- Java Development Kit 1.6
- Recent Git client
- Recent Maven 3
- JBoss AS 7.1.3.Final


Deploying to JBoss AS
=====================

The default configuration is to deploy everything in one ear. There's also a profile (standalone)
that makes it possible to deploy individul parts of the system as wars. The standalone profile
changes the configuration of which modules are deployed, and also includes services (ejb) into the
wars that require it.


Deploy everything in one ear (server)
-------------------------------------

mvn clean jboss-as:deploy


Deploy everything as separate wars
----------------------------------

mvn -Pstandalone clean jboss-as:deploy


Deploy individual parts
-----------------------

rest:
mvn -Pstandalone -f rest/pom.xml clean jboss-as:deploy

scaffold:
mvn -Pstandalone -f scaffold/pom.xml clean jboss-as:deploy

web-js:
mvn -Pstandalone -f web-js/pom.xml clean jboss-as:deploy

web-jsf:
mvn -Pstandalone -f web-jsf/pom.xml clean jboss-as:deploy


Import Data
===========

There is a data import and export service available. This uses DBUnit to import and export the 
database to XML.


Import data from XML (using DBUnit)
-----------------------------------

curl -H "Content-Type: application/xml" -X POST -d @export.xml http://localhost:8080/eventjuggler-rest/data


Export data to XML (using DBUnit)
---------------------------------

curl http://localhost:8080/eventjuggler-rest/data > export.xml


Clear database
--------------

curl http://localhost:8080/eventjuggler-rest/data/clear


Functional Tests
================

The testsuite contains a set of integration tests. The tests can be execute in either managed or remote mode.

To run the testsuite in managed mode, run:

    mvn -Pit-managed -Djboss.home=<PATH TO JBOSS AS> clean install

In managed mode you have to provide "-Djboss.zip" as it requires a full package (see the installation section).

To run the testsuite in remote mode, first start a JBoss AS with the EventJuggler Services sub-systems enabled, and run:

    mvn -Pit-remote clean install



Configuring Facebook Login
==========================

Facebook login requires three properties to be configured in your AS7 standalone.xml file.

    <server xmlns="urn:jboss:domain:1.3">

        <extensions>
            ...
        </extensions>

        <system-properties>
            <property name="eventjuggler-rest.FB_CLIENT_ID" value="VALUE_OF_YOUR_FACEBOOK_APP_ID"/>
            <property name="eventjuggler-rest.FB_CLIENT_SECRET" value="VALUE_OF_YOUR_FACEBOOK_APP_SECRET"/>
            <property name="eventjuggler-rest.FB_RETURN_URL" value="http://localhost:8080/eventjuggler-rest/facebook"/>
            ...
        </system-properties>
        ...
    </server>

In order to get Facebook App ID, and App Secret, go to https://developers.facebook.com/apps, and use Create New Application button.

For App Name type some name i.e. EventJuggler, leave App Namespace empty, and leave Web Hosting unchecked.

Complete the wizard.

After your application is created select how your app integrates with Facebook in the bottom of the screen.
Choose the first option - Website with Facebook Login.

Type in the Site URL: http://localhost:8080/eventjuggler-rest/facebook

That's it.

You now have your application, and an App ID, App Secret, and you have secured your app to only communicate with localhost:8080 ...

Replace VALUE_OF_YOUR_FACEBOOK_APP_ID, and VALUE_OF_YOUR_FACEBOOK_APP_SECRET with actual values.



Configuring Twitter Login
=========================

Twitter login requires three properties to be configured in your AS7 standalone.xml file.

    <server xmlns="urn:jboss:domain:1.3">
        <extensions>
            ...
        </extensions>

        <system-properties>
            <property name="eventjuggler-rest.TWIT_CLIENT_ID" value="VALUE_OF_YOUR_TWITTER_CONSUMER_KEY"/>
            <property name="eventjuggler-rest.TWIT_CLIENT_SECRET" value="VALUE_OF_YOUR_TWITTER_CONSUMER_SECRET"/>
            <property name="eventjuggler-rest.TWIT_RETURN_URL" value="http://localhost:8080/eventjuggler-rest/twitter"/>
            ...
        </system-properties>
        ...
    </server>

In order to get Twitter Consumer Key, and Consumer Secret, go to https://dev.twitter.com/apps, and use Create New Application button.

For Name type some globally unique name i.e. EventJuglerDemo-007. Then type a short Description.
For Website enter something that looks like a top level domain - i.e. http://www.eventjuggler.org leave the rest empty.

Submit the form. That's it.

You now have your application, and a Consumer Key, and Consumer Secret.

Replace VALUE_OF_YOUR_TWITTER_CONSUMER_KEY, and VALUE_OF_YOUR_TWITTER_CONSUMER_SECRET with actual values.
