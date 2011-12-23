package com.cerner.spine.interfaces.pds.binding;

import java.util.Date;

import com.cerner.data.util.CodeSetHelper;
import com.cerner.domain.patient.PatientAlias;

/*
 * File - SupercededId.java
 * Created Nov 24, 2008
 */

/**
 * <p>TODO Insert JavaDoc</p>
 * <p>Copyright (c) 2009 Cerner Corporation</p>
 *
 * @author Sean Parmelee
 */
public class SupercededId
{
    private InstanceIdentifier id;
    private TimeInterval effectiveTime;
    
    public TimeInterval getEffectiveTime()
    {
        return effectiveTime;
    }
    public void setEffectiveTime(TimeInterval effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }
    public InstanceIdentifier getId()
    {
        return id;
    }
    public void setId(InstanceIdentifier id)
    {
        this.id = id;
    }
    
    public PatientAlias toPatientAlias()
    {
        long aliasTypeCodeId = Long.parseLong(CodeSetHelper.findCodeByTitle("4", "SSN").getId());
        PatientAlias alias = new PatientAlias(0, aliasTypeCodeId, 0);
        
        // nhs number
        alias.setAlias(id.getExtension());
        
        // effective time
        if (effectiveTime != null)
        {
            Date[] dates = effectiveTime.toDates();
            alias.setBegEffectiveDateTime(dates[0]);
            alias.setEndEffectiveDateTime(dates[1]);
        }
        
        return alias;
    }
}
