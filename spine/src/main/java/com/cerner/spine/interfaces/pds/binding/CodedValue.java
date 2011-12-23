package com.cerner.spine.interfaces.pds.binding;

/**
 * <p>This is the binding class for the CodedValue (II) data type, as defined in the Message Implementation Manual.</p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * 
 * @author Sean Parmelee
 */
public class CodedValue
{
    private String code;
    private String codeSystem;
    private String codeSystemName;
    private String codeSystemVersion;
    private String displayName;
    private String originalText;
    
    public CodedValue(){}
    
    public CodedValue(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCodeSystem()
    {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem)
    {
        this.codeSystem = codeSystem;
    }

    public String getCodeSystemName()
    {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName)
    {
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemVersion()
    {
        return codeSystemVersion;
    }

    public void setCodeSystemVersion(String codeSystemVersion)
    {
        this.codeSystemVersion = codeSystemVersion;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(String originalText)
    {
        this.originalText = originalText;
    }
}