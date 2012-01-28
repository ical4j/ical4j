#!/bin/sh
export TZURL_HOME=/home/build/tzurl
rsync -av $TZURL_HOME/zoneinfo src/main/resources
rsync -av $TZURL_HOME/zoneinfo-global src/main/resources

