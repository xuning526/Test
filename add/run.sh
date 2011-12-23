#! /bin/bash
# 
# The SCMI run script. Must be run with bash. 
#
# Environment Variables
#
#   SCMI_HOME	The location of the SCMI deployment directory. Defaults to ..
#				(one level up from where this script is running)
#
#   JAVA		The Java command name. Defaults to "java"
#
#	JAVA_HOME	Location for Java command. Used if the Java tool can't be found,
#				in which case it must be set.
#

# resolve links - $0 may be a softlink
THIS="$0"
while [ -h "$THIS" ]; do
  ls=`ls -ld "$THIS"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    THIS="$link"
  else
    THIS=`dirname "$THIS"`/"$link"
  fi
done

THIS_DIR=`dirname "$THIS"`
if test -z "$SCMI_HOME"; then
	SCMI_HOME=`cd "$THIS_DIR/.." ; pwd`
	echo "Using SCMI home directory '$SCMI_HOME'. Overwrite this by setting the SCMI_HOME environment variable."
fi

# See if we got passed the file where we should store the PID
PID_FILE=/dev/null
if [ $1 ]; then
	PID_FILE="$1"
fi

if test -z "$JAVA"; then
	JAVA=java
fi

# Test if Java is found
which $JAVA &>/dev/null
exitCode=$?
if [[ $exitCode != 0 ]]; then
	if test -z "$JAVA_HOME"; then
	    echo "$JAVA was not found. Please add it to the PATH environment variable or set the JAVA_HOME environment."
	    exit 1
	fi
	
	export JAVA=${JAVA_HOME}/bin/java
    which $JAVA &>/dev/null
    if [[ $? != 0 ]]; then
	    echo "$JAVA was not found. Please add it to the PATH environment variable or set the JAVA_HOME environment correctly."
	    exit 1
	fi
fi

# Test that $JAVA doesn't point to gcj/gij
ISGCJ=`$JAVA -version 2>&1 | grep gcj`
if [ "$ISGCJ" != "" ] ; then
  echo ""
  echo "Error: $JAVA appears to be GNU libgcj. Please look for the Sun version of Java on your system and ensure its on your PATH. If you cannot find it, download and install it."
  exit 1
fi


# Add all of the jars found in ${SCMI_HOME}/lib/ to the classpath
CLASSPATH=
for JAR in `find ${SCMI_HOME}/lib -name "*.jar"`
do
	CLASSPATH="$CLASSPATH:$JAR"
done

# Add all of the jar files found in ${SCMI_HOME}/plugins/ to the classpath
for file in `find ${SCMI_HOME}/plugins -name "*.jar"`
do
	CLASSPATH="$CLASSPATH:$file"
done

# Also add the plugins folder to include the *.properties files in there
CLASSPATH="$CLASSPATH:${SCMI_HOME}/plugins"
# The property files may also be in the SCMI_HOME directory
CLASSPATH="$CLASSPATH:${SCMI_HOME}/"

# Set up for us to run with a 512K stack and 64MB of heap.
# Adjust -Xmx higher if out of memory conditions occur.
JAVA_ARGS="-server -Xss512k -Xms64M -Xmx512M"
ulimit -s 512

. ${SCMI_HOME}/bin/scmi-env.sh

# Check if variable scmi.handler and scmi.properties are set
if test -z "${APPLICATION_CLASS}"; then
    echo "Variable APPLICATION_CLASS must be set in file scmi-env.sh"
    exit 1
fi
if test -z "${PROPERTIES_FILE}"; then
    echo "Variable PROPERTIES_FILE must be set in file scmi-env.sh"
    exit 1
fi

cd ${SCMI_HOME}

${JAVA} -cp $CLASSPATH \
    $JAVA_ARGS \
	-Dscmi.application=${APPLICATION_CLASS} \
	-Dscmi.properties=${PROPERTIES_FILE} \
	-Dlog4j.configuration=file:${SCMI_HOME}/log4j.properties \
	net.krugle.scmi.server.ScmiServer &
PID=$!
EXITCODE=$?

cd -

if [[ $EXITCODE != 0 ]]; then
	exit $EXITCODE
fi

echo $PID > $PID_FILE

disown %1

exit 0
