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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

/**
 * Created by stanle on 2/24/17.
 */
public class DateUtils
{
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("YYY-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMATTER_FULL = DateTimeFormat.forPattern("YYY-MM-dd HH:mm:ss.SSS");

    public static Timestamp parseFull(String dateTimeString)
    {
        DateTime dateTime = DATE_TIME_FORMATTER_FULL.parseDateTime(dateTimeString);
        return new Timestamp(dateTime.toDateTime().getMillis());
    }

    public static String printFull(Timestamp ts)
    {
        return DATE_TIME_FORMATTER_FULL.print(ts.getTime());
    }

    public static Timestamp parse(String dateTimeString)
    {
        DateTime dateTime = DATE_TIME_FORMATTER.parseDateTime(dateTimeString);
        return new Timestamp(dateTime.toDateTime().getMillis());
    }

    public static String print(Timestamp ts)
    {
        return DATE_TIME_FORMATTER.print(ts.getTime());
    }
}
