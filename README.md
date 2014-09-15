mule-couchdb-connector
======================

CouchDB connector for Mule ESB

This project was built with command
 
 mvn archetype:generate -DarchetypeGroupId=org.mule.tools -DarchetypeArtifactId=mule-cloud-connector-archetype -DarchetypeVersion=1.0 -DarchetypeRepository=http://repository.mulesoft.org/releases/ -DgroupId=com.wizecore.mule.couchdb -DartifactId=mule-couchdb-connector -Dversion=1.0 -DmuleVersion=3.5.0 -DmuleModuleName=CouchDBConnector
 
And with parameters:

 cloudService: CouchDB
 cloudServiceType: CouchDB
 wsdl: https://raw.githubusercontent.com/wizecore/mule-couchdb-connector/master/couchdb.wsdl
 
With additional tweaking afterwards.

