/*
 * http://stackoverflow.com/questions/428918/how-can-i-increment-a-date-by-one-day-in-java
 */

package info.frangor.android.model.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
