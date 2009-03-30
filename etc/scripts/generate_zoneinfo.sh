#!/bin/sh
make clean; make
./vzic --output-dir zoneinfo --url-prefix http://tzurl.org/zoneinfo
scp -rp zoneinfo modularity@tzurl.org:tzurl.org
