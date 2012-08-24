#!/bin/sh
if [ -z "$COMPACTOR_OPTS" ]; then
    COMPACTOR_OPTS='-Xmx28g -d64 -server -Djava.awt.headless=true -XX:+UseConcMarkSweepGC -XX:+AggressiveOpts'
fi

if [ ! -z "$ENABLE_YOURKIT" ]; then
    COMPACTOR_OPTS="$MAVEN_OPTS -agentlib:yjpagent"
fi

echo "Set COMPACTOR_OPTS to pass jvm parameters. Currently is set to: $COMPACTOR_OPTS"

mvn exec:exec -Dexec.executable="java" -Dexec.classpathScope=runtime -Dexec.args="-cp %classpath $COMPACTOR_OPTS com.moviepilot.sheldon.compactor.main.Main $*"
