package com.cerner.spine.interfaces.pds.binding;

import static com.cerner.common.injector.DependencyInjector.getInjector;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cerner.cab.legacy.globalid.GlobalIdTranslator;
import com.cerner.common.injector.DependencyInjector;
import com.cerner.data.dao.AliasPoolSet;
import com.cerner.data.dao.impl.millennium.aliaspool.AliasPoolDAOImpl;
import com.cerner.data.model.AliasPool;
import com.cerner.data.util.CodeSetHelper;
import com.cerner.data.util.DAOHelper;
import com.cerner.data.util.FSICodeSetHelper;
import com.cerner.domain.organization.Organization;
import com.cerner.domain.organization.OrganizationRepository;
import com.cerner.domain.patient.PatientAddress;
import com.cerner.domain.patient.PatientAlias;
import com.cerner.domain.patient.PatientPhone;
import com.cerner.domain.patient.UpdatePatient;
import com.cerner.domain.patient.Patient.DatePrecision;
import com.cerner.domain.patient.delegates.RetrieveOrganizationIdByAlias;
import com.cerner.domain.patient.delegates.RetrieveRegisteredGPIdByAlias;
import com.cerner.system.i18n.util.DateFormatter;
import com.wellogic.dbone.DBOneFactoryManager;
import com.wellogic.dbone.dao.ProviderDAO;
import com.wellogic.dbone.datatypes.IdentifierInformation;
import com.wellogic.dbone.model.Code;
import com.wellogic.dbone.model.Provider;

/*
 * File - Patient.java
 * Created Dec 1, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2008 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class Patient
{
    private InstanceIdentifier id;
    private Address[] addresses;
    private Telecom[] telecoms;
    private CodedValue confidentialityCode;
    private Name[] names;
    private CodedValue gender;
    private String birthTime;
    private String deceasedTime;
    private Integer birthOrder;
    private PrimaryCareProvider primaryCareProvider;
    private CodedValue languageCode;
    private CodedValue interpreterRequired;
    private String password;
    private CodedValue preferredContactMethod;
    private Consent[] consents;
    private CodedValue deathNotification;
    private CodedValue writtenCommunicationFormat;
    private SupercededId[] supercededIds;

    public Address[] getAddresses()
    {
        return addresses;
    }

    public void setAddresses(Address[] addresses)
    {
        this.addresses = addresses;
    }

    public CodedValue getGender()
    {
        return gender;
    }

    public void setGender(CodedValue administrativeGenderCode)
    {
        this.gender = administrativeGenderCode;
    }

    public String getBirthTime()
    {
        return birthTime;
    }

    public void setBirthTime(String birthTime)
    {
        this.birthTime = birthTime;
    }

    public CodedValue getConfidentialityCode()
    {
        return confidentialityCode;
    }

    public void setConfidentialityCode(CodedValue confidentialityCode)
    {
        this.confidentialityCode = confidentialityCode;
    }

    public Consent[] getConsents()
    {
        return consents;
    }

    public void setConsents(Consent[] consents)
    {
        this.consents = consents;
    }

    public CodedValue getDeathNotification()
    {
        return deathNotification;
    }

    public void setDeathNotification(CodedValue deathNotification)
    {
        this.deathNotification = deathNotification;
    }

    public String getDeceasedTime()
    {
        return deceasedTime;
    }

    public void setDeceasedTime(String deceasedTime)
    {
        this.deceasedTime = deceasedTime;
    }

    public InstanceIdentifier getId()
    {
        return id;
    }

    public void setId(InstanceIdentifier id)
    {
        this.id = id;
    }

    public CodedValue getInterpreterRequired()
    {
        return interpreterRequired;
    }

    public void setInterpreterRequired(CodedValue interpreterRequired)
    {
        this.interpreterRequired = interpreterRequired;
    }

    public CodedValue getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(CodedValue languageCode)
    {
        this.languageCode = languageCode;
    }

    public Integer getBirthOrder()
    {
        return birthOrder;
    }

    public void setBirthOrder(Integer multipleBirthOrderNumber)
    {
        this.birthOrder = multipleBirthOrderNumber;
    }

    public Name[] getNames()
    {
        return names;
    }

    public void setNames(Name[] names)
    {
        this.names = names;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public CodedValue getPreferredContactMethod()
    {
        return preferredContactMethod;
    }

    public void setPreferredContactMethod(CodedValue preferredContactMethod)
    {
        this.preferredContactMethod = preferredContactMethod;
    }

    public PrimaryCareProvider getPrimaryCareProvider()
    {
        return primaryCareProvider;
    }

    public void setPrimaryCareProvider(PrimaryCareProvider primaryCareProvider)
    {
        this.primaryCareProvider = primaryCareProvider;
    }

    public SupercededId[] getSupercededIds()
    {
        return supercededIds;
    }

    public void setSupercededIds(SupercededId[] supercededIds)
    {
        this.supercededIds = supercededIds;
    }

    public Telecom[] getTelecoms()
    {
        return telecoms;
    }

    public void setTelecoms(Telecom[] telecoms)
    {
        this.telecoms = telecoms;
    }

    public CodedValue getWrittenCommunicationFormat()
    {
        return writtenCommunicationFormat;
    }

    public void setWrittenCommunicationFormat(CodedValue writtenCommunicationFormat)
    {
        this.writtenCommunicationFormat = writtenCommunicationFormat;
    }

    public UpdatePatient toUpdatePatient()
    {
        UpdatePatient patient = new UpdatePatient();
        patient.setValid(true);
        
        // NHS numbers
        patient.setCurrentNHSNumber(id.getExtension());
        PatientAlias currentAlias = new PatientAlias(0, Long.parseLong(CodeSetHelper.findCodeByTitle("4", "SSN").getId()), 0);
        currentAlias.setAlias(id.getExtension());
        patient.addAlias(currentAlias);
        
        if (supercededIds != null)
        {
            for (SupercededId supercededId : supercededIds)
            {
                if(supercededId.getId().getExtension().matches("[0-9]{10}"))
                {
                    patient.addAlias(supercededId.toPatientAlias());
                }
            }
        }

        // Addresses
        if (addresses != null)
        {
            for (Address address : addresses)
            {
                // Choose and Book only cares about the usual address
                // the PDS is constrained to only carry one current usual address
                if ("H".equals(address.getUse()) && isValidDateRange(address.getUsablePeriod()))
                {
                    patient.addAddress(address.toPatientAddress(), true);
                    break;
                }
            }
        }
         
        // Phones
        if (telecoms != null)
        {
            for (Telecom telecom : telecoms)
            {
                // all phones which are current, as specified by begin and end effective dates,
                // should be added (even ones with invalid types)
                if (isValidDateRange(telecom.getUsablePeriod()))
                {
                    patient.addPhone(telecom.toPatientPhone());                    
                }
            }
        }
         
        // Names
        if (names != null)
        {
            for (Name name : names)
            {
                // Choose and Book only cares about the usual name
                // the PDS is constrained to only carry one current usual name
                if ("L".equals(name.getUse()) && isValidDateRange(name.getValidTime()))
                {
                    patient.addName(name.toPatientName(), true);
                    break;
                }
            }
        }
         
        if (gender != null)
        {
            Code genderCd = FSICodeSetHelper.findCodeByInboundAlias("57", gender.getCode());
            if (genderCd != null)
            {
                patient.setGenderCodeId(Long.parseLong(genderCd.getId()));
            }
        }

        // confidentiality code
        if (confidentialityCode != null)
        {
            Code confidentialityCd = FSICodeSetHelper.findCodeByInboundAlias("87", confidentialityCode.getCode());
            if (confidentialityCd != null && confidentialityCd.getTitle().equals("SENSITIVE"))
            {
                patient.setSensitive(true);
            }
        }

        // date of birth
        // TODO: compare current method vs using DateFormats to see which is more efficient
        if (birthTime != null && birthTime.length() >= 4)
        {
            int dobLength = birthTime.length();
            Calendar cal = Calendar.getInstance();
            cal.clear();

            // start of by assuming year precision (YYYY)
            cal.set(Calendar.YEAR, Integer.parseInt(birthTime.substring(0, 4)));
            patient.setBirthDatePrecision(DatePrecision.YEAR);

            // check if the month is available
            if (dobLength >= 6)
            {
                // YYYYMM
                cal.set(Calendar.MONTH, Integer.parseInt(birthTime.substring(4, 6)) - 1);
                patient.setBirthDatePrecision(DatePrecision.MONTH);
            }

            // check if the day is available
            if (dobLength >= 8)
            {
                // YYYYMMDD
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(birthTime.substring(6, 8)));
                patient.setBirthDatePrecision(DatePrecision.DAY);
            }

            // check if the time if available
            if (dobLength == 12)
            {
                // YYYYMMDDHHmm
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(birthTime.substring(8, 10)));
                cal.set(Calendar.MINUTE, Integer.parseInt(birthTime.substring(10, 12)));
                patient.setBirthDatePrecision(DatePrecision.MINUTE);
            }
            patient.setBirthDateTime(cal.getTime());
        }

        // deceased time
        if (deceasedTime != null && deceasedTime.length() >= 4)
        {
            int dodLength = deceasedTime.length();
            Calendar cal = Calendar.getInstance();
            cal.clear();

            // start of by assuming year precision (YYYY)
            cal.set(Calendar.YEAR, Integer.parseInt(deceasedTime.substring(0, 4)));
            patient.setDeceasedDatePrecision(DatePrecision.YEAR);

            // check if the month is available
            if (dodLength >= 6)
            {
                // YYYYMM
                cal.set(Calendar.MONTH, Integer.parseInt(deceasedTime.substring(4, 6)) - 1);
                patient.setDeceasedDatePrecision(DatePrecision.MONTH);
            }

            // check if the day is available
            if (dodLength >= 8)
            {
                // YYYYMMDD
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deceasedTime.substring(6, 8)));
                patient.setDeceasedDatePrecision(DatePrecision.DAY);
            }

            // check if the time if available
            if (dodLength == 12)
            {
                // YYYYMMDDHHmm
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(deceasedTime.substring(8, 10)));
                cal.set(Calendar.MINUTE, Integer.parseInt(deceasedTime.substring(10, 12)));
                patient.setDeceasedDatePrecision(DatePrecision.MINUTE);
            }
            patient.setDeceasedDateTime(cal.getTime());
        }

        // registered gp/practice    
        if (primaryCareProvider != null && primaryCareProvider.getCode().getCode().equals("1"))
        {
            String oid = primaryCareProvider.getId().getRoot();
            String alias = primaryCareProvider.getId().getExtension();
            if (oid.equals("2.16.840.1.113883.2.1.4.2"))
            {
                // registered gp
            	RetrieveRegisteredGPIdByAlias delegate = getInjector().get(RetrieveRegisteredGPIdByAlias.class);           
                AliasPoolSet apDAO = new AliasPoolDAOImpl(DBOneFactoryManager.getFactory()); 
                Code nhsCode = CodeSetHelper.findCodeByTitle("89", "NHS"); 
                Code providerAliasTypeCode =CodeSetHelper.findCodeByTitle("320", "EXTERNALID");         
                AliasPool ap =(AliasPool) apDAO.findAliasPoolByContributorSystem(nhsCode, providerAliasTypeCode).get(0);
                Long gpId = delegate.execute(alias,ap); 
                delegate.execute(alias,ap); 
                patient.setRegisteredGeneralPractitionerId (gpId);
            }
            else if (oid.equals("2.16.840.1.113883.2.1.4.3"))
            {
            	// registered practice
                RetrieveOrganizationIdByAlias delegate = getInjector().get(RetrieveOrganizationIdByAlias.class);           
                AliasPoolSet apDAO = new AliasPoolDAOImpl(DBOneFactoryManager.getFactory()); 
                Code nhsCode = CodeSetHelper.findCodeByTitle("89", "NHS"); 
                Code orgAliasTypeCode =CodeSetHelper.findCodeByTitle("334", "COMPANYCODE");         
                AliasPool ap =(AliasPool) apDAO.findAliasPoolByContributorSystem(nhsCode, orgAliasTypeCode).get(0);
                Long rpoId = delegate.execute(alias, orgAliasTypeCode , ap); 
                patient.setRegisteredPracticeOrganizationId(rpoId);
            }
            if (primaryCareProvider.getEffectiveTime() != null)
            {            	
                Date[] dates = primaryCareProvider.getEffectiveTime().toDates();
                patient.setPrimaryCareProviderBeginEffectiveDateTime(dates[TimeInterval.LOW]);
                patient.setPrimaryCareProviderEndEffectiveDateTime(dates[TimeInterval.HIGH]);
            }
        }

        // preferred language
        if (languageCode != null)
        {
            Code languageCd = FSICodeSetHelper.findCodeByInboundAlias("36", languageCode.getCode());
            if (languageCd != null)
            {
                patient.setPreferredLanguageCodeId(Long.parseLong(languageCd.getId()));
            }
        }

        // interpreter
        if (interpreterRequired != null)
        {
            Code interpreterReqdCd = FSICodeSetHelper.findCodeByInboundAlias("329", interpreterRequired.getCode());
            if (interpreterReqdCd != null && interpreterReqdCd.getTitle().equals("YES"))
            {
                patient.setInterpreterRequired(true);
            }
        }

        // password
        if (password != null && password.length() > 0)
        {
            patient.setEncryptedPassword(password);
        }

        // preferred contact method
        if (preferredContactMethod != null)
        {
            Code preferredContactMethodCd = FSICodeSetHelper.findCodeByInboundAlias("23042", preferredContactMethod.getCode());
            if (preferredContactMethodCd != null)
            {
                patient.setPreferredContactMethodCodeId(Long.parseLong(preferredContactMethodCd.getId()));
            }
        }

        patient.setCallbackConsentInd(2);
        patient.setConsentStatusCodeId(Long.parseLong(CodeSetHelper.findCodeByTitle("29465", "NOTONFILE").getId()));
        if (consents != null)
        {
            for (Consent consent : consents)
            {
                if ("6".equals(consent.getType().getCode()))
                {
                    // callback consent
                    Code consentCd = FSICodeSetHelper.findCodeByInboundAlias("23043", consent.getValue().getCode());
                    if (consentCd != null && consentCd.getTitle().equals("YES"))
                    {
                        patient.setCallbackConsentInd(1);
                    }
                    else if (consentCd != null && consentCd.getTitle().equals("NO"))
                    {
                        patient.setCallbackConsentInd(0);
                    }
                }

                else if ("4".equals(consent.getType().getCode()))
                {
                    // NHS Care Record sharing
                    Code consentCd = FSICodeSetHelper.findCodeByInboundAlias("29465", consent.getValue().getCode());
                    if (consentCd != null)
                    {
                        patient.setConsentStatusCodeId(Long.parseLong(consentCd.getId()));
                    }

                    // only this type of consent may have an effective time
                    if (consent.getEffectiveTime() != null)
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.clear();
                        try
                        {
                            cal.setTime(DateFormatter.getDateFormat("yyyyMMdd").parse(consent.getEffectiveTime()));
                            patient.setConsentUpdtDateTime(cal.getTime());
                        }
                        catch(ParseException pe)
                        {
                            // TODO: use the new logging framework
                            //log.warn("Could not parse consent's effective time ({0})", new Object[]{consent.getEffectiveTime()});
                        }
                    }
                }
            }
        }

        // death notification
        if (deathNotification != null)
        {
            Code deathNotificationCd = FSICodeSetHelper.findCodeByInboundAlias("25513", deathNotification.getCode());
            if (deathNotificationCd != null && deathNotificationCd.getTitle().equals("FORMAL"))
            {
                patient.setDeathNotificationFormal(true);
            }
        }

        // preferred written communication
        if (writtenCommunicationFormat != null)
        {
            Code writtenCommunicationFormatCd =
                    FSICodeSetHelper.findCodeByInboundAlias("4000760", writtenCommunicationFormat.getCode());
            if (writtenCommunicationFormatCd != null)
            {
                patient.setPreferredCommunicationFormatCodeId(Long.parseLong(writtenCommunicationFormatCd.getId()));
            }
        }

        return patient;
    }

    public boolean isPreferredContactMethodNotNull()
    {
        if (preferredContactMethod != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isDeathNotificationNotNull()
    {
        if (deathNotification != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isWrittenCommunicationFormatNotNull()
    {
        if (writtenCommunicationFormat != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isDeceasedDateTimeNotNull()
    {
        if (deceasedTime != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Patient fromUpdatePatient(UpdatePatient updatePatient)
    {
        Patient bindingPat = new Patient();
        String nhs = updatePatient.getCurrentNHSNumber();
        /*
        Patient Id : required
        From mim specification:
        To provide the current nationally recognised identifier for the patient. 
        The root attribute will contain an OID with the value "2.16.840.1.113883.2.1.4.1"; 
        The extension attribute will contain the NHS number itself.
        */
        bindingPat.setId(new InstanceIdentifier("2.16.840.1.113883.2.1.4.1", nhs)); //from mim especification
        //Patient Addresses: optional
        if (updatePatient.getAddresses() != null)
        {
            List<PatientAddress> addressList = updatePatient.getAddresses();
            Address[] addressArr = new Address[addressList.size()];
            int AddressIndexCounter = 0;
            for (Iterator<PatientAddress> it = addressList.iterator(); it.hasNext();)
            {
                PatientAddress currentAddinList = it.next();
                Address currentAddToAdd = new Address();
                currentAddToAdd = currentAddToAdd.fromPatientAddress(currentAddinList);
                addressArr[AddressIndexCounter] = currentAddToAdd;
                AddressIndexCounter = AddressIndexCounter + 1;
            }
            bindingPat.setAddresses(addressArr);
        }
        /*
        BirthTime: required
        From mim specification:
        The date/time may be given in one of the following formats:
        YYYYMMDDHHMM where the full date and time of birth is known; 
        YYYYMMDD where the full date of birth is known; 
        YYYYMM or YYYY where the full date of birth is not known.
        */
        if (updatePatient.getBirthDateTime() != null)
        {
            bindingPat.setBirthTime(DateFormatter.getDateFormat("yyyyMMddHHmm").format(updatePatient.getBirthDateTime()));
        }
        /*
        Consents: required
        From mim specification:
        codes ‘4’ (Consent to NHS Care Record sharing) and ‘6’ (Call centre call-back consent) are used 
        to specify the type of consent being updated.                               
        */
        Consent[] consentArr = new Consent[2];
        int consentArrIndexCounter = 0;
        //updatePatient.getCallbackConsentInd() gives
        // 0-->No
        // 1--> Yes
        // 2--> No Value
        if (updatePatient.getCallbackConsentInd() != 2) //Callback consent
        {
            //If there is a value for the Consent to call back 
            Consent firstConsent = new Consent();
            //Consent Type: 
            CodedValue retConsentTypeCv = new CodedValue();
            retConsentTypeCv.setCode("6"); //From Mim
            retConsentTypeCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.35");//From Mim
            firstConsent.setType(retConsentTypeCv);
            //Consent Value: 
            CodedValue retConsentValueCv = new CodedValue();
            if (updatePatient.getCallbackConsentInd() != 0)
            {
                retConsentValueCv.setCode("5"); //From PDS Data dictionary, as mim specifies
                retConsentValueCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.2");
            }
            else
            {
                retConsentValueCv.setCode("4"); //From PDS Data dictionary, as mim specifies
                retConsentValueCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.2");
            }
            firstConsent.setValue(retConsentValueCv);
            consentArr[consentArrIndexCounter] = firstConsent;
            consentArrIndexCounter = consentArrIndexCounter + 1;
        }
        if (updatePatient.getConsentStatusCodeId() > 0) //Sharing consent                 
        {
            Code consentStatusCode = CodeSetHelper.findCodeById(String.valueOf(updatePatient.getConsentStatusCodeId()));
            Consent secondConsent = new Consent();
            //Consent Type:   
            CodedValue retConsentTypeCv = new CodedValue();
            retConsentTypeCv.setCode("4"); //From Mim
            retConsentTypeCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.35"); //From Mim
            secondConsent.setType(retConsentTypeCv);
            //Consent Value: 
            CodedValue retConsentValueCv = new CodedValue();
            retConsentValueCv.setCode(FSICodeSetHelper.findOutboundAliasByCode(consentStatusCode));
            retConsentValueCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.2"); //From Mim 
            secondConsent.setValue(retConsentValueCv);
            Date consentUpdateDateTime = updatePatient.getConsentUpdtDateTime();
            if (consentUpdateDateTime != null)
            {
                secondConsent.setEffectiveTime(DateFormatter.getDateFormat("yyyyMMdd").format(consentUpdateDateTime));
            }
            consentArr[consentArrIndexCounter] = secondConsent;
            consentArrIndexCounter = consentArrIndexCounter + 1;
        }
        if (consentArrIndexCounter > 0)
        {
        	bindingPat.setConsents(consentArr);    ////Commented to avoid - ConsentTranslator.java ln 97     
        }
        /* 
        //Death Notification: optional
        //From mim documentation: DeathNotification Assigned OID: 2.16.840.1.113883.2.1.3.2.4.16.5               
        */
        if (updatePatient.getDeceasedDateTime() != null)
        {
            CodedValue dncv = new CodedValue();
            if (updatePatient.isDeathNotificationFormal())
            {
                dncv.setCode("1"); //From PDS Data dictionary, as mim specifies
                dncv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.5"); // From mim
            }
            else
            {
                dncv.setCode("2"); //From PDS Data dictionary, as mim specifies
                dncv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.5"); // From mim
            }
            bindingPat.setDeathNotification(dncv);
        }
        /*
        Gender:optional
        From mim specification:  
        The code attribute will contain a value from the list of codes in the Sex vocabulary;
        Default Scheme:NHS Data Dictionary Sex
        Assigned OID: 2.16.840.1.113883.2.1.3.2.4.16.25
        */
        CodedValue retGenderCv = new CodedValue();
        if (updatePatient.getGenderCodeId() > 0)
        {
            Code genderCode = CodeSetHelper.findCodeById(String.valueOf(updatePatient.getGenderCodeId()));
            retGenderCv.setCode(FSICodeSetHelper.findOutboundAliasByCode(genderCode));
            retGenderCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.25");
            bindingPat.setGender(retGenderCv);
        }
        //Patient Names:requiered  
        if (updatePatient.getNames() != null)
        {
            List<com.cerner.domain.patient.PatientName> namesList = updatePatient.getNames();
            Name[] namesArr = new Name[namesList.size()];
            int NamesIndexCounter = 0;
            for (Iterator it = namesList.iterator(); it.hasNext();)
            {
                com.cerner.domain.patient.PatientName currentNameinList = (com.cerner.domain.patient.PatientName) it.next();
                Name currentNameToAdd = new Name();
                currentNameToAdd = currentNameToAdd.fromPatientName(currentNameinList);
                namesArr[NamesIndexCounter] = currentNameToAdd;
                NamesIndexCounter = NamesIndexCounter + 1;
            }
            bindingPat.setNames(namesArr);
        }
        //Patient Password:optional
        if (updatePatient.getPassword() != null)
        {
            bindingPat.setPassword(updatePatient.getEncryptedPassword());
        }
        //Preferred Contact Method:optional               
        CodedValue retPrefContactMethod = new CodedValue();
        if (updatePatient.getPreferredContactMethodCodeId() > 0)
        {
            Code prefContactMethodCode =
                    CodeSetHelper.findCodeById(String.valueOf(updatePatient.getPreferredContactMethodCodeId()));
            retPrefContactMethod.setCode(FSICodeSetHelper.findOutboundAliasByCode(prefContactMethodCode));
            retPrefContactMethod.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.16");
            bindingPat.setPreferredContactMethod(retPrefContactMethod);
        }
        //Patient's Primary Care Provider:optional
        if (updatePatient.getRegisteredGeneralPractitionerId() > 0 || updatePatient.getRegisteredPracticeOrganizationId() > 0)
        {
            PrimaryCareProvider pcProvider = new PrimaryCareProvider();
            CodedValue retPcProviderCodedValue = new CodedValue();
            retPcProviderCodedValue.setCode("1"); // For Primary Care, only one supported right at the moment
            retPcProviderCodedValue.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.37"); //From mim                      
            pcProvider.setCode(retPcProviderCodedValue);
            GlobalIdTranslator globalIdTranslator = getInjector().get(GlobalIdTranslator.class);
            InstanceIdentifier priCarePrvInstanceIdentifier = new InstanceIdentifier();
            /*
            From mim: To provide an identifier for the patient care provision details. 
            This identifier is allocated by the PDS and needs to be included in any PDS General Update message 
            where this set of patient care provision information is to be altered or deleted. 
            */
            if (updatePatient.getRegisteredGeneralPractitionerId() > 0)
            {
                // registered gp
                long registeredGPId = updatePatient.getRegisteredGeneralPractitionerId();
                String providerGlobalId =
                        globalIdTranslator.buildGlobalId(registeredGPId, com.cerner.cab.legacy.globalid.ModelTypes.PROVIDER);
                ProviderDAO providerDAO = (ProviderDAO) DAOHelper.newDAO(Provider.MODEL_TITLE);
                com.wellogic.dbone.model.Provider provider =
                        (com.wellogic.dbone.model.Provider) providerDAO.findByGlobalId(providerGlobalId);
                String CS_320 = "320";
                String EXTERNALID = "EXTERNALID";
                com.wellogic.dbone.datatypes.InstanceIdentifier externalId =
                        provider.getId(CodeSetHelper.findCodeByTitle(CS_320, EXTERNALID));
                IdentifierInformation externalIdIdInfo = (IdentifierInformation) externalId.getIdentifierInformationList().get(0);
                priCarePrvInstanceIdentifier.setRoot("2.16.840.1.113883.2.1.4.2");
                priCarePrvInstanceIdentifier.setExtension(externalIdIdInfo.getObjectIdentifier());
                pcProvider.setId(priCarePrvInstanceIdentifier);
                InstanceIdentifier priCarePrvInstanceIdentifierForPdsId = new InstanceIdentifier();
                priCarePrvInstanceIdentifierForPdsId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.1"); //from mim specification
                priCarePrvInstanceIdentifierForPdsId.setExtension("578");
                pcProvider.setPdsId(priCarePrvInstanceIdentifierForPdsId);
            }
            else
            {
                //registered practice
            	long pracId = updatePatient.getRegisteredPracticeOrganizationId();            	
                String orgId =
                    globalIdTranslator.buildGlobalId(pracId, com.cerner.cab.legacy.globalid.ModelTypes.ORGANIZATION);
                OrganizationRepository orgRepo = DependencyInjector.getInjector().get(OrganizationRepository.class);  
                Organization org = orgRepo.get(orgId);               
                priCarePrvInstanceIdentifier.setRoot("2.16.840.1.113883.2.1.4.3"); //from  mim
                priCarePrvInstanceIdentifier.setExtension(org.getNacsCodeAlias());
                pcProvider.setId(priCarePrvInstanceIdentifier);   
            }
            if (updatePatient.getPrimaryCareProviderBeginEffectiveDateTime() != null ||
                    updatePatient.getPrimaryCareProviderEndEffectiveDateTime() != null)
            {
                TimeInterval prvTi = new TimeInterval();
                if (updatePatient.getPrimaryCareProviderBeginEffectiveDateTime() != null)
                {
                    prvTi.setLow(com.cerner.system.i18n.util.DateFormatter.getDateFormat("yyyyMMddHHmm").format(
                        updatePatient.getPrimaryCareProviderBeginEffectiveDateTime()));
                }
                if (updatePatient.getPrimaryCareProviderEndEffectiveDateTime() != null)
                {
                    prvTi.setHigh(com.cerner.system.i18n.util.DateFormatter.getDateFormat("yyyyMMddHHmm").format(
                        updatePatient.getPrimaryCareProviderEndEffectiveDateTime()));
                }
                pcProvider.setEffectiveTime(prvTi);
            }
            bindingPat.setPrimaryCareProvider(pcProvider);
        }
        // Patient Phones:optional
        if (updatePatient.getPhones() != null)
        {
            List<PatientPhone> phonesList = updatePatient.getPhones();
            Telecom[] telecomArr = new Telecom[phonesList.size()];
            int phonesIndexCounter = 0;
            for (Iterator it = phonesList.iterator(); it.hasNext();)
            {
                PatientPhone currentPhoneinList = (PatientPhone) it.next();
                Telecom currentTelecomToAdd = new Telecom();
                currentTelecomToAdd = currentTelecomToAdd.fromPatientPhone(currentPhoneinList);
                telecomArr[phonesIndexCounter] = currentTelecomToAdd;
                phonesIndexCounter = phonesIndexCounter + 1;
            }
            bindingPat.setTelecoms(telecomArr);
        }
        //WrittenCommunicationFormat:optional
        CodedValue retPrefCommFormatCv = new CodedValue();
        if (updatePatient.getPreferredCommunicationFormatCodeId() > 0)
        {
            Code retPrefCommFormatCode =
                    CodeSetHelper.findCodeById(String.valueOf(updatePatient.getPreferredCommunicationFormatCodeId()));
            retPrefCommFormatCv.setCode(FSICodeSetHelper.findOutboundAliasByCode(retPrefCommFormatCode));
            retPrefCommFormatCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.17"); // From mim
            bindingPat.setWrittenCommunicationFormat(retPrefCommFormatCv);
        }
        // Preferred Language:required      
        CodedValue retPrefLanguageCv = new CodedValue();
        if (updatePatient.getPreferredLanguageCodeId() > 0)
        {
            Code retPrefLanguageCode = CodeSetHelper.findCodeById(String.valueOf(updatePatient.getPreferredLanguageCodeId()));
            retPrefLanguageCv.setCode(FSICodeSetHelper.findOutboundAliasByCode(retPrefLanguageCode));
            retPrefLanguageCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.70"); // From mim , Vocabulary Human Language
            bindingPat.setLanguageCode(retPrefLanguageCv);
        }
        //Optional: bindingPat.setBirthOrder(0);
        //Optional: : bindingPat.setConfidentialityCode(mcv);
        /* DeceasedTime: optional 
        From mim specification:
        The date/time may be given in one of the following formats:
        YYYYMMDDHHMM where the full date and time of death is known; 
        YYYYMMDD where the full date of death is known; 
        YYYYMM or YYYY where the full date of death is not known.
        */
        if (updatePatient.getDeceasedDateTime() != null)
        {
            bindingPat.setDeceasedTime(DateFormatter.getDateFormat("yyyyMMddHHmm").format(updatePatient.getDeceasedDateTime()));
        }
        //Interpreter:required
        CodedValue retInterpreterReqCv = new CodedValue();
        if (updatePatient.isInterpreterRequired())
        {
            retInterpreterReqCv.setCode("1"); // From Mim
        }
        else
        {
            retInterpreterReqCv.setCode("0"); // From Mim
        }
        retInterpreterReqCv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.16.39"); //From mim , Vocabulary for InterpreterRequiredIndicator
        bindingPat.setInterpreterRequired(retInterpreterReqCv);
        return bindingPat;
    }
    
    private boolean isValidDateRange(TimeInterval usablePeriod)
    {
        boolean validLow = true;
        boolean validHigh = true;
        if(usablePeriod != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
           
            Date[] dates = usablePeriod.toDates();
            validLow = dates[TimeInterval.LOW] == null || !dates[TimeInterval.LOW].after(today);
            validHigh = dates[TimeInterval.HIGH] == null || !dates[TimeInterval.HIGH].before(today);
        }
        
        return (validLow && validHigh);
    }
}
