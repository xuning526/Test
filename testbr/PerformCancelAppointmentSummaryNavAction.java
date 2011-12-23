package com.cerner.presentation.cabpatient.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.presentation.core.action.ApplicationAction;
import com.cerner.presentation.util.session.PatientApplicationSessionAssistant;

/*
 * File - PerformCancelAppointmentSummaryNavAction.java
 * Created Apr 6, 2007
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
public class PerformCancelAppointmentSummaryNavAction extends ApplicationAction
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
        if (request.getParameter("logout") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"logoutAlert");
            return mapping.findForward("logoutAlert");
        }
        else if (request.getParameter("print") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"print");
            session.setAttribute(PatientApplicationSessionAssistant.PRINTING_FLOW,"cancelBookedAppt");
            return mapping.findForward("print");
        }
        else if (request.getParameter("viewAll") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"viewAll");
            return mapping.findForward("viewAll");
        }
        else if (request.getParameter("cancelAR") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"cancelAR");
            return mapping.findForward("cancelAR");
        }
        else if (session!=null)
        {   // get stuff from session
            String sessionMappingFindForward = (String) session.getAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD);
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
 *   126181          Apr 6, 2007       MM012464          Initial release.
 *   155196          Nov 20, 2007      JA015109          CR# 1-1425670864: Allowed use of 'Go' button in firefox 
 * ***********************************************************************
 */