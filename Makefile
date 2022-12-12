SHELL:=/bin/bash
include .env

NEXT_VERSION=$(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
CHANGE_JUSTIFICATION=$(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

.PHONY: all gradlew clean build zoneinfo changelog currentVersion markNextVersion listApiChanges approveApiChanges \
	verify release publish

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
	./gradlew markNextVersion -Prelease.version=$(NEXT_VERSION)

listApiChanges:
	./gradlew revapi

approveApiChanges:
	./gradlew :revapiAcceptAllBreaks --justification $(CHANGE_JUSTIFICATION)

verify:
	./gradlew verify

release: verify
	./gradlew release

publish:
	./gradlew publish
