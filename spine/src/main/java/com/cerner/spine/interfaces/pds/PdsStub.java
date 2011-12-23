/**
 * PdsStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.cerner.spine.interfaces.pds;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.axis2.AxisFault;
import org.apache.axis2.util.XMLPrettyPrinter;

import com.cerner.data.util.ConfigurationHelper;
import com.cerner.spine.interfaces.pds.binding.RetrievalQuery;
import com.cerner.spine.interfaces.pds.binding.RetrievalQueryResponse;
import com.cerner.system.registry.Registry;

public class PdsStub extends org.apache.axis2.client.Stub
{
    protected org.apache.axis2.description.AxisOperation[] _operations;

    // hashmaps to keep the fault mapping
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();

    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();

    private java.util.HashMap faultMessageMap = new java.util.HashMap();

    private static int counter = 0;

    private static synchronized String getUniqueSuffix()
    {
        // reset the counter if it is greater than 99999
        if (counter > 99999)
        {
            counter = 0;
        }
        counter = counter + 1;
        return Long.toString(System.currentTimeMillis()) + "_" + counter;
    }

    private void populateAxisService() throws org.apache.axis2.AxisFault
    {

        // creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("Pds" + getUniqueSuffix());
        addAnonymousOperations();

        // creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[1];

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("urn:hl7-org:v3", "RetrievalQuery_6B"));
        _service.addOperation(__operation);

        _operations[0] = __operation;

    }

    // populates the faults
    private void populateFaults()
    {

    }

    /**
     * Constructor that takes in a configContext
     */

    public PdsStub(org.apache.axis2.context.ConfigurationContext configurationContext, java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault
    {
        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Constructor that takes in a configContext and useseperate listner
     */
    public PdsStub(org.apache.axis2.context.ConfigurationContext configurationContext, java.lang.String targetEndpoint,
            boolean useSeparateListener) throws org.apache.axis2.AxisFault
    {
        // To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

        configurationContext = _serviceClient.getServiceContext().getConfigurationContext();

        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

    }

    /**
     * Constructor taking the target endpoint
     */
    public PdsStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault
    {
        this(null, targetEndpoint);
    }

    /**
     * <p>Performs the PDS' RetrievalQuery operation</p>>
     * 
     * @param QUPA_IN040000UK32
     * 
     */
    /* this method was originally auto-generated, but has been customized in the following ways:
     * - an Object is returned instead of RetrievalQueryResponse
     * - an 'unmarshall' parameter has been added to allow the unmarshalling process to be skipped (the XML is returned)
     */
    public Object RetrievalQuery(RetrievalQuery QUPA_IN040000UK32, boolean unmarshall)
    throws java.rmi.RemoteException
    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try
        {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction("urn:nhs:names:services:pdsquery/QUPA_IN040000UK32");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            
            Registry registry = Registry.registry();
            String timeOutConfig = ConfigurationHelper.getConfigurableValue(registry, "/Application/Ebookings/PDS/TimeOut");
            if (timeOutConfig != null && timeOutConfig.length() > 0)
            {
                try
                {
                    // the config is in seconds, but Axis expects milliseconds
                    _operationClient.getOptions().setTimeOutInMilliSeconds(Integer.parseInt(timeOutConfig)*1000);
                }
                catch (NumberFormatException nfe){}
            }
			
            addPropertyToOperationClient(_operationClient,
                org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), QUPA_IN040000UK32,
                        optimizeContent(new javax.xml.namespace.QName("urn:hl7-org:v3", "RetrievalQuery_6B")));

            // adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            // execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            
            if (unmarshall)
            {
                return fromOM(_returnEnv.getBody().getFirstElement(),
                    RetrievalQueryResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            }
            else
            {
                OutputStream out = new ByteArrayOutputStream(8192);
                try
                {
                    XMLPrettyPrinter.prettify(_returnEnv, out);
                    return out.toString();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

        }
        catch(org.apache.axis2.AxisFault f)
        {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null)
            {
                if (faultExceptionNameMap.containsKey(faultElt.getQName()))
                {
                    // make the fault by reflection
                    try
                    {
                        java.lang.String exceptionClassName =
                                (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        // message class
                        java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new java.lang.Class[] {messageClass});
                        m.invoke(ex, new java.lang.Object[] {messageObject});

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch(java.lang.ClassCastException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.ClassNotFoundException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.NoSuchMethodException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.reflect.InvocationTargetException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.IllegalAccessException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.InstantiationException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                }
                else
                {
                    throw f;
                }
            }
            else
            {
                throw f;
            }
        }
        finally
        {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env)
    {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while(namespaceIterator.hasNext())
        {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private javax.xml.namespace.QName[] opNameArray = null;

    private boolean optimizeContent(javax.xml.namespace.QName opName)
    {

        if (opNameArray == null)
        {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++)
        {
            if (opName.equals(opNameArray[i]))
            {
                return true;
            }
        }
        return false;
    }

    // https://synch.nis1.national.ncrs.nhs.uk/syncservice-pds/pds_1_6
    private static final org.jibx.runtime.IBindingFactory bindingFactory;

    private static final String bindingErrorMessage;

    private static final int[] bindingNamespaceIndexes;

    private static final String[] bindingNamespacePrefixes;

    static
    {
        org.jibx.runtime.IBindingFactory factory = null;
        String message = null;
        try
        {

            factory =
                    org.jibx.runtime.BindingDirectory
                        .getFactory(RetrievalQuery.class);

            message = null;
        }
        catch(Exception e)
        {
            message = e.getMessage();
        }
        bindingFactory = factory;
        bindingErrorMessage = message;

        int[] indexes = null;
        String[] prefixes = null;
        if (factory != null)
        {

            // check for xsi namespace included
            String[] nsuris = factory.getNamespaces();
            int xsiindex = nsuris.length;
            while( --xsiindex >= 0 && !"http://www.w3.org/2001/XMLSchema-instance".equals(nsuris[xsiindex]))
                ;

            // get actual size of index and prefix arrays to be allocated
            int nscount = 0;
            int usecount = nscount;
            if (xsiindex >= 0)
            {
                usecount++;
            }

            // allocate and initialize the arrays
            indexes = new int[usecount];
            prefixes = new String[usecount];

            if (xsiindex >= 0)
            {
                indexes[nscount] = xsiindex;
                prefixes[nscount] = "xsi";
            }

        }
        bindingNamespaceIndexes = indexes;
        bindingNamespacePrefixes = prefixes;
    }

    private static int nsIndex(String uri, String[] uris)
    {
        for (int i = 0; i < uris.length; i++)
        {
            if (uri.equals(uris[i]))
            {
                return i;
            }
        }
        throw new IllegalArgumentException("Namespace " + uri + " not found in binding directory information");
    }

    private static void addMappingNamespaces(org.apache.axiom.soap.SOAPFactory factory,
            org.apache.axiom.om.OMElement wrapper,
            String nsuri,
            String nspref)
    {
        String[] nss = bindingFactory.getNamespaces();
        for (int i = 0; i < bindingNamespaceIndexes.length; i++)
        {
            int index = bindingNamespaceIndexes[i];
            String uri = nss[index];
            String prefix = bindingNamespacePrefixes[i];
            if (!nsuri.equals(uri) || !nspref.equals(prefix))
            {
                wrapper.declareNamespace(factory.createOMNamespace(uri, prefix));
            }
        }
    }

    private static org.jibx.runtime.impl.UnmarshallingContext getNewUnmarshalContext(org.apache.axiom.om.OMElement param)
            throws org.jibx.runtime.JiBXException
    {
        if (bindingFactory == null)
        {
            throw new RuntimeException(bindingErrorMessage);
        }
        org.jibx.runtime.impl.UnmarshallingContext ctx =
                (org.jibx.runtime.impl.UnmarshallingContext) bindingFactory.createUnmarshallingContext();
        org.jibx.runtime.IXMLReader reader =
                new org.jibx.runtime.impl.StAXReaderWrapper(param.getXMLStreamReaderWithoutCaching(), "SOAP-message", true);
        ctx.setDocument(reader);
        ctx.toTag();
        return ctx;
    }

    private org.apache.axiom.om.OMElement mappedChild(Object value, org.apache.axiom.om.OMFactory factory)
    {
        org.jibx.runtime.IMarshallable mrshable = (org.jibx.runtime.IMarshallable) value;
        org.apache.axiom.om.OMDataSource src = new org.apache.axis2.jibx.JiBXDataSource(mrshable, bindingFactory);
        int index = bindingFactory.getClassIndexMap().get(mrshable.JiBX_getName());
        org.apache.axiom.om.OMNamespace appns = factory.createOMNamespace(bindingFactory.getElementNamespaces()[index], "");
        return factory.createOMElement(src, bindingFactory.getElementNames()[index], appns);
    }

    private static Object fromOM(org.apache.axiom.om.OMElement param, Class type, java.util.Map extraNamespaces)
            throws org.apache.axis2.AxisFault
    {
        try
        {
            org.jibx.runtime.impl.UnmarshallingContext ctx = getNewUnmarshalContext(param);
            // TODO: manually instantiate (unless there's a way for guice to do it) the unmarshaller
            return ctx.unmarshalElement(type);
        }
        catch(Exception e)
        {
            throw new org.apache.axis2.AxisFault(e.getMessage());
        }
    }

    /**
     * get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory)
    {
        return factory.getDefaultEnvelope();
    }

    private org.apache.axiom.om.OMElement toOM(RetrievalQuery param,
            org.apache.axiom.soap.SOAPFactory factory,
            boolean optimizeContent)
    {

        if (param instanceof org.jibx.runtime.IMarshallable)
        {
            if (bindingFactory == null)
            {
                throw new RuntimeException(bindingErrorMessage);
            }
            return(mappedChild(param, factory));
        }
        else if (param == null)
        {
            throw new RuntimeException(
                    "Cannot bind null value of type com.cerner.spine.interfaces.pds.binding.RetrievalQueryMessage");
        }
        else
        {
            throw new RuntimeException(
                    "No JiBX <mapping> defined for class com.cerner.spine.interfaces.pds.binding.RetrievalQueryMessage");
        }

    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            RetrievalQuery param,
            boolean optimizeContent)
    {
        org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
        if (param != null)
        {
            envelope.getBody().addChild(toOM(param, factory, optimizeContent));
        }
        return envelope;
    }

    private Exception convertException(Exception ex) throws java.rmi.RemoteException
    {
        if (ex instanceof org.apache.axis2.AxisFault)
        {
            org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) ex;
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null)
            {
                if (faultExceptionNameMap.containsKey(faultElt.getQName()))
                {
                    try
                    {

                        // first create the actual exception
                        String exceptionClassName = (String) faultExceptionClassNameMap.get(faultElt.getQName());
                        Class exceptionClass = Class.forName(exceptionClassName);
                        Exception e = (Exception) exceptionClass.newInstance();

                        // build the message object from the details
                        String messageClassName = (String) faultMessageMap.get(faultElt.getQName());
                        Class messageClass = Class.forName(messageClassName);
                        Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage", new Class[] {messageClass});
                        m.invoke(e, new Object[] {messageObject});
                        return e;

                    }
                    catch(ClassCastException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(ClassNotFoundException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(NoSuchMethodException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(java.lang.reflect.InvocationTargetException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(IllegalAccessException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                    catch(InstantiationException e)
                    {
                        // we cannot intantiate the class - throw the original
                        // Axis fault
                        throw f;
                    }
                }
                else
                {
                    throw f;
                }
            }
            else
            {
                throw f;
            }

        }
        else if (ex instanceof RuntimeException)
        {
            throw (RuntimeException) ex;
        }
        else if (ex instanceof java.rmi.RemoteException)
        {
            throw (java.rmi.RemoteException) ex;
        }
        else
        {
            throw org.apache.axis2.AxisFault.makeFault(ex);
        }
    }
}
