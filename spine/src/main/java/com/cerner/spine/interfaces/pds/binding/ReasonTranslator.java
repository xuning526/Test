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
 * File - Reasoner.java
 * Created Dec 2, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class ReasonTranslator implements IAliasable, IMarshaller, IUnmarshaller
{
    private static final String JUSTIFYING_DETECTED_ISSUE_EVENT_ELEMENT =  "justifyingDetectedIssueEvent";
    private String uri;
    private int index;
    private String name;
    
    public ReasonTranslator()
    {
        uri = null;
        index = 0;
        name = "reason";
    }
    
    public ReasonTranslator(String uri, int idx, String name)
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
        ctx.parsePastStartTag(uri, JUSTIFYING_DETECTED_ISSUE_EVENT_ELEMENT);
        
        // hoping we can invoke the built-in marshaller for CodedValue
        CodedValue cv = (CodedValue)ctx.unmarshalElement(CodedValue.class);
        
        ctx.parsePastEndTag(uri, JUSTIFYING_DETECTED_ISSUE_EVENT_ELEMENT);
        ctx.parsePastEndTag(uri, name);
        
        return cv;
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
        if (!(obj instanceof CodedValue))
        {
            throw new JiBXException("Invalid object type for marshaller");
        }
        
        CodedValue cv = (CodedValue)obj;
        MarshallingContext ctx = (MarshallingContext)ictx;
        
        // write the constant "RSON" typeCode attribute
        ctx.startTagAttributes(index, name).
            attribute(index, "typeCode", "RSON").
        closeStartContent();
        
        // write the "justifyingDetectedIssueEvent" start tag and attributes
        ctx.startTagAttributes(index, JUSTIFYING_DETECTED_ISSUE_EVENT_ELEMENT).
            attribute(index, "classCode", "ALRT").
            attribute(index, "moodCode", "EVN").
        closeStartContent();
        
        // write the CodedValue
        ctx.getMarshaller(CodedValue.class.getName()).marshal(cv, ctx);
        
        // write the end tags
        ctx.endTag(index, JUSTIFYING_DETECTED_ISSUE_EVENT_ELEMENT);
        ctx.endTag(index, name);
    }
}
