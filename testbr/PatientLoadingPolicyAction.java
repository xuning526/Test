package com.cerner.presentation.cabpatient.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cerner.presentation.cabpatient.bean.PatientActionBean;
import com.cerner.presentation.cabpatient.util.PatientApplicationActionClassHelper;
import com.cerner.presentation.ebs.core.action.LoadingPolicyAction;

/*
 * File - PatientAppLoadingPolicyAction.java
 * Created Jun 14, 2007
 */

/**
 * <p>
 * Extension of LoadingPolicyAction to handle repeated tasks in the patient application
 * </p>
 * <p>
 * Copyright (c) 2007 Cerner Corporation
 * </p>
 * 
 * @author Surrounding Care [Choose and Book]
 */
public abstract class PatientLoadingPolicyAction extends LoadingPolicyAction
{

    /**
     * @see com.cerner.presentation.struts.extended.ActionEvents#doBeforeExecute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.Action, org.apache.struts.action.ActionForm, org.apache.struts.action.ActionMapping)
     */
    public ActionForward doBeforeExecute(HttpServletRequest request,
            HttpServletResponse response,
            Action action,
            ActionForm form,
            ActionMapping mapping) throws IOException, ServletException
    {
        request.setAttribute(PatientActionBean.ACTION_BEAN, new PatientActionBean());
        PatientApplicationActionClassHelper.setGlobalInfo(request, response);
        request.setAttribute("nhsLink", PatientApplicationActionClassHelper.NHS_LOGO_LINK_URL);
        request.setAttribute("healthspaceLink", PatientApplicationActionClassHelper.HEALTHSPACE_LINK_URL);
        return super.doBeforeExecute(request, response, action, form, mapping);

    }

    /**
     * This makes a call to the TransactionManager.commit().
     */
    public ActionForward doAfterExecute(HttpServletRequest request,
            HttpServletResponse response,
            Action action,
            ActionForm form,
            ActionMapping mapping,
            ActionForward forward) throws IOException, ServletException
    {
        PatientApplicationActionClassHelper.setCurrentPage(request, (PatientActionBean) request
            .getAttribute(PatientActionBean.ACTION_BEAN));
        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
        response.setHeader("Pragma","no-cache"); //HTTP 1.0
        response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
        return super.doAfterExecute(request, response, action, form, mapping, forward);
    }
}

/*
 * **************************** MOD BLOCK ********************************
 *  FEATURE #   DATE         NAME        DESCRIPTION
 *  126181      07/25/2007   MM012464    New PWA - Initial release.
 *  147451      09/19/2007   SS011948    Forcing page refresh when using the browser's back button
 * ***********************************************************************
 */