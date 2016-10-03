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

package kata.domain;

import kata.util.TimestampProvider;

import java.sql.Timestamp;

public class Pet extends PetAbstract
{
    public Pet(Timestamp processingDate)
    {
        super(processingDate);
        // You must not modify this constructor. Mithra calls this internally.
        // You can call this constructor. You can also add new constructors.
    }

    public Pet()
    {
        this(TimestampProvider.getInfinityDate());
    }

    public Pet(String petName, int personId, int petAge, int petTypeId)
    {
        this(TimestampProvider.getInfinityDate());
        this.setPetName(petName);
        this.setPersonId(personId);
        this.setPetAge(petAge);
        this.setPetTypeId(petTypeId);
    }
}
