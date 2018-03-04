/*
 Copyright 2018 Goldman Sachs.
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

import com.gs.fw.common.mithra.MithraManagerProvider;
import com.gs.fw.finder.Navigation;
import kata.domain.PersonList;
import kata.domain.PetFinder;
import kata.domain.PetList;
import kata.domain.PetType;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class Exercise2Test extends AbstractMithraTest
{
    /**
     * Find the list of {@code Pet}s. Order them first by {@code petAge} descending, and then by {@code petName} ascending.
     * Use appropriate method on {@link PetFinder} and {@link PetList}
     */
    @Test
    public void getAllPetsDescendingOrderedByAgeAndAscendingByName()
    {
        PetList pets = null;

        Verify.assertListsEqual(Lists.mutable.with("Speedy", "Spot", "Spike", "Dolly", "Tabby", "Tweety", "Fuzzy", "Wuzzy"),
                pets.asEcList().collect(TO_PET_NAME));
    }

    /**
     * Find all the {@code Person} objects which have at least 1 {@code Pet}.
     * Use Reladomo's relationship traversal between {@code Person} and {@code Pet}
     * Use appropriate method on {@link kata.domain.PersonFinder}
     */
    @Test
    public void getAllPeopleWithPets()
    {
        PersonList petPeople = null;
        Verify.assertSize(6, petPeople);
    }

    /**
     * Get all the pets who's owner's last name is "Smith"
     * Extra credit: try getting the list of people and then the list of pets from the people list.
     */
    @Test
    public void getAllPetsOfSmiths()
    {
        PetList smithPets = null;

        Verify.assertSetsEqual(Sets.mutable.with("Dolly", "Spike", "Tabby", "Spot"),
                smithPets.asEcList().collect(TO_PET_NAME, Sets.mutable.empty()));
    }

    /**
     * Find all the {@code Person} objects which have at least one {@code Pet} of {@code petTypeId} = {@link kata.domain.PetType#DOG_ID}.
     * Use Reladomo's relationship traversal between {@code Person}, {@code Pet} and {@code PetType}.
     * Use appropriate method on {@link kata.domain.PersonFinder}
     */
    @Test
    public void getAllDogLovers()
    {
        PersonList dogLovers = null;
        Verify.assertSize(2, dogLovers);
    }

    /**
     * The relationship is one {@link kata.domain.Person} to many {@link kata.domain.Pet}s, many {@link kata.domain.Pet}s to one {@link PetType}.
     * Since there are three relationships the minimum database hits are 3.
     * Use appropriate method on {@link kata.domain.PersonFinder} and Reladomo's {@code deepFetch} property.
     *
     * @see PersonList#deepFetch(Navigation)
     */
    @Test
    public void getAllObjectsInMinDatabaseHits()
    {
        int minDatabaseHits = 3;
        PersonList people = null;

        Verify.assertSize(8, people);

        int databaseHitsAfterPeopleFetch = MithraManagerProvider.getMithraManager().getDatabaseRetrieveCount();

        for (int i = 0; i < people.size(); i++)
        {
            PetList pets = people.getPersonAt(i).getPets();
            int databaseHitsAfterPetFetch = MithraManagerProvider.getMithraManager().getDatabaseRetrieveCount();
            Assert.assertEquals(databaseHitsAfterPeopleFetch, databaseHitsAfterPetFetch);

            for (int j = 0; j < pets.size(); j++)
            {
                PetType petType = pets.getPetAt(j).getPetType();
                int databaseHitsAfterPetTypeFetch = MithraManagerProvider.getMithraManager().getDatabaseRetrieveCount();
                Assert.assertEquals(databaseHitsAfterPetFetch, databaseHitsAfterPetTypeFetch);
            }
        }
    }

    /**
     * This is similar to what you have solved before.
     * Use {@link PetType#HAMSTER_ID}.
     *
     * @see Exercise2Test#getAllDogLovers()
     */
    @Test
    public void getAllHamsterLovers()
    {
        PersonList hamsterLovers = null;
        Verify.assertSize(1, hamsterLovers);
    }
}
