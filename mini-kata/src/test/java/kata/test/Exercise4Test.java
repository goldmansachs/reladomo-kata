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

import com.gs.fw.common.mithra.AggregateList;
import com.gs.fw.common.mithra.mtloader.AbortException;
import com.gs.fw.common.mithra.mtloader.DbLoadThread;
import com.gs.fw.common.mithra.mtloader.MatcherThread;
import com.gs.fw.common.mithra.util.SingleQueueExecutor;
import kata.domain.PersonFinder;
import kata.domain.PersonList;
import kata.domain.Pet;
import kata.domain.PetFinder;
import kata.domain.PetList;
import kata.util.TimestampProvider;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;

public class Exercise4Test extends AbstractMithraTest
{
    /**
     * This represents a classic case of data loading. Take a look at {@link Exercise4Test#getInputPetList()}.
     * It has the new data which needs to be loaded.
     * <p>
     * Use {@link com.gs.fw.common.mithra.util.MithraMultiThreadedLoader}.
     * {@code Executor}s are already created for your convenience.
     * You only need to create the {@link com.gs.fw.common.mithra.mtloader.MatcherThread}, {@link com.gs.fw.common.mithra.mtloader.DbLoadThread}.
     * {@code PetList} is the list of current {@code Pet}s
     * </p>
     * Replace the nulls with appropriate inputs.
     */
    @Test
    public void loadAllPetData() throws AbortException
    {
        MutableList<Pet> inputPetList = this.getInputPetList();

        SingleQueueExecutor singleQueueExecutor = new SingleQueueExecutor(
                1,
                PetFinder.petName().ascendingOrderBy(),
                1,
                PetFinder.getFinderInstance(),
                1);

        MatcherThread<Pet> matcherThread = null;
        matcherThread.start();

        PetList dbList = null;
        DbLoadThread dbLoadThread = null;
        dbLoadThread.start();

        matcherThread.addFileRecords(null);
        matcherThread.setFileDone();
        matcherThread.waitTillDone();

        Verify.assertSize(8,
                PetFinder.findMany(PetFinder.processingDate().eq(TimestampProvider.getPreviousDay(TimestampProvider.getCurrentDate()))));
        Verify.assertSize(8, PetFinder.findMany(PetFinder.all()));
        Verify.assertSize(1, PetFinder.findMany(PetFinder.petName().eq("Serpy")));
        Verify.assertEmpty(PetFinder.findMany(PetFinder.petName().eq("Wuzzy").or(PetFinder.petName().eq("Speedy"))));
    }

    /**
     * Similar to {@link Exercise3Test#getTotalAgeOfAllSmithPets()} only average the age of all {@code Pet}s.
     * <p>
     * Use Reladomo's aggregation methods.
     * </p>
     */
    @Test
    public void getAverageAgeOfAllPets()
    {
        AggregateList aggregateList = null;
        Assert.assertEquals(IntHashSet.newSetWith(12), aggregateList.getAttributeAsEcIntSet("averagePetAge"));
    }

    /**
     * Similar to {@link Exercise3Test#getTotalAgeOfAllSmithPets()} only get the number of {@code Pet}s per {@code Person}.
     * <p>
     * Use Reladomo's aggregation methods.
     * </p>
     */
    @Test
    public void getNumberOfPetsPerPerson()
    {
        AggregateList aggregateList = null;

        MutableMap<Integer, Integer> personIdToPetCountMap = Iterate.toMap(
                aggregateList,
                aggregateData -> aggregateData.getAttributeAsInteger("personId"),
                aggregateData -> aggregateData.getAttributeAsInteger("petCount"));

        Verify.assertMapsEqual(Maps.mutable.with(Tuples.pair(1, 1), Tuples.pair(2, 2), Tuples.pair(3, 1), Tuples.pair(5, 1), Tuples.pair(6, 1), Tuples.pair(7, 2)),
                personIdToPetCountMap);
    }

    /**
     * Add methods on Reladomo List.
     */
    @Test
    public void makePetsPlay()
    {
        PetList pets = PetFinder.findMany(PetFinder.all());
        pets.play();
    }

    /**
     * Use a cursor so that you go over each row.
     * Use Reladomo's {@code forEachWithCursor}.
     */
    @Test
    public void getListOfPersonIdsWithoutUsingCollect()
    {
        PersonList personList = PersonFinder.findMany(PersonFinder.all());
        MutableIntSet personIdSet = IntSets.mutable.empty();
        Assert.assertEquals(IntSets.mutable.with(1, 2, 3, 4, 5, 6, 7, 8), personIdSet);
    }

    private MutableList<Pet> getInputPetList()
    {
        Pet tabby = new Pet("Tabby", 1, 4, 1); // Tabby's age has changed
        Pet spot = new Pet("Spot", 1, 17, 2); // Spot's owner has changed
        Pet dolly = new Pet("Dolly", 3, 4, 1); // Dolly's owner and age has changed
        Pet spike = new Pet("Spike", 3, 5, 2); // Spike's age has changed
        Pet tweety = new Pet("Tweety", 6, 2, 4); // Tweety's age has changed
        Pet fuzzy = new Pet("Fuzzy", 7, 1, 6); // Fuzzy is unchanged
        Pet serpy = new Pet("Serpy", 4, 50, 6); // Serpy was milestoned before, but now is coming again
        Pet chirpy = new Pet("Chirpy", 2, 2, 4); // Chirpy is newly added to the Pet family
        // Wuzzy and Speedy are not coming, we need to milestone them.

        return Lists.mutable.with(tabby, spot, dolly, spike, tweety, fuzzy, serpy, chirpy);
    }
}
