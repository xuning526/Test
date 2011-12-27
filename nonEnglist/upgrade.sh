#!/bin/bash
me=$0
logfile=upgrade.log
krugle_backend="krugle-package.tar.gz"
krugle_front="k4.tar.gz"
filename=$1
CRON_JOB_TMP=`mktemp`

check(){
    sta=$?
    message=$1
    if [ $sta -ne 0 ]; then
        echo $message
        exit 1
    fi
}
usage(){
    echo "
    usage: 
        $me filename

    filename: a krugle upgrade package"
}
stopmonitcronjob(){
    crontab -l | grep -v "monit_reload" | grep -v "cron-krugle-logrotate" | grep -v "disk-monitor" > $CRON_JOB_TMP
    crontab $CRON_JOB_TMP
}

startmonitcronjob(){
    echo "*/10 * * * * /bin/bash /data/krugle/scripts/monit_reload.sh" >> $CRON_JOB_TMP
    echo "0 0 * * * /bin/bash /data/krugle/lib/logrotate/cron-krugle-logrotate.sh" >> $CRON_JOB_TMP
    echo "*/10 * * * * /bin/bash /data/krugle/lib/hub/etc/init.d/disk-monitor restart" >> $CRON_JOB_TMP
    crontab $CRON_JOB_TMP
}

installpythonlib(){
    su - k4 -c "
    source /home/k4/Envs/k4/bin/activate
    pip install chardet pygments pysolr
    if [ ! -z $? ]; then
        exit 1 # exit current k4 user
        exit 1 # exit this scripts
    fi
    deactivate
    exit 0
    "
}

rm -rf $logfile $krugle_front $krugle_backend md5sum.txt sha1sum.txt
if [ -z $1 ]; then
    usage
    exit 1
fi
if [ ! -f $1 ]; then
    echo 'The krugle build package is required'
    usage
    exit 2
fi

unzip $filename 

#check data integrity
md5sum md5sum.txt >/dev/null 
if [ $? -ne 0 ]; then
    echo 'The download file is not integrity'
    exit 1;
fi

sha1sum -c sha1sum.txt >/dev/null
if [ $? -ne 0 ]; then
    echo 'The download file is not integrity'
    exit 1;
fi

# stop monit reload cronjob
stopmonitcronjob
# stop krugle4 service
monit stop all > /dev/null 2>&1

sleep 60 # wait the service to stop 

rm -rf /data/krugle/lib.old
mv /data/krugle/lib /data/krugle/lib.old
rm -rf /home/k4/LucidWorks/lucidworks/solr/lib

echo "Deploying krugle backend..." | tee -a $logfile
tar xf $krugle_backend -C / >> $logfile
check "Failed when deploying krugle backend."
echo "Deploy krugle_backend successfully" | tee -a $logfile

echo "Deploying krugle front..." | tee -a $logfile
tar xf $krugle_front -C /home/k4/k4/ >> $logfile
check "Failed when deploying krugle frontend."
echo "Deploy krugle front successfully" | tee -a $logfile

# fix permission
chmod +x /data/krugle/lib/api/scripts/wrapper-ent-live.sh
chmod +x /data/krugle/lib/hub/etc/init.d/*
chmod +x /data/krugle/lib/sloccount/bin/*

# install python dependency library
installpythonlib 
# XXX: fix k4 home permission 
chown k4:k4 -R /home/k4/

# FIX: add krugle-rdate service 
if [ ! `rpm -q ntp >/dev/null 2>&1 &&  echo $?` ]; then 
    yum install -y -q ntp
fi

chmod +x /data/krugle/lib/install-scripts/etc/init.d/krugle-rdate
if [ ! -f /etc/init.d/krugle-rdate ]; then
    ln -fs /data/krugle/lib/install-scripts/etc/init.d/krugle-rdate /etc/init.d/krugle-rdate
    cd /etc/init.d/
    chkconfig --add krugle-rdate
    chkconfig --levels 35 krugle-rdate on
fi

if [ ! -f /etc/init.d/disk-monit ]; then
    ln -fs /data/krugle/lib/hub/etc/init.d/disk-monitor /etc/init.d/disk-monitor
    cd /etc/init.d/
fi
chkconfig -add disk-monitor


# set the default life of log to 45 days
lifeOfLogFile=`echo "select _key, _value from ConfigurationTuple where _key='lifeOfLogFile'" | mysql -uroot hub | grep lifeOfLogFile| cut -f 2`
if [ -z "${lifeOfLogFile}" -o "${lifeOfLogFile}" == "3888000000" ]
then
    echo "delete from ConfigurationTuple where _key = 'lifeOfLogFile'" | mysql -uroot hub
    echo "insert into ConfigurationTuple(_key,_value) values('lifeOfLogFile','ONE_AND_HALF_MONTH')" | mysql -uroot hub
fi

/etc/init.d/krugle-rdate start #FIXME: add krugle-rdate to monit

#FIXME: fix monitrc 
chown root:root /etc/monitrc

# upgrade successfully, start all service
echo "Try to start all service"
sleep 20
monit start all >/dev/null 2>&1
check "Failed when start all service"
monit reload >/dev/null 2>&1

#start monit reload cronjob
startmonitcronjob

echo 'Upgrade krugle successfully.'

