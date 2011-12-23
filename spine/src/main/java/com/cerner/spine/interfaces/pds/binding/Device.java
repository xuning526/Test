package com.cerner.spine.interfaces.pds.binding;

/**
 * <p>This is the binding class for the Device class, as defined in the Message Implementation Manual.</p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class Device
{
    private InstanceIdentifier[] ids;
    
    public Device(){}
    
    public Device(InstanceIdentifier id)
    {
        this.ids = new InstanceIdentifier[]{id};
    }

    public InstanceIdentifier[] getIds()
    {
        return ids;
    }

    public void setIds(InstanceIdentifier[] ids)
    {
        this.ids = ids;
    }
}