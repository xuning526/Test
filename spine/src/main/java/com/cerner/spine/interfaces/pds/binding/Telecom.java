package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.FSICodeSetHelper;
import com.cerner.domain.patient.PatientPhone;
import com.cerner.presentation.ebs.dateformat.DateFormatHelper;
import com.cerner.system.i18n.util.DateFormatter;
import com.cerner.system.i18n.util.ResourceAssistant;
import com.wellogic.dbone.model.Code;

/*
 * File - Telecom.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2009 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Telecom
{
    private String use;
    private String value;
    private TimeInterval usablePeriod;
    private InstanceIdentifier pdsId;
    
    public InstanceIdentifier getPdsId()
    {
        return pdsId;
    }
    public void setPdsId(InstanceIdentifier pdsId)
    {
        this.pdsId = pdsId;
    }
    public TimeInterval getUsablePeriod()
    {
        return usablePeriod;
    }
    public void setUsablePeriod(TimeInterval usablePeriod)
    {
        this.usablePeriod = usablePeriod;
    }
    public String getUse()
    {
        return use;
    }
    public void setUse(String use)
    {
        this.use = use;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public PatientPhone toPatientPhone()
    {
        // use
        long phoneTypeCodeId = 0;
        if (use != null && use.length() > 0)
        {
            Code phoneUseCd = FSICodeSetHelper.findCodeByInboundAlias("43", use);
            if (phoneUseCd != null)
            {
                // PDS' telecom use is Cerner's phone type code
                phoneTypeCodeId = Long.parseLong(phoneUseCd.getId());
            }
        }
        
        // PDS object identifier
        PatientPhone phone = new PatientPhone(0, phoneTypeCodeId);
        
        phone.setSourceIdentifier(pdsId.getExtension());
        // type and number
        if (value != null && value.length() > 0)
        {
            // a colon is used to separate the telecom's type from its value
            int i = value.indexOf(':');
            if (i > -1)
            {
                // TODO: don't need to include the colon once our code value aliases are updated
                String phoneType = value.substring(0, i+1);
                Code phoneTypeCd = FSICodeSetHelper.findCodeByInboundAlias("23056", phoneType);
                if (phoneTypeCd != null)
                {
                    // PDS' telecom type is Cerner's phone contact method
                    phone.setContactMethod(Long.valueOf(phoneTypeCd.getId()), "do you really need the display?");
                }
                
                if (i < value.length())
                {
                    // everything after the colon is considered the phone number
                    String phoneNumber = value.substring(i+1, value.length());
                    phone.setPhoneNumber(phoneNumber);
                }
            }
            else
            {
                // there is no colon, so assume the entire value is the phone number
                phone.setPhoneNumber(value);
            }
        }
        
        
        // usable period
        if(usablePeriod != null)
        {
            Date[] dates = usablePeriod.toDates();
            phone.setBegEffectiveDateTime(dates[0]);
            phone.setEndEffectiveDateTime(dates[1]);
        }
        
        return phone;
    }
    
    public Telecom fromPatientPhone (PatientPhone patPhone)
    {
    	com.cerner.spine.interfaces.pds.binding.Telecom retPhone = new com.cerner.spine.interfaces.pds.binding.Telecom();	        
        //From mim
        //In addition, for each telecommunication number, one occurrence of the use attribute is required to indicate 
        //the context for the telecommunication number, using the following values:
        //H - A communication address at a home; 
        //HP - The primary home, to reach a person after business hours; 
        //HV - A vacation home, to reach a person while on vacation; 
        //WP - An office address; 
        //AS - An automated answering machine; 
        //EC - A contact specifically designated to be used for emergencies; 
        //PG - A paging device suitable to solicit a callback or to leave a very short message; 
        //MC - A telecommunication device that moves and stays with its owner  
        if (patPhone.getPhoneTypeCodeId()>0)
        {
        	retPhone.setUse(FSICodeSetHelper.findOutboundAliasByCode(CodeSetHelper.findCodeById(String.valueOf(patPhone.getPhoneTypeCodeId()))));
        }     
        //From mim
        //To provide zero or more telecommunication numbers of the patient, together with the contact method for each. 
        //These two items of information are carried in the value attribute, separated by a colon character. 
        //The contact method will be one of the following values:
        //tel (for telephone numbers); 
        //fax (for fax numbers); 
        //mailto (for e-mail addresses); 
        //textphone (for minicom or textphone numbers).
        StringBuilder valueStringBuilder = new StringBuilder();
        if (patPhone.getContactMethodCodeId()>0)
        {
     	   valueStringBuilder.append(FSICodeSetHelper.findOutboundAliasByCode(CodeSetHelper.findCodeById(String.valueOf(patPhone.getContactMethodCodeId()))));
        }
        if (patPhone.getPhoneNumber()!=null)
        {
     	   valueStringBuilder.append(patPhone.getPhoneNumber());
        }
        retPhone.setValue(valueStringBuilder.toString());       
        //In addition, the id element is required to be present to provide an identifier for the telecommmunication details. 
        //This identifier is allocated by the PDS and needs to be included in any PDS General Update message where these 
        //telecommmunication details are to be altered or deleted. The Identifier External datatype flavour is used to carry 
        //this information as follows:
        //The root attribute will contain an OID with the value "2.16.840.1.113883.2.1.3.2.4.18.1"; 
        //The extension attribute will contain the PDS Allocated Object Identifier itself.
        String sourceId = patPhone.getSourceIdentifier();
        if (sourceId != null && sourceId.length() > 0)
        {
            retPhone.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", sourceId));
        }
        else
        {
            retPhone.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", "Ph" + Long.toString(Calendar.getInstance().getTimeInMillis())));
        }
        TimeInterval usablePeriod = new TimeInterval();
        DateFormat dateFormat = DateFormatter.getDateFormat("yyyyMMdd");
        Date begEffectiveDateTime = patPhone.getBegEffectiveDateTime();
        usablePeriod.setLow(begEffectiveDateTime != null ? dateFormat.format(begEffectiveDateTime) : "0");
        Date endEffectiveDateTime = patPhone.getEndEffectiveDateTime();
        usablePeriod.setHigh(endEffectiveDateTime != null ? dateFormat.format(endEffectiveDateTime) : "1");
        usablePeriod.setCenter("0");
        usablePeriod.setUnit("days");
        usablePeriod.setWidth("5");
        retPhone.setUsablePeriod(usablePeriod);
		return retPhone;
     }  
}
