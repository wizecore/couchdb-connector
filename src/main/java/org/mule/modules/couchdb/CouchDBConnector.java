/**
 * (c) 2014-2015 Wizecore. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.couchdb;

import java.util.HashMap;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

import com.google.gson.JsonObject;

/**
 * Anypoint Connector for connecting to CouchDB,
 * http://couchdb.apache.org/
 *
 * @author Wizecore
 */
@Connector(name="couchdb", schemaVersion="1.0", friendlyName="CouchDB", description = "Enables operations with Apache CouchDB")
public class CouchDBConnector
{
	/**
	 * Host on which Apache CouchDB installed.
	 */
    @Configurable
    @Default("localhost")
    private String hostname;

    /**
     * Protocol to connect with. Default is HTTP.
     */
    @Configurable
    @Default("http")
    private String protocol;

    /**
     * CouchDB port. Default is 5984.
     */
    @Configurable
    @Default("5984")
    private int port;
    
    /**
     * Target database to operate on.
     */
    @Configurable
    @Default("test")
    private String database;
    
    /**
     * Should we create (true) or fail (false) if target database does not exist? Default is true.
     */
    @Configurable
    @Default("true")
    private boolean autoCreate;
    
    /**
     * Private client
     */
    private CouchDbClient client;
    
    /**
     * Connect to CouchDB.
     *
     * @param username A username (optional, required if you have setup user auth in CouchDB)
     * @param password A password (optional, required if you have setup user auth in CouchDB)
     * @throws ConnectionException
     */
    @Connect
    public void connect(@ConnectionKey @Optional String username, @Password @Optional String password)
        throws ConnectionException {
    	try {
    		ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
    		try {
    			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    			client = new CouchDbClient(getDatabase(), isAutoCreate(), protocol, hostname, port, username, password);
    		} finally {
    			Thread.currentThread().setContextClassLoader(ctxClassLoader);
    		}
    	} catch (Throwable e) {
    		ConnectionException ce = new ConnectionException(ConnectionExceptionCode.UNKNOWN, "1", "Failed to connect to " + hostname + ":" + port + ": " + e.toString());
    		ce.initCause(e);
    		throw ce;
    	}
    }

    /**
     * Disconnect from CouchDB.
     */
    @Disconnect
    public void disconnect() {
    	CouchDbClient c = client;
    	client = null;
    	if (c != null) {
    		c.shutdown();
    	}
    }

    /**
     * Are we connected. Tests connection to CouchDB.
     */
    @ValidateConnection
    public boolean isConnected() {
        return client != null && client.context().info().getInstanceStartTime() > 0;
    } 

    /**
     * Returns connection identifier.
     */
    @ConnectionIdentifier
    public String connectionId() {
        return "" + client.context().info().getInstanceStartTime();
    }
    
    /**
     * Saves content to CouchDB. If content starts with { assume it JSON and parse it.
     * If other cases saves it as content property of document.
     *
     * {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:save}
     *
     * @param content Content to be processed
     * @return ID saved
     */
    @Processor
    public String save(String content) {
    	if (content != null) {
    		if (content.startsWith("{")) {
    			JsonObject json = client.getGson().fromJson(content, JsonObject.class);
    			Response r = client.save(json);
    			return r.getId();
    		} else {
    			HashMap<String, String> m = new HashMap<String, String>();
    			m.put("content", content);
    			Response r = client.save(m);
    			return r.getId();
    		}
    	} else {
    		throw new NullPointerException("content");
    	}
    }

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}