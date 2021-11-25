SHELL:=/bin/bash
include .env

.PHONY: all gradlew clean build zoneinfo changelog currentVersion markNextVersion release publish

all: test

gradlew:
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin

clean:
	./gradlew clean

test:
	./gradlew test

build:
	./gradlew build

zoneinfo:
	docker pull benfortuna/tzurl && \
        docker run -v $(pwd)/src/main/resources/zoneinfo:/zoneinfo -it benfortuna/tzurl rsync -av --delete /usr/local/apache2/htdocs/zoneinfo / && \
        docker run -v $(pwd)/src/main/resources/zoneinfo-global:/zoneinfo-global -it benfortuna/tzurl rsync -av --delete /usr/local/apache2/htdocs/zoneinfo-global /

changelog:
	git log "$(CHANGELOG_START_TAG)...$(CHANGELOG_END_TAG)" \
    	--pretty=format:'* %s [View commit](http://github.com/ical4j/ical4j/commit/%H)' --reverse | grep -v Merge

currentVersion:
	./gradlew -q currentVersion

markNextVersion:
	NEXT_VERSION=$(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
	./gradlew markNextVersion -Prelease.version=$(NEXT_VERSION)

release:
	./gradlew release

publish:
	./gradlew publish
