package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.cerner.domain.patient.UpdatePatient;
import com.cerner.system.i18n.util.DateFormatter;
import com.cerner.test.BaseTestCase;

/*
 * File - EffectiveDateTimeTest.java
 * Created Jun 23, 2009
 */

/**
 * <p>
 * TODO Insert JavaDoc
 * </p>
 * <p>
 * Copyright (c) 2009 Cerner Corporation
 * </p>
 *
 * @author Vinayak Kandiah [Choose and Book]
 */
public class EffectiveDateTimeTest extends BaseTestCase
{
    private String today;
    private String before;
    private String after;
    
    @Before
    public void setUp()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        DateFormat dateFormat = DateFormatter.getDateFormat("yyyyMMdd");
        today = dateFormat.format(cal.getTime());
        
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        before = dateFormat.format(cal.getTime());
        
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) +2);
        after = dateFormat.format(cal.getTime());
    }
    
    @Test
    public void testValidDateRange()
    {
        Patient patient = new Patient();
        InstanceIdentifier ii = new InstanceIdentifier("", "");
        patient.setId(ii);
        TimeInterval usablePeriod = new TimeInterval();
        usablePeriod.setCenter("0");
        usablePeriod.setUnit("days");
        usablePeriod.setWidth("5");
        Address[] addressArr = new Address[1];
        Address address = new Address();
        address.setUse("H");
        address.setPdsId(ii);
        
        address.setUsablePeriod(null);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        UpdatePatient updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 1);
        
        
        usablePeriod.setLow(null);
        usablePeriod.setHigh(before);
        address.setUsablePeriod(usablePeriod);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 0);
        
        usablePeriod.setLow(after);
        usablePeriod.setHigh(today);
        address.setUsablePeriod(usablePeriod);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 0);
        
        usablePeriod.setLow(after);
        usablePeriod.setHigh(before);
        address.setUsablePeriod(usablePeriod);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 0);
        
        usablePeriod.setLow(before);
        usablePeriod.setHigh(after);
        address.setUsablePeriod(usablePeriod);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 1);
        
        usablePeriod.setLow(today);
        usablePeriod.setHigh(null);
        address.setUsablePeriod(usablePeriod);
        addressArr[0] = address;
        patient.setAddresses(addressArr);
        updatePatient = patient.toUpdatePatient();
        assertTrue(updatePatient.getAddresses().size() == 1);
    }
}
