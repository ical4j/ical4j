#!/usr/bin/env bash
GIT_BRANCH=${TRAVIS_BRANCH:-$(git rev-parse --abbrev-ref HEAD)}
[[ $GIT_BRANCH =~ ^release ]] && ./gradlew -Pbintray_user=$BINTRAY_USER -Pbintray_key=$BINTRAY_KEY \
 -Psonatype_username=$SONATYPE_USER -Psonatype_password=$SONATYPE_PASSWORD \
 release bintrayUpload $@