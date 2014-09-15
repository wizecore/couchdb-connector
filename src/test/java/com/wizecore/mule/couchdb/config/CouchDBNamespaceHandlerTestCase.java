package com.wizecore.mule.couchdb.config;

import org.mule.api.construct.FlowConstruct;
import org.mule.modules.tests.ConnectorTestCase;

public class CouchDBNamespaceHandlerTestCase extends ConnectorTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "couchdb-namespace-config.xml";
    }

    public void testSendMessageToFlow() throws Exception
    {
    	//runFlowAndGetPayload(flowName)
    }
}
