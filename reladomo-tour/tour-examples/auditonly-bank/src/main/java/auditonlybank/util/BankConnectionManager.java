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

package auditonlybank.util;

import com.gs.fw.common.mithra.bulkloader.BulkLoader;
import com.gs.fw.common.mithra.bulkloader.BulkLoaderException;
import com.gs.fw.common.mithra.connectionmanager.SourcelessConnectionManager;
import com.gs.fw.common.mithra.connectionmanager.XAConnectionManager;
import com.gs.fw.common.mithra.databasetype.DatabaseType;
import com.gs.fw.common.mithra.databasetype.Udb82DatabaseType;

import java.sql.Connection;
import java.util.TimeZone;

/**
 * Created by stanle on 11/23/16.
 */

public class BankConnectionManager implements SourcelessConnectionManager
{
    protected static BankConnectionManager instance;
    private XAConnectionManager xaConnectionManager;

    protected BankConnectionManager()
    {
        this.createConnectionManager();
    }

    public static synchronized BankConnectionManager getInstance()
    {
        if (instance == null)
        {
            instance = new BankConnectionManager();
        }
        return instance;
    }

    private void createConnectionManager()
    {
        this.xaConnectionManager = new XAConnectionManager();
        xaConnectionManager.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        xaConnectionManager.setHostName("my.db.host");
        xaConnectionManager.setPort(12345);
        xaConnectionManager.setJdbcUser("user1");
        xaConnectionManager.setJdbcPassword("password1");
        xaConnectionManager.setMaxWait(500);
        xaConnectionManager.setPoolName("pet store connection pool");
        xaConnectionManager.setPoolSize(10);
        xaConnectionManager.setInitialSize(1);
        xaConnectionManager.initialisePool();
    }

    @Override
    public Connection getConnection()
    {
        return xaConnectionManager.getConnection();
    }

    @Override
    public DatabaseType getDatabaseType()
    {
        return Udb82DatabaseType.getInstance();
    }

    @Override
    public TimeZone getDatabaseTimeZone()
    {
        return TimeZone.getTimeZone("America/New York");
    }

    //this uniquely identifies the database from which the connection is acquired
    @Override
    public String getDatabaseIdentifier()
    {
        return xaConnectionManager.getHostName() + ":" + xaConnectionManager.getPort();
    }

    @Override
    public BulkLoader createBulkLoader() throws BulkLoaderException
    {
        return null;
    }

}
