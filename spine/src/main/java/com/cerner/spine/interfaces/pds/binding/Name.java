package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.FSICodeSetHelper;
import com.cerner.domain.patient.PatientName;
import com.cerner.system.i18n.util.DateFormatter;
import com.wellogic.dbone.model.Code;

/*
 * File - Name.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2009 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Name
{
    private String use;
    private String familyName;
    private String[] givenNames;
    private String prefix;
    private String suffix;
    private TimeInterval validTime;
    private InstanceIdentifier pdsId;
    
    public String getFamilyName()
    {
        return familyName;
    }
    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }
    public String[] getGivenNames()
    {
        return givenNames;
    }
    public void setGivenNames(String[] givenNames)
    {
        this.givenNames = givenNames;
    }
    public InstanceIdentifier getPdsId()
    {
        return pdsId;
    }
    public void setPdsId(InstanceIdentifier pdsId)
    {
        this.pdsId = pdsId;
    }
    public String getPrefix()
    {
        return prefix;
    }
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    public String getSuffix()
    {
        return suffix;
    }
    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }
    public String getUse()
    {
        return use;
    }
    public void setUse(String use)
    {
        this.use = use;
    }
    public TimeInterval getValidTime()
    {
        return validTime;
    }
    public void setValidTime(TimeInterval validTime)
    {
        this.validTime = validTime;
    }
    
    public PatientName toPatientName()
    {
        // use
        long nameTypeCodeId = 0;
        if (use != null && use.length() > 0)
        {
            Code useCd = FSICodeSetHelper.findCodeByInboundAlias("213", use);
            if(useCd != null)
            {
                nameTypeCodeId = Long.parseLong(useCd.getId());
            }
        }
        
        PatientName name = new PatientName(0, nameTypeCodeId);
        
        name.setSourceIdentifier(pdsId.getExtension());
        // last name
        if (familyName != null && familyName.length() > 0)
        {
            name.setLastName(familyName);
        }
        
        if (givenNames != null && givenNames.length > 0)
        {
            // the first given name is the patient's first name
            if (givenNames[0].length() > 0)
            {
                name.setFirstName(givenNames[0]);
            }
            
            // the second given name is the patient's middle name
            if (givenNames.length == 2 && givenNames[1].length() > 0)
            {
                name.setMiddleName(givenNames[1]);
            }
        }
        
        // prefix
        if (prefix != null && prefix.length() > 0)
        {
            name.setPrefix(prefix);
        }
        
        // suffix
        if (suffix != null && suffix.length() > 0)
        {
            name.setSuffix(suffix);
        }
        
        // effective dates
        if(validTime != null)
        {
            Date[] dates = validTime.toDates();
            name.setBegEffectiveDateTime(dates[TimeInterval.LOW]);
            name.setEndEffectiveDateTime(dates[TimeInterval.HIGH]);
        }
        
        return name;
    }
    
    public Name fromPatientName (PatientName patName)
    {
    	Name retName = new Name();	
        //From Mim:
        //The following sub-elements are used to carry components of a structured name:
        //A single instance of the prefix element is used to carry a name title, for example, Mr, Mrs, Ms etc; 
        //One or more instances of the given element are used to carry given names (or forenames) or initials, 
        //with the order in which they are carried in the message being significant, i.e. the first occurrence 
        //of the given element is used to carry the first given name, the second occurrence of the given element is 
        //used to carry the second given name etc; 
        //A single instance of the family element is used to carry the family name (or surname); 
        //A single instance of the suffix element is used to carry a name prefix, for example, Jnr, Snr etc.                                                                  
    	retName.setPrefix(patName.getPrefix());
        String[] arrGivenNames = new String [2];
        if (patName.getFirstName()!=null)
        {
        	arrGivenNames[0] = patName.getFirstName();
        }
        if (patName.getMiddleName()!=null)
        {	                
        	arrGivenNames[1] = patName.getMiddleName();
        }
        retName.setGivenNames(arrGivenNames);
        if (patName.getLastName()!=null)
        {
        	retName.setFamilyName(patName.getLastName());
        }
        if (getSuffix()!=null)
        {
        	retName.setSuffix(getSuffix());
        }     
        //From Mim
        //HL7 standard name types supported by NPfIT:
        //"L" - Usual (current) name 
        //"A" - Alias name   
        if (patName.getNameTypeCodeId()>0)
        {
        	retName.setUse(FSICodeSetHelper.findOutboundAliasByCode(CodeSetHelper.findCodeById(String.valueOf(patName.getNameTypeCodeId())))); 
        }
        // From mim
        //The root attribute will contain an OID with the value "2.16.840.1.113883.2.1.3.2.4.18.1"; 
        //The extension attribute will contain the PDS Allocated Object Identifier itself.
        //For the usual and previous name types, the validTime sub-element may be used to indicate dates 
        String sourceId = patName.getSourceIdentifier();
        if ( sourceId != null && sourceId.length() > 0)
        {
            retName.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", sourceId)); 
        }
        else
        {
            retName.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", "Name" + Long.toString(Calendar.getInstance().getTimeInMillis())));
        }

        TimeInterval usablePeriod = new TimeInterval();
        DateFormat dateFormat = DateFormatter.getDateFormat("yyyyMMdd");
        Date begEffectiveDateTime = patName.getBegEffectiveDateTime();
        usablePeriod.setLow(begEffectiveDateTime != null ? dateFormat.format(begEffectiveDateTime) : "0");
        Date endEffectiveDateTime = patName.getEndEffectiveDateTime();
        usablePeriod.setHigh(endEffectiveDateTime != null ? dateFormat.format(endEffectiveDateTime) : "1");
        usablePeriod.setCenter("0");
        usablePeriod.setUnit("days");
        usablePeriod.setWidth("5");
        retName.setValidTime(usablePeriod);
		return retName;   	
    }
}
