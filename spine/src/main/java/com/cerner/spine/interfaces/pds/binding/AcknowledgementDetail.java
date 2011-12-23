package com.cerner.spine.interfaces.pds.binding;

/*
 * File - AcknowledgementDetail.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class AcknowledgementDetail
{
    private String typeCode;
    private CodedValue code;
    
    public CodedValue getCode()
    {
        return code;
    }
    public void setCode(CodedValue code)
    {
        this.code = code;
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
