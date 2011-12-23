package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.cerner.system.i18n.util.DateFormatter;

/*
 * File - TimeInterval.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class TimeInterval
{
    public static final int LOW = 0;
    public static final int HIGH = 1;
    
    private static final DateFormat dateFormat = DateFormatter.getDateFormat("yyyyMMdd");
    
    private String low;
    private String high;
    private String center;
    private String width;
    private String unit;
    
    public String getCenter()
    {
        return center;
    }
    public void setCenter(String center)
    {
        this.center = center;
    }
    public String getHigh()
    {
        return high;
    }
    public void setHigh(String high)
    {
        this.high = high;
    }
    public String getLow()
    {
        return low;
    }
    public void setLow(String low)
    {
        this.low = low;
    }
    public String getUnit()
    {
        return unit;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    public String getWidth()
    {
        return width;
    }
    public void setWidth(String width)
    {
        this.width = width;
    }
    
    private Calendar parseCalendar(String date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        try
        {
            calendar.setTime(dateFormat.parse(date));
            calendar.set(Calendar.DST_OFFSET, 0);
            calendar.set(Calendar.ZONE_OFFSET, 0);
        }
        catch(ParseException pe)
        {
            calendar = null;
        }
        return calendar;
    }
    
    private Date parseDate(String date)
    {
        Date result;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        try
        {
            calendar.setTime(dateFormat.parse(date));
            result = calendar.getTime();
        }
        catch(ParseException pe)
        {
            result = null;
        }
        return result;
    }
    
    public Calendar[] toCalendars()
    {

        Calendar[] calendars = new Calendar[2];
        calendars[TimeInterval.LOW] = low != null ? parseCalendar(low) : null;
        calendars[TimeInterval.HIGH] = high != null ? parseCalendar(high) : null;
        return calendars;
    }
    
    public Date[] toDates()
    {
        Date[] dates = new Date[2];
        dates[TimeInterval.LOW] = low != null ? parseDate(low) : null;
        dates[TimeInterval.HIGH] = high != null ? parseDate(high) : null;
        return dates;
    }
}
