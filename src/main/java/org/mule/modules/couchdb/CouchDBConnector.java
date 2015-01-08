/**
 * (c) 2014-2015 Wizecore. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.couchdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.lightcouch.View;
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

import com.google.gson.JsonArray;
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
     * Removes document from CouchDB by JSON or by id and optional revision.
     * 
     * {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:remove}
     * 
     * @param document Document to delete. Must contain at least _id.
     * @param idValue Id value, either this or document must be specified.
     * @param revision Revision. Optional, if not specified, will be found last and deleted.
     * @return JSON of specified document or last version in database.
     * @throws IOException If error occurs
     */
    @Processor
    public String remove(@Optional String document, @Optional String idValue, @Optional String revision) throws IOException {
    	JsonObject json = null;
    	if (document != null && document.trim().equals("") && document.startsWith("{")) {
    		json = client.getGson().fromJson(document, JsonObject.class);
    		if (json.has("_id")) {
    			idValue = json.get("_id").getAsString();
    		}
    		if (json.has("_rev")) {
    			revision = json.get("_rev").getAsString();
    		}
    	}
    	
    	if (revision == null || revision.trim().equals("")) {
    		json = client.find(JsonObject.class, idValue);
    		revision = json.get("_rev").getAsString();
    	}
    	
    	Response r = client.remove(idValue, revision);
    	checkResponse(r);
    	return client.getGson().toJson(json);
    }
    
    /**
     * Looks for object by specified id. If found, returns JSON, if not returns <code>defaultValue</code> JSON enriched with specified ID.
     * So one can use flow findById -> save without additional steps.
     * 
     * {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:findById}
     * 
     * @param idValue CouchDB id to search for.
     * @param defaultValue Default value to return.
     * @return Object JSON
     */
    @Processor
    public String findById(String idValue, @Optional String defaultValue) {	
    	if (client.contains(idValue)) {
    		JsonObject o = client.find(JsonObject.class, idValue);
    		return client.getGson().toJson(o);
    	} else {
    		JsonObject o = client.getGson().fromJson(defaultValue, JsonObject.class);
    		o.addProperty("_id", idValue);
    		return client.getGson().toJson(o);
    	}
    }
    
    /**
     * Looks up document by key in view. If multiple entries found, returns first one. If no records found, 
     * returns defaultValue.
     * 
     *  {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:findByKey}
     * 
     * @param viewName View in form design/view-name.
     * @param keyValue Key to look for. Required.
     * @param defaultValue Default JSON to return if not found.
     * @return Returns JSON of found document or default value if not found.
     */
    @Processor
    public String findByKey(String viewName, String keyValue, @Optional String defaultValue) {
    	View v = client.view(viewName).includeDocs(true);
    	v.key(keyValue);
    	List<JsonObject> list = v.query(JsonObject.class);
    	if (list.size() == 0) {
    		return defaultValue;
    	} else {
    		return client.getGson().toJson(list.get(0));
    	}
    }
    
    /**
     * Query view specified in form design/view-name. Parameters keyValue and startKey/endKey are mutually exclusive.
     * Limit by default is no limit.
     * 
     * {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:listView}
     * 
     * @param viewName View in form design/view-name.
     * @param keyValue Key to search for. Can be omitted to return all elements.
     * @param startKey Start key to search for.
     * @param endKey End key to search for.
     * @param limit Limit number of entries returned. By default returns all.
     * @return JSON array of elements found
     */
    @Processor
    public String listView(String viewName, @Optional String keyValue, @Optional String startKey, @Optional String endKey, @Default("0") int limit) {
    	View v = client.view(viewName).includeDocs(true);
    	if (limit > 0) {
    		v.limit(limit);
    	}
    	if (startKey != null && !startKey.trim().equals("")) {
    		v.startKey(startKey);
    	}
    	if (endKey != null && !endKey.trim().equals("")) {
    		v.endKey(endKey);
    	}
    	if (keyValue != null && !keyValue.trim().equals("")) {
    		v.key(keyValue);
    	}
    	List<JsonObject> list = v.query(JsonObject.class);
    	JsonArray a = new  JsonArray();
    	for (JsonObject o: list) a.add(o);
    	return client.getGson().toJson(a);
    }
    
    /**
     * Save or updates object in CouchDB. If content starts with { assume it JSON and parse it.
     * If other cases saves it as content property of document.
     * 
     * If JSON contains _id it is interpreted as ID of document and will be updated.
     *
     * {@sample.xml ../../../doc/couchdb-connector.xml.sample couchdb:save}
     *
     * @param content Content to be processed
     * @param idValue Optional ID. If not specified, will be looked inside JSON.
     * @param propertyName Property to set if not a JSON. Default is <code>content</code>.
     * @return ID of saved object
     * @throws IOException If error occurs during operation
     */
    @Processor
    public String save(@Optional String content, @Optional String idValue, @Optional String propertyName) throws IOException {
    	if (propertyName == null || propertyName.trim().equals("")) {
    		propertyName = "content";
    	}
    	
    	if (idValue == null || idValue.trim().equals("")) {
    		idValue = null;
    	}
    	
    	if (content != null) {
    		if (content.startsWith("{")) {
    			JsonObject json = client.getGson().fromJson(content, JsonObject.class);
    			if (json.has("_id")) {
    				idValue = json.get("_id").getAsString();
    			}
    		
    			if (idValue != null && !idValue.trim().equals("") && client.contains(idValue)) {
					Response r = client.update(json);
					checkResponse(r);
					return r.getId();
    			} else {
	    			Response r = client.save(json);
	    			checkResponse(r);
	    			return r.getId();
    			}
    		} else {
    			HashMap<String, String> m = new HashMap<String, String>();
    			m.put(propertyName, content);
    			
    			if (idValue != null) {
    				m.put("_id", idValue);
    			}
    			
    			if (idValue != null && client.contains(idValue)) {
					Response r = client.update(m);
					checkResponse(r);
					return r.getId();
    			} else {
	    			Response r = client.save(m);
	    			checkResponse(r);
	    			return r.getId();
    			}
    		}
    	} else {
    		throw new NullPointerException("content");
    	}
    }

	private void checkResponse(Response r) throws IOException {
		if (r.getError() != null && r.getError().equals("")) {
			throw new IOException("CouchDB error: " + r.getError());
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