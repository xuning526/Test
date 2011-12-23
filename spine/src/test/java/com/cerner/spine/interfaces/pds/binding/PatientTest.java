package com.cerner.spine.interfaces.pds.binding;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.cerner.domain.patient.UpdatePatient;
import com.cerner.domain.patient.Patient.DatePrecision;
import com.cerner.test.BaseTestCase;

/*
 * File - PatientTest.java
 * Created Jun 4, 2009
 */

/**
 * <p>
 * TODO Insert JavaDoc
 * </p>
 * <p>
 * Copyright (c) 2009 Cerner Corporation
 * </p>
 *
 * @author JS016083 [Choose and Book]
 */
public class PatientTest extends BaseTestCase
{
    @Test
    public void testDateOfDeath()
    {
        String year = "1999";
        String month = "07";
        String day = "13";
        String hour = "09";
        String minute = "30";

        // Get a new PDS Patient object.
        Patient pdsPatient = new Patient();
        InstanceIdentifier ii = new InstanceIdentifier("", "");
        pdsPatient.setId(ii);

        // Simulate receiving YYYY as the deceased date.
        StringBuilder builder = new StringBuilder();
        builder.append(year);

        // Build calendar control object to compare translation results.
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // Return the UpdatePatient object, pull off the translated deceased date, verify against control.
        pdsPatient.setDeceasedTime(builder.toString());
        UpdatePatient updtPatient = pdsPatient.toUpdatePatient();
        Date dod = updtPatient.getDeceasedDateTime();

        assertTrue(dod.compareTo(cal.getTime()) == 0);
        assertTrue(updtPatient.getDeceasedDatePrecision() == DatePrecision.YEAR);

        // Simulate receiving YYYYMM as the deceased date
        builder.append(String.valueOf(month));

        // Reset control object.
        cal.set(Calendar.MONTH, Calendar.JULY);

        // Return the UpdatePatient object, pull off the translated deceased date, verify against control.
        pdsPatient.setDeceasedTime(builder.toString());
        updtPatient = pdsPatient.toUpdatePatient();
        dod = updtPatient.getDeceasedDateTime();

        assertTrue(dod.compareTo(cal.getTime()) == 0);
        assertTrue(updtPatient.getDeceasedDatePrecision() == DatePrecision.MONTH);

        // Simulate receiving YYYYMMDD as the deceased date
        builder.append(String.valueOf(day));

        // Reset control object.
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

        // Return the UpdatePatient object, pull off the translated deceased date, verify against control.
        pdsPatient.setDeceasedTime(builder.toString());
        updtPatient = pdsPatient.toUpdatePatient();
        dod = updtPatient.getDeceasedDateTime();

        assertTrue(dod.compareTo(cal.getTime()) == 0);
        assertTrue(updtPatient.getDeceasedDatePrecision() == DatePrecision.DAY);

        // Simulate receiving YYYYMMDDHHmm as the deceased date
        builder.append(String.valueOf(hour));
        builder.append(String.valueOf(minute));

        // Reset control object.
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(minute));

        // Return the UpdatePatient object, pull off the translated deceased date, verify against control.
        pdsPatient.setDeceasedTime(builder.toString());
        updtPatient = pdsPatient.toUpdatePatient();
        dod = updtPatient.getDeceasedDateTime();

        assertTrue(dod.compareTo(cal.getTime()) == 0);
        assertTrue(updtPatient.getDeceasedDatePrecision() == DatePrecision.MINUTE);
    }
}
