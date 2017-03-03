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

import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.Iterate;
import com.gs.fw.common.mithra.AggregateList;
import com.gs.fw.common.mithra.finder.Operation;
import kata.domain.Pet;
import kata.domain.PetFinder;
import kata.util.TimestampProvider;
import org.junit.Assert;
import org.junit.Test;

public class Exercise3Test extends AbstractMithraTest
{
    private static final Operation SPEEDY_SELECT = PetFinder.petName().eq("Speedy");

    /**
     * Sum the ages of all {@code Pet}s.
     * Use Reladomo's aggregation methods.
     * <p>
     * <p>Name {@code groupBy} column as "personId</p>
     * <p>Name {@code sum} column as "petAge"</p>
     * <p>Use appropriate methods on {@link AggregateList}</p>
     */
    @Test
    public void getTotalAgeOfAllSmithPets()
    {
        AggregateList aggregateList = null;

        aggregateList.addAggregateAttribute("petAge", PetFinder.petAge().sum());
        aggregateList.addGroupBy("personId", PetFinder.personId());

        MutableMap<Integer, Integer> personIdToTotalPetAgeMap = Iterate.toMap(
                aggregateList,
                aggregateData -> aggregateData.getAttributeAsInteger("personId"),
                aggregateData -> aggregateData.getAttributeAsInteger("petAge")
        );

        Verify.assertMapsEqual(
                Maps.mutable.with(1, 3, 2, 20, 3, 4),
                personIdToTotalPetAgeMap
        );
    }

    /**
     * Find the {@code Pet} named "Speedy" and update {@code petAge} to 70.
     * <p>Remember {@code Pet} is a processing temporal table. You cannot update the age directly,
     * but need to do it inside a {@link com.gs.fw.common.mithra.MithraTransaction}
     * </p>
     * Use appropriate method on {@link kata.domain.Pet}
     */
    @Test
    public void updateAgeOfSpeedyTo70()
    {
        /*
        Add the update logic here.
         */

        Pet currentSpeedy = PetFinder.findOne(SPEEDY_SELECT);
        Assert.assertEquals(70, currentSpeedy.getPetAge());
        Assert.assertEquals(TimestampProvider.getCurrentDate(), TimestampProvider.getDate(currentSpeedy.getProcessingDateFrom()));

        Pet previousSpeedy = PetFinder.findOne(SPEEDY_SELECT.and(PetFinder.processingDate().eq(TimestampProvider.getPreviousDay(TimestampProvider.getCurrentDate()))));
        Assert.assertEquals(68, previousSpeedy.getPetAge());
        Assert.assertEquals(TimestampProvider.getCurrentDate(), TimestampProvider.getDate(previousSpeedy.getProcessingDateTo()));
    }

    /**
     * Speedy has grown old. It is time to say goodbye.
     * <p>Milestone Speedy. Similar to {@link Exercise3Test#updateAgeOfSpeedyTo70()} you need to do it inside a {@link com.gs.fw.common.mithra.MithraTransaction}</p>
     * Use appropriate method on {@link Pet} to {@code terminate}.
     */
    @Test
    public void milestoneSpeedy()
    {
        Assert.assertNotNull(PetFinder.findOne(SPEEDY_SELECT));

        /*
        Add milestoning logic here.
         */

        Assert.assertNull(PetFinder.findOne(SPEEDY_SELECT));

        Pet lastSpeedy = PetFinder.findMany(SPEEDY_SELECT.and(PetFinder.processingDate().equalsEdgePoint()))
                .asGscList().maxBy(Pet::getProcessingDate);
        Assert.assertEquals(TimestampProvider.getCurrentDate(), TimestampProvider.getDate(lastSpeedy.getProcessingDateTo()));
    }
}
