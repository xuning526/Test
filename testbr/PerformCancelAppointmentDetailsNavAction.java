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
 * File - PerformCancelAppointmentDetailsNavAction.java
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
public class PerformCancelAppointmentDetailsNavAction extends ApplicationAction
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
            String reason = request.getParameter("reason");
            if (reason != null)
            {
                session.setAttribute(PatientApplicationSessionAssistant.CANCEL_REASON, reason);
            }
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"incomplete");
            return mapping.findForward("incomplete");
        }
        else if (request.getParameter("back") != null)
        {
            session.removeAttribute(PatientApplicationSessionAssistant.CANCEL_REASON);
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"back");
            return mapping.findForward("back");
        }
        else if (request.getParameter("continue") != null)
        {
            session.setAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD,"continue");
            return mapping.findForward("continue");
        }
        else if (session!=null)
        {   // get stuff from session
            String sessionMappingFindForward = (String) session.getAttribute(PatientApplicationSessionAssistant.MAPPING_FIND_FORWARD);
            if (sessionMappingFindForward.equalsIgnoreCase("incomplete"))
            {
                String sessionCancelReason = (String) session.getAttribute(PatientApplicationSessionAssistant.CANCEL_REASON);  
                request.setAttribute("reason", sessionCancelReason);
            }
            else
            {
                if (sessionMappingFindForward.equalsIgnoreCase("back"))
                {
                    session.removeAttribute(PatientApplicationSessionAssistant.CANCEL_REASON);
                }
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
 *   126181          Apr 6, 2007       MM012464          Initial release.
 *   155196          Nov 20, 2007      JA015109          CR# 1-1425670864: Allowed use of 'Go' button in firefox 
 * ***********************************************************************
 */