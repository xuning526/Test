package com.cerner.spine.interfaces.pds.binding;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

/*
 * File - Consenter.java
 * Created Dec 3, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class ConsentTranslator implements IAliasable, IMarshaller, IUnmarshaller
{
    private String uri;
    private int index;
    private String name;
    
    public ConsentTranslator()
    {
        uri = null;
        index = 0;
        name = "subjectOf3";
    }
    
    public ConsentTranslator(String uri, int idx, String name)
    {
        this.uri = uri;
        this.index = idx;
        this.name = name;
    }
    
    /**
     * @see org.jibx.runtime.IUnmarshaller#isPresent(org.jibx.runtime.IUnmarshallingContext)
     */
    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException
    {
        return ctx.isAt(uri, name);
    }

    /**
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object, org.jibx.runtime.IUnmarshallingContext)
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException
    {
        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
        
        // ensure the parser is at the correct element
        if (!ctx.isAt(uri, name))
        {
            ctx.throwStartTagNameError(uri, name);
        }
        
        ctx.parsePastStartTag(uri, name);
        
        // invoke the built-in marshaller for Consent
        Consent c = (Consent)ctx.unmarshalElement(Consent.class);
        //Object o = ctx.unmarshalElement();
        
        ctx.parsePastEndTag(uri, name);
        
        return c;
    }

    /**
     * @see org.jibx.runtime.IMarshaller#isExtension(java.lang.String)
     */
    public boolean isExtension(String mapname)
    {
        return false;
    }

    /**
     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object, org.jibx.runtime.IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException
    {   
        Consent c = (Consent)obj;
        MarshallingContext ctx = (MarshallingContext)ictx;
        
        // write the constant "SJB" typeCode attribute
        ctx.startTagAttributes(index, name).
            attribute(index, "typeCode", "SBJ").
        closeStartContent();
        
        // write the Consent
        ctx.getMarshaller(Consent.class.getName()).marshal(c, ctx);
        
        // write the end tags
        ctx.endTag(index, name);
    }
}
