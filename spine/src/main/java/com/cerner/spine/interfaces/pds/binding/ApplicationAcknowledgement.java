package com.cerner.spine.interfaces.pds.binding;

import com.cerner.domain.patient.UpdatePatient;
import com.cerner.domain.patient.util.PDSResponse;

/*
 * File - ApplicationAcknowledgement.java
 * Created Dec 1, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class ApplicationAcknowledgement
{
    private InstanceIdentifier id;
    private String creationTime;
    private CodedValue versionCode;
    private InstanceIdentifier interactionId;
    private CodedValue processingCode;
    private CodedValue processingMoodCode;
    private Acknowledgement acknowledgement;
    private Device receivingSystem;
    private Device sendingSystem;
    private Author author;
    private AuthorSystem[] authorSystems;
    private CodedValue[] detectedIssues;
    
    public Acknowledgement getAcknowledgement()
    {
        return acknowledgement;
    }
    public void setAcknowledgement(Acknowledgement acknowledgement)
    {
        this.acknowledgement = acknowledgement;
    }
    public Author getAuthor()
    {
        return author;
    }
    public void setAuthor(Author author)
    {
        this.author = author;
    }
    public AuthorSystem[] getAuthorSystems()
    {
        return authorSystems;
    }
    public void setAuthorSystems(AuthorSystem[] authorSystems)
    {
        this.authorSystems = authorSystems;
    }
    public String getCreationTime()
    {
        return creationTime;
    }
    public void setCreationTime(String creationTime)
    {
        this.creationTime = creationTime;
    }
    public CodedValue[] getDetectedIssues()
    {
        return detectedIssues;
    }
    public void setDetectedIssues(CodedValue[] detectedIssues)
    {
        this.detectedIssues = detectedIssues;
    }
    public InstanceIdentifier getId()
    {
        return id;
    }
    public void setId(InstanceIdentifier id)
    {
        this.id = id;
    }
    public InstanceIdentifier getInteractionId()
    {
        return interactionId;
    }
    public void setInteractionId(InstanceIdentifier interactionId)
    {
        this.interactionId = interactionId;
    }
    public CodedValue getProcessingCode()
    {
        return processingCode;
    }
    public void setProcessingCode(CodedValue processingCode)
    {
        this.processingCode = processingCode;
    }
    public CodedValue getProcessingMoodCode()
    {
        return processingMoodCode;
    }
    public void setProcessingMoodCode(CodedValue processingMoodCode)
    {
        this.processingMoodCode = processingMoodCode;
    }
    public Device getReceivingSystem()
    {
        return receivingSystem;
    }
    public void setReceivingSystem(Device receivingSystem)
    {
        this.receivingSystem = receivingSystem;
    }
    public Device getSendingSystem()
    {
        return sendingSystem;
    }
    public void setSendingSystem(Device sendingSystem)
    {
        this.sendingSystem = sendingSystem;
    }
    public CodedValue getVersionCode()
    {
        return versionCode;
    }
    public void setVersionCode(CodedValue versionCode)
    {
        this.versionCode = versionCode;
    }
    
    public PDSResponse toPDSResponse()
    {   
        PDSResponse response = new PDSResponse();
        
        // look for any errors/warnings
        if (detectedIssues != null && detectedIssues.length > 0)
        {
            for(CodedValue detectedIssue : detectedIssues)
            {
                response.addResponse(new PDSResponse.Response(detectedIssue.getCode(), detectedIssue.getDisplayName()));
            }
        }
        
        return response;
    }
}
