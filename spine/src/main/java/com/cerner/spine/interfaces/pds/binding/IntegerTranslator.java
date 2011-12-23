package com.cerner.spine.interfaces.pds.binding;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

//import com.cerner.system.logging.Log;
//import com.cerner.system.logging.LogFactory;

/*
 * File - Integerer.java
 * Created Dec 2, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class IntegerTranslator implements IAliasable, IMarshaller, IUnmarshaller
{
    //private static Log LOG = LogFactory.getLog(IntegerTranslator.class);
    
    private static final String NULL_FLAVOR_ATTRIBUTE = "nullFlavor";
    private static final String VALUE_ATTRIBUTE = "value";
    
    private String uri;
    private int index;
    private String name;
    
    public IntegerTranslator()
    {
        uri = null;
        index = 0;
        name = VALUE_ATTRIBUTE;
    }
    
    public IntegerTranslator(String uri, int idx, String name)
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
        
        Integer i = null;
        
        if(ctx.hasAttribute(null, VALUE_ATTRIBUTE))
        {
            // set the value
            i = Integer.valueOf(ctx.attributeInt(null, VALUE_ATTRIBUTE));
        }
        else
        {
            // the "nullFlavor" attribute is set. the attribute value indicates why the value is not set
            // this may be be useful in the future, so the following line of code is an example of how to retrieve the value
            // String nullFlavor = ctx.attributeText(uri, NULL_FLAVOR_ATTRIBUTE);
        }
        
        ctx.parsePastEndTag(uri, name);
        
        return i;
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
        Integer i = (Integer)obj;
        MarshallingContext ctx = (MarshallingContext)ictx;
        
        ctx.startTagAttributes(index, name);
        
        if(i != null)
        {
            ctx.attribute(index, VALUE_ATTRIBUTE, i.toString());
        }
        else
        {
            // not fully supported; hardcoded to "UNK" for now which means "unknown"
            ctx.attribute(index, NULL_FLAVOR_ATTRIBUTE, "UNK");
        }
        ctx.closeStartEmpty();
    }
}
