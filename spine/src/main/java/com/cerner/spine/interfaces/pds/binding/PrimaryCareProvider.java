package com.cerner.spine.interfaces.pds.binding;

/*
 * File - PrimaryCareProvider.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class PrimaryCareProvider
{
    private InstanceIdentifier pdsId;
    private CodedValue code;
    private TimeInterval effectiveTime;
    private InstanceIdentifier id;
    
    public CodedValue getCode()
    {
        return code;
    }
    public void setCode(CodedValue code)
    {
        this.code = code;
    }
    public TimeInterval getEffectiveTime()
    {
        return effectiveTime;
    }
    public void setEffectiveTime(TimeInterval effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }
    public InstanceIdentifier getId()
    {
        return id;
    }
    public void setId(InstanceIdentifier id)
    {
        this.id = id;
    }
    public InstanceIdentifier getPdsId()
    {
        return pdsId;
    }
    public void setPdsId(InstanceIdentifier pdsId)
    {
        this.pdsId = pdsId;
    }
}
