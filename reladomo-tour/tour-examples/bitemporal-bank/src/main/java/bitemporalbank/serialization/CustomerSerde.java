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

package bitemporalbank.serialization;

import bitemporalbank.domain.Customer;
import bitemporalbank.domain.CustomerAccount;
import bitemporalbank.domain.CustomerAccountList;
import bitemporalbank.util.DateUtils;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;


/**
 * Created by stanle on 12/5/16.
 */
public class CustomerSerde
{
    public static class Serializer extends StdSerializer<Customer>
    {
        public Serializer()
        {
            this(null);
        }

        public Serializer(Class<Customer> t)
        {
            super(t);
        }

        @Override
        public void serialize(Customer customer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonGenerationException
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", customer.getCustomerId());
            jsonGenerator.writeStringField("firstName", customer.getFirstName());
            jsonGenerator.writeStringField("lastName", customer.getLastName());
            jsonGenerator.writeStringField("businessDate", DateUtils.printFull(customer.getBusinessDate()));
            jsonGenerator.writeStringField("processingDate", DateUtils.printFull(customer.getProcessingDate()));
            jsonGenerator.writeArrayFieldStart("accounts");
            CustomerAccountList accounts = customer.getAccounts();
            for (CustomerAccount account : accounts)
            {
                jsonGenerator.writeObject(account);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<Customer>
    {
        public Deserializer()
        {
            this(null);
        }

        public Deserializer(Class<?> vc)
        {
            super(vc);
        }

        @Override
        public Customer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
        {
            TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
            int id = ((IntNode) treeNode.get("id")).intValue();
            String firstName = ((TextNode) treeNode.get("firstName")).asText();
            String lastName = ((TextNode) treeNode.get("lastName")).asText();
            Timestamp businessDate = DateUtils.parseFull(((TextNode) treeNode.get("businessDate")).asText());
            Timestamp processingDate = DateUtils.parseFull(((TextNode) treeNode.get("processingDate")).asText());

            Customer customer = new Customer(businessDate, processingDate);
            customer.setCustomerId(id);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);

            CustomerAccountList accounts = new CustomerAccountList();
            ArrayNode accountsNode = (ArrayNode) treeNode.get("accounts");
            for (JsonNode accountNode : accountsNode)
            {
                CustomerAccount account = new CustomerAccountSerde.Deserializer().deserialize(accountNode);
                accounts.add(account);
            }
            customer.setAccounts(accounts);
            return customer;
        }
    }
}
