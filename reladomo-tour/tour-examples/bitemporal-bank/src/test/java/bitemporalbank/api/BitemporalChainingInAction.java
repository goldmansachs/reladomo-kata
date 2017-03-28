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
import bitemporalbank.domain.CustomerAccountFinder;
import bitemporalbank.domain.CustomerFinder;
import bitemporalbank.util.DateUtils;
import com.gs.fw.common.mithra.MithraManagerProvider;
import com.gs.fw.common.mithra.MithraTransaction;
import com.gs.fw.common.mithra.finder.Operation;
import com.gs.fw.common.mithra.test.ConnectionManagerForTests;
import com.gs.fw.common.mithra.test.MithraTestResource;
import com.gs.fw.common.mithra.util.dbextractor.DbExtractor;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class BitemporalChainingInAction
{
    private String testRuntimeConfigXML = "testconfig/BitemporalBankTestRuntimeConfiguration.xml";

    @Before
    public void setup() throws Exception
    {
        intializeReladomoForTest();
    }

    private void intializeReladomoForTest()
    {
        MithraTestResource testResource = new MithraTestResource(testRuntimeConfigXML);
        ConnectionManagerForTests connectionManager = ConnectionManagerForTests.getInstance("test_db");
        testResource.createSingleDatabase(connectionManager);
        testResource.setUp();
    }

    @Test
    public void run() throws Exception
    {
        int accountId = 12345;

        createAccount("2017-01-01", accountId);

        fetchAccountWithBusinessDate("2017-01-01", accountId);

        updateBalance("2017-01-20", accountId, 200);

        dumpCustomerAccount(accountId);

        updateBalance("2017-01-17", "2017-01-25", accountId, 50);

        dumpCustomerAccount(accountId);

        balanceAsOfJan12_OnJan23(accountId);

        dumpCustomer(1);
    }

    private void createAccount(String date, int accountId)
    {
        Timestamp jan1 = DateUtils.parse(date);
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx ->
        {
            // We set the processing time to simulate the update opening on a specific date - Do not do this in production
            doNotDoThisInProduction(date, tx);

            Customer customer = new Customer(jan1);
            customer.setFirstName("mickey");
            customer.setLastName("mouse");
            customer.setCustomerId(1);
            customer.setCountry("usa");

            CustomerAccount account = new CustomerAccount(jan1);
            account.setAccountId(accountId);
            account.setBalance(100);
            account.setAccountType("savings");
            account.setAccountName("retirement");
            customer.getAccounts().add(account);
            customer.cascadeInsert();
            return null;
        });
    }

    private void fetchAccountWithBusinessDate(String date, int accountId)
    {
        Timestamp jan1 = DateUtils.parse(date);
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation jan1Op = CustomerAccountFinder.businessDate().eq(jan1);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(jan1Op));
        assertEquals(100, (int) account.getBalance());
    }

    private void updateBalance(String date, int accountId, int balance)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx -> {

            Timestamp timestamp = DateUtils.parse(date);

            // Simulate the db change happening on a specific date - Do not do this in production
            doNotDoThisInProduction(date, tx);

            Operation ts = CustomerAccountFinder.businessDate().eq(timestamp);
            Operation id = CustomerAccountFinder.accountId().eq(accountId);
            CustomerAccount account = CustomerAccountFinder.findOne(ts.and(id));
            account.incrementBalance(balance);
            return null;
        });
    }

    private void updateBalance(String businessDate, String processingDate, int accountId, int balance)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx -> {

            // Simulate the db change happening on a specific date - Do not do this in production
            doNotDoThisInProduction(processingDate, tx);

            Timestamp businessDateTs = DateUtils.parse(businessDate);
            Operation ts = CustomerAccountFinder.businessDate().eq(businessDateTs);
            Operation id = CustomerAccountFinder.accountId().eq(accountId);
            CustomerAccount account = CustomerAccountFinder.findOne(ts.and(id));
            account.incrementBalance(balance);
            return null;
        });
    }

    private String dumpCustomerAccount(int accountId) throws Exception
    {
        Operation historyOperation = computeHistoryOperation(accountId);
        Path tempFile = Files.createTempFile(System.nanoTime() + "", "");
        DbExtractor dbExtractor = new DbExtractor(tempFile.toFile().getAbsolutePath(), false);
        dbExtractor.addClassToFile(CustomerAccountFinder.getFinderInstance(), historyOperation);

        String data = Files.readAllLines(tempFile).stream().collect(Collectors.joining("\n"));
        System.out.println(data);
        return data;
    }

    private String dumpCustomer(int customerId) throws Exception
    {
        Operation idOp = CustomerFinder.customerId().eq(customerId);
        Operation processingDateOp = CustomerFinder
                .processingDate()
                .equalsEdgePoint();
        Operation businessDateOp = CustomerFinder
                .businessDate()
                .equalsEdgePoint();
        Operation op = idOp.and(processingDateOp).and(businessDateOp);

        Path tempFile = Files.createTempFile(System.nanoTime() + "", "");
        DbExtractor dbExtractor = new DbExtractor(tempFile.toFile().getAbsolutePath(), false);
        dbExtractor.addClassToFile(CustomerFinder.getFinderInstance(), op);

        String data = Files.readAllLines(tempFile).stream().collect(Collectors.joining("\n"));
        System.out.println(data);
        return data;
    }

    private Operation computeHistoryOperation(int accountId)
    {
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation processingDateOp = CustomerAccountFinder
                .processingDate()
                .equalsEdgePoint();
        Operation businessDateOp = CustomerAccountFinder
                .businessDate()
                .equalsEdgePoint();
        return idOp.and(processingDateOp).and(businessDateOp);
    }

    private void balanceAsOfJan12_OnJan23(int accountId)
    {
        Timestamp jan12TS = DateUtils.parse("2017-01-12");
        Timestamp jan23TS = DateUtils.parse("2017-01-23");

        Operation id = CustomerAccountFinder.accountId().eq(accountId);
        Operation businessDate = CustomerAccountFinder.businessDate().eq(jan12TS);
        Operation processingDate = CustomerAccountFinder.processingDate().eq(jan23TS);
        CustomerAccount account = CustomerAccountFinder.findOne(id.and(businessDate).and(processingDate));
        assertEquals(100, (int) account.getBalance());
    }

    /*
    This method explicitly sets the transaction's processing time to the supplied value.
    You should rarely have to do this in production.

    It is being done here just to simulate a database operation happening on a specific date,
    so that the output of thus program matches the narrative in the Reladomo tour docs.

    Without this, the processing time gets set to the time of running this program.

    Calling this method in production code violates the auditability of the data, which is the only reason the processingDate exists in the first place.
 */
    private void doNotDoThisInProduction(String date, MithraTransaction tx)
    {
        Timestamp ts = DateUtils.parse(date);
        doNotDoThisInProduction(ts, tx);
    }

    private void doNotDoThisInProduction(Timestamp ts, MithraTransaction tx)
    {
        // We set the processing time to simulate the database change happening on a specific date - Do not do this in production
        tx.setProcessingStartTime(ts.getTime());
    }

}
