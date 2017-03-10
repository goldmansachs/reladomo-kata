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

package bitemporalbank.web;

import bitemporalbank.domain.Customer;
import bitemporalbank.domain.CustomerFinder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gs.fw.common.mithra.finder.Operation;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

@Path("/api/customer")
public class CustomerResource
{
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("a");

    @POST
    public Response createCustomer(
            @FormParam("customerId") int customerId,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("businessDate") String businessDate
    )
    {
        Timestamp businessDateTS = parse(businessDate);
        Customer customer = new Customer(businessDateTS);
        customer.setCustomerId(customerId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.insert();
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomerById(@PathParam("id") int customerId, @QueryParam("businessDate") String businessDate) throws JsonProcessingException
    {
        Timestamp businessDateTS = parse(businessDate);
        Operation op1 = CustomerFinder.businessDate().eq(businessDateTS);
        Operation op2 = CustomerFinder.customerId().eq(customerId);
        return CustomerFinder.findOne(op1.and(op2));
    }

    private Timestamp parse(String dateTimeString)
    {
        DateTime dateTime = DATE_TIME_FORMATTER.parseDateTime(dateTimeString);
        return new Timestamp(dateTime.toDateTime().getMillis());
    }
}
