package com.cerner.spine.interfaces.pds.binding;

/*
 * File - Acknowledgement.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Acknowledgement
{
    private String typeCode;
    private AcknowledgementDetail[] details;
    private InstanceIdentifier messageRef;
    
    public AcknowledgementDetail[] getDetails()
    {
        return details;
    }
    public void setDetails(AcknowledgementDetail[] details)
    {
        this.details = details;
    }
    public InstanceIdentifier getMessageRef()
    {
        return messageRef;
    }
    public void setMessageRef(InstanceIdentifier messageRef)
    {
        this.messageRef = messageRef;
    }
    public String getTypeCode()
    {
        return typeCode;
    }
    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }
    
}
