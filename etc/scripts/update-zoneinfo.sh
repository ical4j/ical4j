#!/usr/bin/env bash
docker pull benfortuna/tzurl && \
    docker run -v $(pwd)/src/main/resources/zoneinfo:/zoneinfo -it benfortuna/tzurl rsync -av --delete /usr/local/apache2/htdocs/zoneinfo / && \
    docker run -v $(pwd)/src/main/resources/zoneinfo-global:/zoneinfo-global -it benfortuna/tzurl rsync -av --delete /usr/local/apache2/htdocs/zoneinfo-global /
