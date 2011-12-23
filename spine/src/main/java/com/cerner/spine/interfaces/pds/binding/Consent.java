package com.cerner.spine.interfaces.pds.binding;

/*
 * File - Consent.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Consent
{
    private String effectiveTime;
    private CodedValue type;
    private CodedValue value;
    
    public String getEffectiveTime()
    {
        return effectiveTime;
    }
    public void setEffectiveTime(String effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }
    public CodedValue getType()
    {
        return type;
    }
    public void setType(CodedValue type)
    {
        this.type = type;
    }
    public CodedValue getValue()
    {
        return value;
    }
    public void setValue(CodedValue value)
    {
        this.value = value;
    }
}
