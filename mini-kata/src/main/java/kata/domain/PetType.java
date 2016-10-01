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

public class PetType extends PetTypeAbstract
{
    public static final int CAT_ID = 1;
    public static final int DOG_ID = 2;
    public static final int SNAKE_ID = 3;
    public static final int BIRD_ID = 4;
    public static final int TURTLE_ID = 5;
    public static final int HAMSTER_ID = 6;

    public PetType()
    {
        super();
        // You must not modify this constructor. Mithra calls this internally.
        // You can call this constructor. You can also add new constructors.
    }
}
