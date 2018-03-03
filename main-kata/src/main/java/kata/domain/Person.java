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

package kata.domain;

import org.eclipse.collections.api.block.procedure.Procedure;

public class Person
        extends PersonAbstract
{
    // Added for ExercisesAdvancedFinder question 9
    public static final Procedure<Person> INCREMENT_AGE = new Procedure<Person>()
    {
        public void value(Person person)
        {
            person.incrementAge();
        }
    };

	public Person()
	{
		super();
		// You must not modify this constructor. Mithra calls this internally.
		// You can call this constructor. You can also add new constructors.
	}

    // Added for ExercisesAdvancedFinder question 9
    public void incrementAge()
    {
        this.setAge(this.getAge() + 1);
	}

    @Override
    public String toString()
    {
        return "Person[name=" + this.getName()
                + "; country=" + this.getCountry()
                + "; age=" + this.getAge() + ']';
    }
}
