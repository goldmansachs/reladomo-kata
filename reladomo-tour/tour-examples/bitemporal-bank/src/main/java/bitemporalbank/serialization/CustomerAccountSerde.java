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

import bitemporalbank.domain.CustomerAccount;
import bitemporalbank.util.DateUtils;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by stanle on 12/5/16.
 */
public class CustomerAccountSerde
{

    public static class Serializer extends StdSerializer<CustomerAccount>
    {
        public Serializer()
        {
            this(null);
        }

        public Serializer(Class<CustomerAccount> t)
        {
            super(t);
        }

        @Override
        public void serialize(CustomerAccount account, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonGenerationException
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("id", account.getAccountId());
            jsonGenerator.writeStringField("name", account.getAccountName());
            jsonGenerator.writeStringField("type", account.getAccountType());
            jsonGenerator.writeNumberField("balance", account.getBalance());
            jsonGenerator.writeStringField("businessDate", DateUtils.printFull(account.getBusinessDate()));
            jsonGenerator.writeStringField("processingDate", DateUtils.printFull(account.getProcessingDate()));
            jsonGenerator.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<CustomerAccount>
    {
        public Deserializer()
        {
            this(null);
        }

        public Deserializer(Class<CustomerAccount> t)
        {
            super(t);
        }

        @Override
        public CustomerAccount deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException
        {
            TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
            return deserialize(treeNode);
        }

        public CustomerAccount deserialize(TreeNode treeNode)
        {
            int id = ((IntNode) treeNode.get("id")).intValue();
            String name = ((TextNode) treeNode.get("name")).asText();
            String type = ((TextNode) treeNode.get("type")).asText();
            double balance = ((DoubleNode) treeNode.get("balance")).doubleValue();

            Timestamp businessDate = DateUtils.parseFull(((TextNode) treeNode.get("businessDate")).asText());
            Timestamp processingDate = DateUtils.parseFull(((TextNode) treeNode.get("processingDate")).asText());

            CustomerAccount customerAccount = new CustomerAccount(businessDate, processingDate);
            customerAccount.setAccountId(id);
            customerAccount.setAccountName(name);
            customerAccount.setAccountType(type);
            customerAccount.setBalance(balance);

            return customerAccount;
        }
    }
}
