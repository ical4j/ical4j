#!/usr/bin/env bash
[[ $GIT_BRANCH =~ ^release ]] && ./gradlew -Pbintray_user=$BINTRAY_USER -Pbintray_key=$BINTRAY_KEY release bintrayUpload