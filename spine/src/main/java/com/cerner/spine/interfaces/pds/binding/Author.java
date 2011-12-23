package com.cerner.spine.interfaces.pds.binding;

/**
 * <p>This is the binding class for the Author (the person who is logged in at the time the message is initiated) class, as defined in the Message Implementation Manual.</p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class Author
{
    private InstanceIdentifier sdsRoleProfileCode;
    private InstanceIdentifier sdsUserID;
    private InstanceIdentifier sdsJobRoleCode;
    
    public Author()
    {
    }
    
    public Author(String sdsRoleProfileCode, String sdsUserID, String sdsJobRoleCode)
    {
        this.sdsRoleProfileCode = new InstanceIdentifier("1.2.826.0.1285.0.2.0.67", sdsRoleProfileCode);
        this.sdsUserID = new InstanceIdentifier("1.2.826.0.1285.0.2.0.65", sdsUserID);
        this.sdsJobRoleCode = new InstanceIdentifier("1.2.826.0.1285.0.2.1.104", sdsJobRoleCode);
    }
    
    public InstanceIdentifier getSdsRoleProfileCode()
    {
        return sdsRoleProfileCode;
    }
    public void setSdsRoleProfileCode(InstanceIdentifier sdsRoleProfileCode)
    {
        this.sdsRoleProfileCode = sdsRoleProfileCode;
    }
    public InstanceIdentifier getSdsUserID()
    {
        return sdsUserID;
    }
    public void setSdsUserID(InstanceIdentifier sdsUserID)
    {
        this.sdsUserID = sdsUserID;
    }
    public InstanceIdentifier getSdsJobRoleCode()
    {
        return sdsJobRoleCode;
    }
    public void setSdsJobRoleCode(InstanceIdentifier sdsJobRoleCode)
    {
        this.sdsJobRoleCode = sdsJobRoleCode;
    }
}
