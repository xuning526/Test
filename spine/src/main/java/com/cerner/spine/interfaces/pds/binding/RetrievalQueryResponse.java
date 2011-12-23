package com.cerner.spine.interfaces.pds.binding;

import com.cerner.domain.patient.util.PDSResponse;
import com.cerner.system.logging.Log;
import com.cerner.system.logging.LogFactory;

/**
 * <p></p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class RetrievalQueryResponse
{
    private static final Log log = LogFactory.getLog(RetrievalQueryResponse.class);
    private RetrievalQueryFailure failure;
    private RetrievalQuerySuccess success;
    private ApplicationAcknowledgement applicationAcknowledgement;
    
    public ApplicationAcknowledgement getApplicationAcknowledgement()
    {
        return applicationAcknowledgement;
    }
    public void setApplicationAcknowledgement(ApplicationAcknowledgement applicationAcknowledgement)
    {
        this.applicationAcknowledgement = applicationAcknowledgement;
    }
    public RetrievalQueryFailure getFailure()
    {
        return failure;
    }
    public void setFailure(RetrievalQueryFailure failure)
    {
        this.failure = failure;
    }
    public RetrievalQuerySuccess getSuccess()
    {
        return success;
    }
    public void setSuccess(RetrievalQuerySuccess success)
    {
        this.success = success;
    }
    
    public PDSResponse toPDSResponse()
    {
        if(success != null)
        {
            return success.toPDSResponse();
        }
        else if(failure != null)
        {
            return failure.toPDSResponse();
        }
        else if(applicationAcknowledgement != null)
        {
            return applicationAcknowledgement.toPDSResponse();
        }
        else
        {
            // this would make for an interesting scenario
            // TODO: check whether this would be caught/handled in the JiBX unmarshalling layer.
            log.warn("The PDS sent an unexpected response, check the XML message for more details.");
            return null;
        }
    }
}
