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
import simplebank.domain.Customer;
import simplebank.domain.CustomerFinder;
import simplebank.domain.CustomerList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by stanle on 11/28/16.
 */
@Path("/api/customer")
public class CustomerResource
{
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
    public Customer getCustomerById(@PathParam("id") int customerId) throws JsonProcessingException
    {
        return CustomerFinder.findByPrimaryKey(customerId);
    }

    @GET
    @Path("/findByLastName")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerList getCustomersByLastName(@QueryParam("lastName") String lastName)
    {
        return CustomerFinder.findMany(CustomerFinder.lastName().eq(lastName));
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") int customerId, Customer customerPojo)
    {
        MithraManager.getInstance().executeTransactionalCommand((tx) -> {
            //locate the customer
            Customer customer = CustomerFinder.findOne(CustomerFinder.customerId().eq(customerId));
            //update names
            customer.setFirstName(customerPojo.getFirstName());
            customer.setLastName(customerPojo.getLastName());
            //delete existing accounts and replace with new accounts
            customer.getAccounts().deleteAll();
            ;
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
