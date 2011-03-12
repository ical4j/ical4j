#!/bin/sh
export TZURL_HOME=/home/build/tzurl
rsync -av $TZURL_HOME/zoneinfo
rsync -av $TZURL_HOME/zoneinfo-global

