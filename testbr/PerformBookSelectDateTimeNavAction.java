package com.cerner.presentation.cabpatient.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.presentation.util.session.PatientApplicationSessionAssistant;
import com.cerner.presentation.core.action.ApplicationAction;

/*
 * File - PerformBookSelectDateTimeNavAction.java
 * Created Apr 18, 2007
 */

/**
 * <p>
 * Determines which button the user clicked
 * </p>
 * <p>
 * Copyright (c) 2007 Cerner Corporation
 * </p>
 * 
 * @author Surrounding Care [Choose and Book]
 */
public class PerformBookSelectDateTimeNavAction extends ApplicationAction
{
    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        HttpSession session = request.getSession(false);
        if ("true".equalsIgnoreCase(request.getParameter("incompleteInd")))
        {
            String slotId = request.getParameter("slotId");
            if (slotId != null)
            {
                session.setAttribute(PatientApplicationSessionAssistant.SCHEDULED_ACTIVITY_GLOBAL_ID, slotId);
            }
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"incomplete");
            return mapping.findForward("incomplete");
        }
        else if (request.getParameter("back") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"back");
            return mapping.findForward("back");
        }
        else if (request.getParameter("continue") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"continue");
            return mapping.findForward("continue");
        }
        else if (request.getParameter("backToDetails") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"backToDetails");
            return mapping.findForward("backToDetails");
        }
        else if (session!=null)
        {   // get stuff from session
            String sessionMappingFindForward = (String) session.getAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD);
            if (sessionMappingFindForward.equalsIgnoreCase("incomplete"))
            {
                String sessionSlotId = (String) session.getAttribute(PatientApplicationSessionAssistant.SCHEDULED_ACTIVITY_GLOBAL_ID);
                request.setAttribute("slotId", sessionSlotId);               
            }
            return mapping.findForward(sessionMappingFindForward);
        }
        else
        {
            throw new IllegalArgumentException("No navigation parameter found.");
        }
    }
}

/*
 * **************************** MOD BLOCK ********************************
 *  FEATURE #           DATE             NAME               DESCRIPTION
 *   126181          Apr 18, 2007       MM012464          Initial release.
 *   155196          Nov 20, 2007       JA015109          CR# 1-1425670864: Allowed use of 'Go' button in firefox 
 * ***********************************************************************
 */