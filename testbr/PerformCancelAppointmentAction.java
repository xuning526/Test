package com.cerner.presentation.cabpatient.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.business.ebs.exception.ActionRejectedException;
import com.cerner.business.ebs.exception.LockExpiredException;
import com.cerner.business.ebs.scheduling.AppointmentManager;
import com.cerner.business.exception.AppointmentLockExpiredException;
import com.cerner.data.model.Appointment;
import com.cerner.data.model.AppointmentTransition;
import com.cerner.data.model.Lock;
import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.DAOHelper;
import com.cerner.presentation.booking.util.ReasonHelper;
import com.cerner.presentation.cabpatient.exception.InvalidStateException;
import com.cerner.presentation.cabpatient.util.TimingAssistant;
import com.cerner.presentation.ebs.lock.LockRegistry;
import com.cerner.presentation.util.session.PatientApplicationSessionAssistant;
import com.cerner.service.exception.ExternalSystemFailureException;
import com.wellogic.dbone.dao.ActRelationshipDAO;
import com.wellogic.dbone.datatypes.ConceptDescriptor;
import com.wellogic.dbone.model.ActRelationship;
import com.wellogic.dbone.model.Code;

/*
 * File - PerformCancelAppointmentAction.java
 * Created Apr 6, 2007
 */

/**
 * <p>
 * Cancels the Appointment with the information stored in session
 * </p>
 * <p>
 * Copyright (c) 2007 Cerner Corporation
 * </p>
 * 
 * @author Surrounding Care [Choose and Book]
 */
public class PerformCancelAppointmentAction extends PatientLoadingPolicyAction
{
    private static final String sessionVars[] =
            {PatientApplicationSessionAssistant.APPT_LOCK_GLOBAL_ID, PatientApplicationSessionAssistant.VERSION, PatientApplicationSessionAssistant.CANCEL_REASON};

    private static final String CS_3505 = "3505";

    private static final String FAILURE = "cancelFailed";
    private static final String SUCCESS = "success";

    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm actionForm,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long start = System.currentTimeMillis();
        HttpSession session = request.getSession();

        String apptLockId = (String) session.getAttribute(PatientApplicationSessionAssistant.APPT_LOCK_GLOBAL_ID);
        String apptGlobalId = (String) session.getAttribute(PatientApplicationSessionAssistant.APPT_GLOBAL_ID);

        AppointmentManager apptManager = (AppointmentManager) factory.newBusinessManager(AppointmentManager.MANAGER_TITLE);
        Appointment appt = PatientApplicationSessionAssistant.loadSessionAppointment(session, factory);
        if (!(apptManager.canPerformCancel(appt) && (apptLockId != null) && (apptGlobalId != null)))
        {
            throw new InvalidStateException();
        }

        Appointment newAppt = (Appointment) DAOHelper.newModel(Appointment.MODEL_TITLE);
        newAppt.setGlobalId(apptGlobalId);

        //verify the version matches
        //NOTE: we are checking the version here instead of in the script since the force_update_ind is ALWAYS true from appointment updates
        long version = ((Long) session.getAttribute(PatientApplicationSessionAssistant.VERSION)).longValue();
        if (version != appt.getVersion())
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
        else
        {
            newAppt.setVersion(version);
        }

        Code statetransCode = CodeSetHelper.findCodeByTitle(CS_3505, "UPDATE");

        ActRelationshipDAO arDAO = (ActRelationshipDAO) DAOHelper.newDAO(ActRelationship.MODEL_TITLE);
        arDAO.createActRelationship(appt, newAppt, statetransCode);

        statetransCode = CodeSetHelper.findCodeByTitle(CS_3505, "STATETRANS");

        AppointmentTransition cancelTrans = (AppointmentTransition) DAOHelper.newModel(AppointmentTransition.MODEL_TITLE);

        String cancelReason = (String) session.getAttribute(PatientApplicationSessionAssistant.CANCEL_REASON);
        if (cancelReason != null && (cancelReason.trim().length() > 0))
        {
            //verify that reason is from the right codeset
            List codeList = ReasonHelper.getAppointmentCancelReasonsByRole();
            Code theReasonCode = CodeSetHelper.findCodeById(cancelReason);
            if (codeList.contains(theReasonCode))
            {
                cancelTrans.setReasonCode(theReasonCode);
            }
            else
            {
                return waitAndReturn(mapping, FAILURE, start, session);
            }
        }
        else
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }

        //Attach the patient to the template
        newAppt.setPatient(appt.getPatient());

        cancelTrans.setCode(new ConceptDescriptor(CodeSetHelper.findCodeByTitle("14232", "CANCEL")));
        arDAO.createActRelationship(newAppt, cancelTrans, statetransCode);

        Lock apptLock = (Lock) DAOHelper.newModel(Lock.MODEL_TITLE);
        apptLock.setGlobalId(apptLockId);
        statetransCode = CodeSetHelper.findCodeByTitle(CS_3505, "ISLOCKED");
        arDAO.createActRelationship(newAppt, apptLock, statetransCode);

        boolean success = false;
        try
        {
            apptManager.performCancel(newAppt);
            success = true;
        }
        catch(ActionRejectedException e)
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
        catch(ExternalSystemFailureException e1)
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
        catch(AppointmentLockExpiredException alee)
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
        catch(LockExpiredException lee)
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
        finally
        {
            //Remove lock from registry
            LockRegistry lockRegistry = (LockRegistry) session.getAttribute(LockRegistry.LOCK_REGISTRY);
            lockRegistry.removeLock(apptLockId);
        }

        if (success)
        {
            return waitAndReturn(mapping, SUCCESS, start, session);
        }
        else
        {
            return waitAndReturn(mapping, FAILURE, start, session);
        }
    }

    private ActionForward waitAndReturn(ActionMapping mapping, String forward, long start, HttpSession session)
    {
        TimingAssistant.waitDefault(start, "cancel");

        //remove data in session that is no longer needed
        PatientApplicationSessionAssistant.clearParameters(session, sessionVars);

        return mapping.findForward(forward);
    }

    /**
     * @see com.cerner.presentation.ebs.core.action.LoadingPolicyAction#getLoadingPolicy(javax.servlet.http.HttpServletRequest)
     */
    protected String getLoadingPolicy(HttpServletRequest arg0)
    {
        return "PerformPatientCancelAppointment";
    }
}

/*
 * **************************** MOD BLOCK ********************************
 *  FEATURE #           DATE             NAME               DESCRIPTION
 *   126181          Apr 6, 2007       MM012464          Initial release.
 * ***********************************************************************
 */