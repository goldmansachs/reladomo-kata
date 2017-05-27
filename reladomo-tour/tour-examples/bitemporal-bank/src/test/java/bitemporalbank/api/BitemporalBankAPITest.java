/*
Copyright 2016 Goldman Sachs.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package bitemporalbank.api;

import bitemporalbank.domain.Customer;
import bitemporalbank.domain.CustomerAccount;
import bitemporalbank.domain.CustomerAccountList;
import bitemporalbank.serialization.BitemporalBankJacksonObjectMapperProvider;
import bitemporalbank.web.BitemporalBankServer;
import com.gs.fw.common.mithra.test.ConnectionManagerForTests;
import com.gs.fw.common.mithra.test.MithraTestResource;
import com.gs.fw.common.mithra.util.serializer.Serialized;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class BitemporalBankAPITest
{
    private String testRuntimeConfigXML = "testconfig/BitemporalBankTestRuntimeConfiguration.xml";

    @Before
    public void setup() throws Exception
    {
        intializeReladomoForTest();
        initializeApp();
    }

    private void intializeReladomoForTest()
    {
        MithraTestResource testResource = new MithraTestResource(testRuntimeConfigXML);
        ConnectionManagerForTests connectionManager = ConnectionManagerForTests.getInstance("test_db");
        testResource.createSingleDatabase(connectionManager, "testconfig/BitemporalBankTestData.txt");
        testResource.setUp();
    }

    private void initializeApp() throws Exception
    {
        new BitemporalBankServer(testRuntimeConfigXML).start();
    }

    @Test
    public void testGetCustomer()
    {
        WebTarget target = webTarget("/api/customer/1");
        Response response = target
                .queryParam("businessDate", "2017-01-20")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Serialized<Customer> mickeySerialized = response.readEntity(new GenericType<Serialized<Customer>>()
        {
        });

        Customer mickey = mickeySerialized.getWrapped();
        assertEquals(1, mickey.getCustomerId());
        assertEquals("mickey", mickey.getFirstName());
        assertEquals("mouse", mickey.getLastName());

        CustomerAccountList mickeysAccounts = mickey.getAccounts();
        assertEquals(1, mickeysAccounts.size());
        CustomerAccount clubhouseAccount = mickeysAccounts.get(0);
        assertEquals(12345, clubhouseAccount.getAccountId());
        assertEquals("retirement", clubhouseAccount.getAccountName());
        assertEquals("savings", clubhouseAccount.getAccountType());
        assertEquals(350, clubhouseAccount.getBalance(), 0);
    }

    private WebTarget webTarget(String path)
    {
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        client.register(BitemporalBankJacksonObjectMapperProvider.class);
        return client.target("http://localhost:9998").path(path);
    }
}
