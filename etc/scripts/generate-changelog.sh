#!/usr/bin/env bash
: ${FROM_TAG?"Need to set FROM_TAG variable"}
: ${TO_TAG?"Need to set TO_TAG variable"}
git log ${FROM_TAG}...${TO_TAG} --pretty=format:'* %s [View commit](http://github.com/ical4j/ical4j/commit/%H)' --reverse | grep -v Merge
