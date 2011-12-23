package com.cerner.presentation.cabpatient.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.business.ebs.scheduling.AppointmentManager;
import com.cerner.data.model.Appointment;
import com.cerner.data.model.Lock;
import com.cerner.data.simpleobject.additionalrequirement.AppointmentAdditionalRequirement;
import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.DAOHelper;
import com.cerner.presentation.appointment.view.AppointmentView;
import com.cerner.presentation.util.session.PatientApplicationSessionAssistant;
import com.cerner.presentation.ebs.lock.LockRegistry;
import com.cerner.system.i18n.util.ResourceAssistant;
import com.wellogic.dbone.dao.ActRelationshipDAO;
import com.wellogic.dbone.model.ActRelationship;
import com.wellogic.dbone.model.Code;

/*
 * File - PerformCancelARAction.java
 * Created Apr 6, 2007
 */

/**
 * <p>
 * Cancels the Additional Requirements with the information stored in session
 * </p>
 * <p>
 * Copyright (c) 2007 Cerner Corporation
 * </p>
 * 
 * @author Surrounding Care [Choose and Book]
 */
public class PerformCancelARAction extends PatientLoadingPolicyAction
{
    private static final String sessionVars[] = {PatientApplicationSessionAssistant.APPT_LOCK_GLOBAL_ID, PatientApplicationSessionAssistant.VERSION};

    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        HttpSession session = request.getSession();
        Appointment appt = PatientApplicationSessionAssistant.loadSessionAppointment(session, factory);
        AppointmentView apptView = new AppointmentView(appt);

        List existingARs = apptView.getAdditionalRequirements();
        List arsToCancel = new ArrayList();

        List ars = (List) session.getAttribute(PatientApplicationSessionAssistant.ADDITIONAL_REQUIREMENTS);
        for (int x = 0; x < ars.size(); x++)
        {
            String arToCancel = (String) ars.get(x);
            int arSize = existingARs.size();
            for (int i = 0; i < arSize; i++)
            {
                AppointmentAdditionalRequirement existingAR = (AppointmentAdditionalRequirement) existingARs.get(i);
                if (existingAR.getArTypeCd().getId().equals(arToCancel))
                {
                    AppointmentAdditionalRequirement arToSubmit = new AppointmentAdditionalRequirement();
                    arToSubmit.setArStatusCd(existingAR.getArStatusCd());
                    arToSubmit.setArTypeCd(existingAR.getArTypeCd());
                    arToSubmit.setDescription(existingAR.getDescription());
                    changeARStatus(arToSubmit);
                    arsToCancel.add(arToSubmit);
                    break;
                }
            }
        }

        String apptLockGlobalId = (String) session.getAttribute(PatientApplicationSessionAssistant.APPT_LOCK_GLOBAL_ID);

        //Create template appointment
        Appointment template = (Appointment) DAOHelper.newModel(Appointment.MODEL_TITLE);

        prepareTemplate(template, appt, apptLockGlobalId, arsToCancel);

        AppointmentManager apptMgr = (AppointmentManager) factory.newBusinessManager(AppointmentManager.MANAGER_TITLE);
        apptMgr.performCancelAdditionalRequirements(template);

        //Remove lock from registry
        LockRegistry lockRegistry = (LockRegistry) session.getAttribute(LockRegistry.LOCK_REGISTRY);
        lockRegistry.removeLock(apptLockGlobalId);

        //clear session attributes that might still exist
        PatientApplicationSessionAssistant.clearParameters(session, sessionVars);

        return mapping.findForward("success");
    }

    private void changeARStatus(AppointmentAdditionalRequirement ar)
    {
        Code status = ar.getArStatusCd();
        String statusMeaning = CodeSetHelper.findCodeExtensionByCode(status).getValue("STATUS_MEANING");

        if ("BOOKED".equals(statusMeaning))
        {
            ar.setArStatusCd(CodeSetHelper.findCodeByTitle("23071", "CNLNOTREQ"));
        }
        else if ("TOBERESOLVED".equals(statusMeaning) || "NOTPROVIDED".equals(statusMeaning))
        {
            ar.setArStatusCd(CodeSetHelper.findCodeByTitle("23071", "NOTREQ"));
        }

        ar.setComment(ResourceAssistant.getString("ebspatient.cancelar.comments"));
    }

    private void prepareTemplate(Appointment template, Appointment appointment, String lockId, List arList)
    {
        template.setGlobalId(appointment.getGlobalId());

        ActRelationshipDAO actDAO = (ActRelationshipDAO) DAOHelper.newDAO(ActRelationship.MODEL_TITLE);

        // Set lock on the template
        Lock lock = (Lock) DAOHelper.newModel(Lock.MODEL_TITLE);
        lock.setGlobalId(lockId);
        actDAO.createActRelationship(template, lock, CodeSetHelper.findCodeByTitle("3505", "ISLOCKED"));

        // Create update relationship
        actDAO.createActRelationship(appointment, template, CodeSetHelper.findCodeByTitle("3505", "UPDATE"));

        // Set additional requirements on template
        template.setAdditionalRequirements(arList);
    }

    /**
     * @see com.cerner.presentation.ebs.core.action.LoadingPolicyAction#getLoadingPolicy(javax.servlet.http.HttpServletRequest)
     */
    protected String getLoadingPolicy(HttpServletRequest request)
    {
        return "cancelAdditionalRequirements";
    }
}

/*
 * **************************** MOD BLOCK ********************************
 *  FEATURE #           DATE             NAME               DESCRIPTION
 *   126181          Apr 6, 2007       MM012464          Initial release.
 * ***********************************************************************
 */