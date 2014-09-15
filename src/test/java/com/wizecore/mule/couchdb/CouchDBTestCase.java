package com.wizecore.mule.couchdb;

import org.junit.Test;
import org.mule.api.ConnectionException;

public class CouchDBTestCase
{
    @Test
    public void invokeSomeMethodOnTheCloudConnector() throws ConnectionException
    {
    	CouchDBConnector cn = new CouchDBConnector();
    	cn.connect(null, null);
    	cn.disconnect();
    }
}
