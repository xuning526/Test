package com.cerner.spine.interfaces.pds.binding;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.FSICodeSetHelper;
import com.cerner.domain.patient.PatientAddress;
import com.cerner.system.i18n.util.DateFormatter;
import com.wellogic.dbone.model.Code;


/*
 * File - Address.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2009 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Address
{
    private String use;
    private String[] streetAddressLines;
    private String postalCode;
    private String addressKey;
    private String description;
    private TimeInterval usablePeriod;
    private InstanceIdentifier pdsId;
    
    public String getAddressKey()
    {
        return addressKey;
    }
    public void setAddressKey(String addressKey)
    {
        this.addressKey = addressKey;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String desc)
    {
        this.description = desc;
    }
    public InstanceIdentifier getPdsId()
    {
        return pdsId;
    }
    public void setPdsId(InstanceIdentifier pdsId)
    {
        this.pdsId = pdsId;
    }
    public String getPostalCode()
    {
        return postalCode;
    }
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }
    public String[] getStreetAddressLines()
    {
        return streetAddressLines;
    }
    public void setStreetAddressLines(String[] streetAddressLines)
    {
        this.streetAddressLines = streetAddressLines;
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
    
    public PatientAddress toPatientAddress()
    {
        // use
        long addressTypeCodeId = 0;
        if(use != null && use.length() > 0)
        {
            Code useCd = FSICodeSetHelper.findCodeByInboundAlias("212", use);
            if(useCd != null)
            {
                addressTypeCodeId = Long.parseLong(useCd.getId());
            }
        }
        
        PatientAddress address = new PatientAddress(0, addressTypeCodeId);
                
        address.setSourceIdentifier(pdsId.getExtension());
        // address lines
        if (streetAddressLines != null && streetAddressLines.length > 0)
        {
            for(int i = 0; i < streetAddressLines.length; i++)
            {
                if(streetAddressLines[i] != null && streetAddressLines[i].length() > 0)
                {
                    switch(i)
                    {
                        case 0:
                            address.setStreetAddr(streetAddressLines[i]);
                            break;
                        case 1:
                            address.setStreetAddr2(streetAddressLines[i]);
                            break;
                        case 2:
                            address.setStreetAddr3(streetAddressLines[i]);
                            break;
                        case 3:
                            address.setCity(streetAddressLines[i]);
                            break;
                        case 4:
                            address.setCounty(streetAddressLines[i]);
                            break;
                    }
                }
            }
        }
        
        // post code
        if(postalCode != null)
        {
            String poastalCodeFormatted = postalCode.trim().toUpperCase();
            if(poastalCodeFormatted.indexOf(' ') == -1 && poastalCodeFormatted.length() >= 5 && poastalCodeFormatted.length() <= 7
                    && poastalCodeFormatted.matches("[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][A-Z-[CIKMOV]]{2}"))
            {
                StringBuilder builder = new StringBuilder(poastalCodeFormatted);
                builder.insert(poastalCodeFormatted.length() - 3, ' ');
                poastalCodeFormatted = builder.toString();
            }
            address.setZipcode(poastalCodeFormatted);
        }
        
        if(usablePeriod != null)
        {
            Date[] dates = usablePeriod.toDates();
            address.setBegEffectiveDateTime(dates[TimeInterval.LOW]);
            address.setEndEffectiveDateTime(dates[TimeInterval.HIGH]);
        }
        
        return address;
    }
    
    public Address fromPatientAddress (PatientAddress patAddress)
    {
        /* 
        StreetAddressLines: From documentation:
        Address Line 1: premises ID, house name, e.g. ‘Flat 1’, ‘The Old Schoolhouse’
        Address Line 2: house number, dependent thoroughfare name and descriptor without commas, e.g. ’23 Mill Lane’
        Address Line 3: dependent locality, e.g. ‘Boxgrove’
        Address Line 4: post town, e.g. ‘Leeds’
        Address Line 5: county (if present), e.g. ‘Hampshire’, ‘Hants’
        */                              
        Address returnAdd = new Address();                 
        String [] retStreetAddress = new String [5];                                                         
        if (patAddress.getStreetAddr()!=null)
        {
               retStreetAddress [0] = patAddress.getStreetAddr();                       
        }                                                     
        if (patAddress.getStreetAddr2()!=null)
        {
               retStreetAddress [1] = patAddress.getStreetAddr2();
        }
        if (patAddress.getStreetAddr3()!=null)
        {
               retStreetAddress [2] = patAddress.getStreetAddr3();
        }
        if (patAddress.getCity()!=null)
        {
              retStreetAddress [3] = patAddress.getCity();   
        }  
        if (patAddress.getCounty()!=null)
        {
              retStreetAddress [4] = patAddress.getCounty();
        }                                                        
        returnAdd.setStreetAddressLines(retStreetAddress);
        //Postal Code
        if (patAddress.getZipcode() != null)
        {
              returnAdd.setPostalCode(patAddress.getZipcode());
        } 
        /*
        From mim specification:The type of address is required to be identified by using one of the following codes in the use attribute:
        H - usual address
        TMP - temporary address
        PST - correspondence address.
        */
        returnAdd.setUse(FSICodeSetHelper.findOutboundAliasByCode(CodeSetHelper.findCodeById(String.valueOf(patAddress.getAddressTypeCodeId()))));           
        // from mim: the id element is required to be present to provide an identifier for the address
        //The root attribute will contain an OID with the value "2.16.840.1.113883.2.1.3.2.4.18.1";               
        String sourceId = patAddress.getSourceIdentifier();
        if (sourceId != null && sourceId.length() > 0)
        {
            returnAdd.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", sourceId));
        }
        else
        {
            returnAdd.setPdsId(new InstanceIdentifier("2.16.840.1.113883.2.1.3.2.4.18.1", "Adr" + Long.toString(Calendar.getInstance().getTimeInMillis())));
        }

        TimeInterval usablePeriod = new TimeInterval();
        DateFormat dateFormat = DateFormatter.getDateFormat("yyyyMMdd");
        Date begEffectiveDateTime = patAddress.getBegEffectiveDateTime();
        usablePeriod.setLow(begEffectiveDateTime != null ? dateFormat.format(begEffectiveDateTime) : "0");            
        Date endEffectiveDateTime = patAddress.getEndEffectiveDateTime();
        usablePeriod.setHigh(endEffectiveDateTime != null ? dateFormat.format(endEffectiveDateTime) : "1");
        usablePeriod.setCenter("0");
        usablePeriod.setUnit("days");
        usablePeriod.setWidth("5");
        returnAdd.setUsablePeriod(usablePeriod);
        return returnAdd; 
     }            
}

