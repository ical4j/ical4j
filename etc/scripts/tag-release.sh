#!/usr/bin/env bash
GIT_BRANCH=${TRAVIS_BRANCH:-$(git rev-parse --abbrev-ref HEAD)}
[[ $GIT_BRANCH =~ ^release ]] && ./gradlew release bintrayUpload $@
