#!/usr/bin/env bash
GIT_BRANCH=${GIT_BRANCH:-$(git rev-parse --abbrev-ref HEAD)}
[[ $GIT_BRANCH =~ ^release ]] && ./gradlew -Pbintray_user=$BINTRAY_USER -Pbintray_key=$BINTRAY_KEY release bintrayUpload