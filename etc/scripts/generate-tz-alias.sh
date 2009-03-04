## An AWK script for reading the Olson backward compatibility file and generating an alias properties file
#
## Latest tzdata available here: ftp://elsie.nci.nih.gov/pub/
#
awk '/Link/ {print $3,"=",$2}' backward