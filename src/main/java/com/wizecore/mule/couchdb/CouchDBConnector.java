/*
Copyright 2014 Wizecore

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
