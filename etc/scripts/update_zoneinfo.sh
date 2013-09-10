#!/bin/sh
export TZURL_HOME=/home/fortuna/Development/tzurl
rsync -av $TZURL_HOME/zoneinfo src/main/resources
rsync -av $TZURL_HOME/zoneinfo-global src/main/resources

