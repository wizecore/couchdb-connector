package com.wizecore.mule.couchdb;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.ConnectivityTesting;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.param.ConnectionKey;

@Connector(name = "couchdb-connector", schemaVersion = "2.4", description = "CouchDB Integration", friendlyName = "CouchDB",
minMuleVersion = "3.5", connectivityTesting = ConnectivityTesting.DISABLED)
public class CouchDBConnector {
	
	@Connect
    public void connect(@ConnectionKey String username, String password) throws ConnectionException{
	}
	
	@Disconnect
	public void disconnect() {
	}
	
	@ValidateConnection
    public boolean validateConnection() {
        return true;
    }
 
    @ConnectionIdentifier
    public String getConnectionIdentifier() {
        return "1";
    }
    
    @Processor
    public String getContent(String key) {
    	return "";
    }
}
