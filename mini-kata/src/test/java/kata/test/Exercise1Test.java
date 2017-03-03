/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kata.test;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.test.Verify;
import com.gs.fw.common.mithra.finder.Operation;
import kata.domain.*;
import org.junit.Assert;
import org.junit.Test;

public class Exercise1Test extends AbstractMithraTest
{
    /**
     * Find all the {@code Person} objects.
     * Use the appropriate method on {@link kata.domain.PersonFinder}.
     */
    @Test
    public void getAllPeople()
    {
        PersonList people = null;
        Verify.assertSize(8, people);
    }

    /**
     * Find all the {@code Person} objects with {@code lastName().eq("Smith")}.
     * Use the appropriate method on {@link kata.domain.PersonFinder}
     */
    @Test
    public void getAllSmiths()
    {
        PersonList smiths = null;
        Verify.assertSize(3, smiths);
    }

    /**
     * Find all the {@code Pet}s which are older than 1 or if the name is "Wuzzy".
     * Explore concatenation of multiple conditions using {@code and}, {@code or}.
     * Use the appropriate method on {@link kata.domain.PetFinder}
     */
    @Test
    public void getAllPets_OlderThan1_Or_Wuzzy()
    {
        PetList oldPetsOrWuzzy = null;
        Verify.assertSize(7, oldPetsOrWuzzy);
    }

    /**
     * There are no {@code Person} objects with {@code firstName} equal to "George".
     * Bulk update the {@code firstName} of only those {@code Person} objects with {@code lastName} equal to "Smith".
     * Use the appropriate method on {@link PersonFinder} and {@link PersonList}
     */
    @Test
    public void updateAllSmithsFirstNamesToGeorge()
    {
        Operation georgeSmithOperation = PersonFinder.firstName().eq("George").and(PersonFinder.lastName().eq("Smith"));

        PersonList georgeSmiths = PersonFinder.findMany(georgeSmithOperation);
        Verify.assertEmpty(georgeSmiths);

        /*
        Add the update logic here.
         */

        georgeSmiths = PersonFinder.findMany(georgeSmithOperation);
        Verify.assertSize(3, georgeSmiths);
    }

    /**
     * Insert a new {@code Person} object with first name: Jane and last name: Doe.
     * Do not add any {@code Pet}s to the new {@code Person} object.
     * Use appropriate method on {@link Person}.
     */
    @Test
    public void insertJaneDoe()
    {
        Person janeDoe = PersonFinder.findOne(PersonFinder.firstName().eq("Jane").and(PersonFinder.lastName().eq("Doe")));
        Assert.assertNull(janeDoe);

        /*
        Add the insert logic here.
         */

        janeDoe = PersonFinder.findOne(PersonFinder.firstName().eq("Jane").and(PersonFinder.lastName().eq("Doe")));

        Assert.assertNotNull(janeDoe);
        Assert.assertEquals(9, janeDoe.getPersonId());
    }
}
