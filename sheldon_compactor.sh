#!/bin/sh
if [ -z "$MAVEN_OPTS" ]; then
    MAVEN_OPTS='-Xmx28g -d64 -server -Djava.awt.headless=true -XX:+UseConcMarkSweepGC'
fi

if [ ! -z "$ENABLE_YOURKIT" ]; then
    MAVEN_OPTS="$MAVEN_OPTS -agentlib:yjpagent"
fi


echo "Set MAVEN_OPTS to pass jvm parameters. Currently is set to: $MAVEN_OPTS"
mvn exec:java -Dexec.args="$*"
