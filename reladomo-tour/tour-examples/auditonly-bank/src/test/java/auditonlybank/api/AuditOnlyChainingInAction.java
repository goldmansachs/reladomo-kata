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

package auditonlybank.api;

import auditonlybank.domain.Customer;
import auditonlybank.domain.CustomerAccount;
import auditonlybank.domain.CustomerAccountFinder;
import auditonlybank.util.DateUtils;
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

public class AuditOnlyChainingInAction
{
    private String testRuntimeConfigXML = "testconfig/AuditOnlyBankTestRuntimeConfiguration.xml";

    @Before
    public void setup() throws Exception
    {
        intializeReladomoForTest();
    }

    private void intializeReladomoForTest()
    {
        MithraTestResource testResource = new MithraTestResource(testRuntimeConfigXML);
        ConnectionManagerForTests connectionManager = ConnectionManagerForTests.getInstance("test_db");
        testResource.createSingleDatabase(connectionManager, "testconfig/AuditOnlyChainingInActionTestData.txt");
        testResource.setUp();
    }

    @Test
    public void run() throws Exception
    {
        int accountId = 12345;
        createAccountOnJan1(accountId);
        fetchLatestAccount(accountId);
        updateBalance(accountId, 200);
        dumpCustomerAccount(accountId);
        updateBalance(accountId, 50);
        dumpCustomerAccount(accountId);
    }

    private void createAccountOnJan1(int accountId)
    {
        Customer customer = new Customer();
        customer.setFirstName("mickey");
        customer.setLastName("mouse");
        customer.setCustomerId(1);
        customer.setCountry("usa");

        CustomerAccount account = new CustomerAccount();
        account.setAccountId(accountId);
        account.setBalance(100);
        account.setAccountType("savings");
        account.setAccountName("retirement");
        customer.getAccounts().add(account);
        customer.cascadeInsert();
    }

    private void fetchLatestAccount(int accountId)
    {
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx -> {
            CustomerAccount account = CustomerAccountFinder.findOne(idOp);
            assertEquals(100, (int) account.getBalance());
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

    private Operation computeHistoryOperation(int accountId)
    {
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation processingDateOp = CustomerAccountFinder
                .processingDate()
                .equalsEdgePoint();
        return idOp.and(processingDateOp);
    }

    private void updateBalance(int accountId, int deposit)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(tx ->
        {
            Timestamp mar = DateUtils.parse("2017-03-01");
            tx.setProcessingStartTime(mar.getTime());
            Operation id = CustomerAccountFinder.accountId().eq(accountId);
            CustomerAccount account = CustomerAccountFinder.findOne(id);
            account.setBalance(account.getBalance() + deposit);
            return null;
        });
    }

    @Test
    public void run2() throws Exception
    {
        int accountId = 5678;
        balanceAsOfJan1(accountId);
        balanceAsOfJan20(accountId);
        balanceAsOfJan25(accountId);
        balanceAsOfJan17(accountId);
    }

    private void balanceAsOfJan1(int accountId)
    {
        Timestamp date = DateUtils.parse("2017-01-01");
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation dateOp = CustomerAccountFinder.processingDate().eq(date);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(dateOp));
        assertEquals(100, (int) account.getBalance());
    }

    private void balanceAsOfJan20(int accountId)
    {
        Timestamp date = DateUtils.parse("2017-01-20");
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation dateOp = CustomerAccountFinder.processingDate().eq(date);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(dateOp));
        assertEquals(300, (int) account.getBalance());
    }

    private void balanceAsOfJan25(int accountId)
    {
        Timestamp date = DateUtils.parse("2017-01-25");
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation dateOp = CustomerAccountFinder.processingDate().eq(date);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(dateOp));
        assertEquals(350, (int) account.getBalance());
    }

    private void balanceAsOfJan17(int accountId)
    {
        Timestamp date = DateUtils.parse("2017-01-17");
        Operation idOp = CustomerAccountFinder.accountId().eq(accountId);
        Operation dateOp = CustomerAccountFinder.processingDate().eq(date);
        CustomerAccount account = CustomerAccountFinder.findOne(idOp.and(dateOp));

        /*
            This balance is incorrect !!
            Even though we adjusted for the missed deposit on Jan 17, there is no way to query for the balance as of Jan 17
        */
        assertEquals(100, (int) account.getBalance());
    }
}
