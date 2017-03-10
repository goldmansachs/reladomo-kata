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
        createAccountOnJan1(accountId);
        fetchAccountWithBusinessDateJan1(accountId);
        dumpCustomerAccount(accountId);
        updateBalanceForJan17OnJan25(accountId);
        dumpCustomerAccount(accountId);
        balanceAsOfJan12_OnJan23(accountId);
        dumpCustomer(1);
    }

    private void createAccountOnJan1(int accountId)
    {
        Timestamp jan1 = DateUtils.parse("2017-01-01");
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx ->
        {
            tx.setProcessingStartTime(jan1.getTime());

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

    private void fetchAccountWithBusinessDateJan1(int accountId)
    {
        Timestamp jan1 = DateUtils.parse("2017-01-01");
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation jan1Op = CustomerAccountFinder.businessDate().eq(jan1);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(jan1Op));
        assertEquals(100, (int) account.getBalance());
    }

    private void deposit200onJan20(int accountId)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx -> {

            Timestamp jan20TS = DateUtils.parse("2017-01-20");

            tx.setProcessingStartTime(jan20TS.getTime());

            Operation ts = CustomerAccountFinder.businessDate().eq(jan20TS);
            Operation id = CustomerAccountFinder.accountId().eq(accountId);
            CustomerAccount account = CustomerAccountFinder.findOne(ts.and(id));
            account.incrementBalance(200);
            return null;
        });
    }

    private void updateBalanceForJan17OnJan25(int accountId)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx -> {

            Timestamp jan25TS = DateUtils.parse("2017-01-25");
            tx.setProcessingStartTime(jan25TS.getTime());

            Timestamp jan17TS = DateUtils.parse("2017-01-17");
            Operation ts = CustomerAccountFinder.businessDate().eq(jan17TS);
            Operation id = CustomerAccountFinder.accountId().eq(accountId);
            CustomerAccount account = CustomerAccountFinder.findOne(ts.and(id));
            account.incrementBalance(50);
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
}
