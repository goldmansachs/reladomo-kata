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

package simplebank.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gs.fw.common.mithra.MithraManager;
import com.gs.fw.common.mithra.util.serializer.SerializationConfig;
import com.gs.fw.common.mithra.util.serializer.Serialized;
import com.gs.fw.common.mithra.util.serializer.SerializedList;
import simplebank.domain.Customer;
import simplebank.domain.CustomerFinder;
import simplebank.domain.CustomerList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/customer")
public class CustomerResource
{
    private final SerializationConfig serializationConfig;

    public CustomerResource()
    {
        this.serializationConfig = SerializationConfig
                .shallowWithDefaultAttributes(CustomerFinder.getFinderInstance());
        serializationConfig.withDeepDependents();
    }

    @POST
    public Response createCustomer(
            @FormParam("customerId") int customerId,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName)
    {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.insert();
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Serialized<Customer> getCustomerById(@PathParam("id") int customerId) throws JsonProcessingException
    {
        Customer customer = CustomerFinder.findByPrimaryKey(customerId);
        return new Serialized<>(customer, serializationConfig);
    }

    @GET
    @Path("/findByLastName")
    @Produces(MediaType.APPLICATION_JSON)
    public SerializedList<Customer, CustomerList> getCustomersByLastName(@QueryParam("lastName") String lastName)
    {
        CustomerList customers = CustomerFinder.findMany(CustomerFinder.lastName().eq(lastName));
        return new SerializedList<>(customers, serializationConfig);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") int customerId, Serialized<Customer> serialized)
    {
        Customer customerPojo = serialized.getWrapped();
        MithraManager.getInstance().executeTransactionalCommand((tx) -> {
            //locate the customer
            Customer customer = CustomerFinder.findOne(CustomerFinder.customerId().eq(customerId));
            //update names
            customer.setFirstName(customerPojo.getFirstName());
            customer.setLastName(customerPojo.getLastName());
            //delete existing accounts and replace with new accounts
            customer.getAccounts().deleteAll();
            customer.getAccounts().addAll(customerPojo.getAccounts());
            return null;
        });
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int customerId)
    {
        Customer customer = CustomerFinder.findOne(CustomerFinder.customerId().eq(customerId));
        customer.cascadeDelete();
        return Response.ok().build();
    }
}
