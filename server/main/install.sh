#!/bin/sh

# install new cronjob for cs183 NMP project
PULLPATH=`pwd`
PULLPATH="${PULLPATH}/pullLogs.py"
echo $PULLPATH
(crontab -l ; echo "* * * * * $PULLPATH")| crontab -
