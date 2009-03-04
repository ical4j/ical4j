## An AWK script for reading the Olson backward compatibility file and generating an htaccess file
#
## Latest tzdata available here: ftp://elsie.nci.nih.gov/pub/
#
awk '/Link/ {print "RewriteRule","^(.*)"$3,"$1"$2,"[NC]"}' backward
