#!/usr/bin/env bash

: ${1?"Usage: $ mark-next-version.sh <release_version>"}

./gradlew markNextVersion -Prelease.version=$1
