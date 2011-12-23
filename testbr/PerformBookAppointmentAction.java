package com.cerner.presentation.cabpatient.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.data.model.Appointment;
import com.cerner.presentation.cabpatient.exception.InvalidForwardException;
import com.cerner.presentation.cabpatient.util.PatientApplicationActionClassHelper;
import com.cerner.presentation.cabpatient.util.PatientApplicationSchedulingActionHelper;
import com.cerner.presentation.util.session.PatientApplicationSessionAssistant;

/*
 * File - PerformBookAppointmentAction.java
 * Created Apr 6, 2007
 */

/**
 * <p>
 * Books the Appointment with the information stored in session
 * </p>
 * <p>
 * Copyright (c) 2007 Cerner Corporation
 * </p>
 * 
 * @author Surrounding Care [Choose and Book]
 */
public class PerformBookAppointmentAction extends PatientLoadingPolicyAction
{
    private static final String sessionVars[] =
            {PatientApplicationSessionAssistant.APPT_LOCK_GLOBAL_ID, PatientApplicationSessionAssistant.VERSION, PatientApplicationSessionAssistant.SCHEDULING_SERVICE_GLOBAL_IDS,
                    PatientApplicationSessionAssistant.FULL_SCHEDULING_SERVICE_GLOBAL_IDS,
                    PatientApplicationSessionAssistant.SCHEDULING_SERVICES_AVAILABLE_FOR_BOOKING_COUNT, PatientApplicationSessionAssistant.SLOT_PAGE,
                    PatientApplicationSessionAssistant.NUMBER_OF_RESULTS, PatientApplicationSessionAssistant.SCHEDULED_ACTIVITY_GLOBAL_ID,
                    PatientApplicationSessionAssistant.SLOT_SCHEDULE_RESERVATION_GLOBAL_ID,
                    PatientApplicationSessionAssistant.PATIENT_SCHEDULE_RESERVATION_GLOBAL_ID, PatientApplicationSessionAssistant.UNAVAILABLE_SLOT_IDS};

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        long startTime = System.currentTimeMillis();

        HttpSession session = request.getSession();

        long findByGlobalIdStart = System.currentTimeMillis();
        Appointment savedAppt = PatientApplicationSessionAssistant.loadSessionAppointment(session, factory);
        long findByGlobalIdEnd = System.currentTimeMillis();

        String forward =
                PatientApplicationSchedulingActionHelper.doBook(savedAppt, session, factory, startTime, findByGlobalIdEnd
                        - findByGlobalIdStart);
        if ("success".equals(forward))
        {
            PatientApplicationSessionAssistant.clearParameters(session, sessionVars);
            return mapping.findForward(forward);
        }
        else if ("versionMismatch".equals(forward))
        {
            return mapping.findForward("timeout");
        }
        else if ("apptLockExpired".equals(forward))
        {
            return mapping.findForward("lockExpired");
        }
        else if ("systemUnavailable".equals(forward) || "slotLockExpired".equals(forward))
        {
            //update the list of unavailable slots
            List unavailableSlots = (List) session.getAttribute(PatientApplicationSessionAssistant.UNAVAILABLE_SLOT_IDS);
            if (unavailableSlots == null)
            {
                unavailableSlots = new ArrayList(1);
            }
            unavailableSlots.add(session.getAttribute(PatientApplicationSessionAssistant.SCHEDULED_ACTIVITY_GLOBAL_ID));
            session.setAttribute(PatientApplicationSessionAssistant.UNAVAILABLE_SLOT_IDS, unavailableSlots);

            //Determine if there are more available slots
            Integer numOfResults = (Integer) session.getAttribute(PatientApplicationSessionAssistant.NUMBER_OF_RESULTS);
            boolean slotsAvailable = numOfResults.intValue() > unavailableSlots.size();

            //Determine if all of the services available for booking were selected
            int selectedServiceCount =
                    PatientApplicationActionClassHelper.stringToList(
                        (String) session.getAttribute(PatientApplicationSessionAssistant.SCHEDULING_SERVICE_GLOBAL_IDS), ",").size();
            int allServicesCount =
                    ((Integer) session.getAttribute(PatientApplicationSessionAssistant.SCHEDULING_SERVICES_AVAILABLE_FOR_BOOKING_COUNT)).intValue();
            boolean allServicesSelected = (selectedServiceCount == allServicesCount);

            if (!slotsAvailable)
            {
                if (allServicesSelected)
                {
                    return mapping.findForward("bookFailed");
                }
                else
                {
                    return mapping.findForward("slotUnavailable");
                }
            }
            else
            {
                if ("systemUnavailable".equals(forward))
                {
                    return mapping.findForward("pasDown");
                }
                else
                {
                    return mapping.findForward("slotUnavailable");
                }
            }
        }
        else
        {
            //should not reach here
            throw new InvalidForwardException();
        }
    }

    /**
     * @see com.cerner.presentation.ebs.core.action.LoadingPolicyAction#getLoadingPolicy(javax.servlet.http.HttpServletRequest)
     */
    protected String getLoadingPolicy(HttpServletRequest request)
    {
        return "PerformPatientBookAppointment";
    }
}

/*
 * **************************** MOD BLOCK ********************************
 *  FEATURE #           DATE             NAME               DESCRIPTION
 *   126181          Apr 6, 2007       MM012464          Initial release.
 *   152929          10/18/2007        SS011948          Clearing system error after unavailable slot
 * ***********************************************************************
 */