package com.cerner.spine.interfaces.pds.binding;

/**
 * <p>This is the binding class for the Author's System (the system that is involved in the initiation of the message) class, as defined in the Message Implementation Manual.</p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class AuthorSystem
{
    private InstanceIdentifier accreditedSystemID;
    
    public AuthorSystem(){}
    
    public AuthorSystem(InstanceIdentifier accreditedSystemID)
    {
        this.accreditedSystemID = accreditedSystemID;
    }

    public InstanceIdentifier getAccreditedSystemID()
    {
        return accreditedSystemID;
    }

    public void setAccreditedSystemID(InstanceIdentifier accreditedSystemID)
    {
        this.accreditedSystemID = accreditedSystemID;
    }
}
