package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.cerner.system.i18n.util.DateFormatter;

/**
 * <p></p>
 * <p>Copyright 2008 Cerner Corporation</p>
 * @author Sean Parmelee
 */
public class RetrievalQuery
{
    private static final DateFormat df = DateFormatter.getDateFormat("yyyyMMddHHmmss"); 
    private final InstanceIdentifier id = new InstanceIdentifier(UUID.randomUUID().toString().toUpperCase());
    private final String creationTime = df.format(new Date());
    private final CodedValue versionCode = new CodedValue("V3NPfIT4.2.00");
    private final InstanceIdentifier interactionId = new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.12", "QUPA_IN040000UK32");
    private final CodedValue processingCode = new CodedValue("P");
    private final CodedValue processingMoodCode = new CodedValue("T");
    private final Device receivingSystem;
    private final Device sendingSystem;
    private Author author;
    private final AuthorSystem[] authorSystems;
    private final CodedValue historicDataIndicator = new CodedValue("0");
    private final InstanceIdentifier personId;
    private ArrayList<String> retrievalItems = new ArrayList<String>();
    
    public RetrievalQuery()
    {
        authorSystems = null;
        receivingSystem = null;
        sendingSystem = null;
        personId = null;
    }
    
    public RetrievalQuery(String nhsNumber, String receivingSystemASID, String sendingSystemASID)
    {
        personId = new InstanceIdentifier("2.16.840.1.113883.2.1.4.1", nhsNumber);
        receivingSystem = new Device(new InstanceIdentifier("1.2.826.0.1285.0.2.0.107", receivingSystemASID));
        sendingSystem = new Device(new InstanceIdentifier("1.2.826.0.1285.0.2.0.107", sendingSystemASID));
        authorSystems = new AuthorSystem[]{new AuthorSystem(new InstanceIdentifier("1.2.826.0.1285.0.2.0.107", sendingSystemASID))};
    }
    
    public InstanceIdentifier getId()
    {
        return id;
    }
    public String getCreationTime()
    {
        return creationTime;
    }
    public CodedValue getVersionCode()
    {
        return versionCode;
    }
    public InstanceIdentifier getInteractionId()
    {
        return interactionId;
    }
    public CodedValue getProcessingCode()
    {
        return processingCode;
    }
    public CodedValue getProcessingMoodCode()
    {
        return processingMoodCode;
    }
    public Device getReceivingSystem()
    {
        return receivingSystem;
    }
    public Device getSendingSystem()
    {
        return sendingSystem;
    }
    public Author getAuthor()
    {
        return author;
    }
    public void setAuthor(Author author)
    {
        this.author = author;
    }

    public AuthorSystem[] getAuthorSystems()
    {
        return authorSystems;
    }
    public CodedValue getHistoricDataIndicator()
    {
        return historicDataIndicator;
    }

    public InstanceIdentifier getPersonId()
    {
        return personId;
    }

    public List<String> getRetrievalItems()
    {
        return retrievalItems;
    }
}