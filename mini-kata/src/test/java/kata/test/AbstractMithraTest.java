/*
 Copyright 2017 Goldman Sachs.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package kata.test;

import com.gs.collections.api.block.function.Function;
import com.gs.fw.common.mithra.test.ConnectionManagerForTests;
import com.gs.fw.common.mithra.test.MithraTestResource;
import kata.domain.Pet;
import org.junit.After;
import org.junit.Before;

public class AbstractMithraTest
{
    public static final Function<Pet, String> TO_PET_NAME = Pet::getPetName;

    private MithraTestResource mithraTestResource;

    protected String getMithraConfigXmlFilename()
    {
        return "testconfig/TestMithraRuntimeConfig.xml";
    }

    @Before
    public void setUp() throws Exception
    {
        this.mithraTestResource = new MithraTestResource(this.getMithraConfigXmlFilename());
        final ConnectionManagerForTests connectionManager = ConnectionManagerForTests.getInstanceForDbName("mithra_db");
        this.mithraTestResource.createSingleDatabase(connectionManager);
        this.mithraTestResource.addTestDataToDatabase("testdata/data_AllTypes.txt", connectionManager);
        this.mithraTestResource.setUp();
    }

    @After
    public void tearDown() throws Exception
    {
        this.mithraTestResource.tearDown();
    }
}
