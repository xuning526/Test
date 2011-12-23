package com.cerner.spine.interfaces.pds.binding;

/**
 * <p>This is the binding class for the InstanceIdentifier (II) data type, as defined in the Message Implementation Manual.</p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class InstanceIdentifier
{
    private String root;
    private String extension;
    private String assigningAuthorityName;
    private String displayable;
    
    public InstanceIdentifier(){}
    
    public InstanceIdentifier(String root)
    {
        this.root = root;
    }
    
    public InstanceIdentifier(String root, String extension)
    {
        this.root = root;
        this.extension = extension;
    }
    
    public String getRoot()
    {
        return root;
    }
    
    public void setRoot(String root)
    {
        this.root = root;
    }
    
    public String getExtension()
    {
        return extension;
    }
    
    public void setExtension(String extension)
    {
        this.extension = extension;
    }
    
    public String getAssigningAuthorityName()
    {
        return assigningAuthorityName;
    }
    
    public void setAssigningAuthorityName(String assigningAuthorityName)
    {
        this.assigningAuthorityName = assigningAuthorityName;
    }
    
    public String getDisplayable()
    {
        return displayable;
    }
    
    public void setDisplayable(String displayable)
    {
        this.displayable = displayable;
    }
}
