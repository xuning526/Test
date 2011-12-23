package com.cerner.spine.interfaces.pds.binding;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class BindingTest
{
    @Test
    public void UTCDates()
    {
        // use London's timezone to simulate a date that falls under British Summer Time
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        cal.clear();
        // January 1, 1970 is a British Summer Time date
        cal.set(1970, 0, 1);
        
        // the Date representation of this calendar will be December 31, 1969
        // i.e. the getTime() method will return -360000
        Date wrongDate = cal.getTime();
        
        // simulate the same date as it would appear in the XML message
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setLow("19700101");
        Date correctDate = timeInterval.toDates()[TimeInterval.LOW];
        assertTrue(wrongDate.getTime() < correctDate.getTime());
    }
}
