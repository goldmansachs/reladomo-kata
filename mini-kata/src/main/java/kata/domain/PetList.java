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

import com.gs.fw.finder.Operation;

import java.util.Collection;

public class PetList extends PetListAbstract
{
    public PetList()
    {
        super();
    }

    public PetList(int initialSize)
    {
        super(initialSize);
    }

    public PetList(Collection c)
    {
        super(c);
    }

    public PetList(Operation operation)
    {
        super(operation);
    }

    /**
     * For each element in the list, print out petName() + " plays".
     */
    public void play()
    {
        throw new UnsupportedOperationException("Implement this as a part of kata.testExercise4Test.makePetsPlay");
    }
}
