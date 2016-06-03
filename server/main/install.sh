#!/bin/sh

# install new cronjob for cs183 NMP project
PULLPATH=`pwd`
PULLPATH="${PULLPATH}/pullLogs.py"
echo $PULLPATH
(crontab -l ; echo "* * * * * $PULLPATH")| crontab -
# install new cronjob for email alerts
PULLPATH="/home/ec2-user/cs183/NMP/database"
(crontab -l ; echo "* * * * * $PULLPATH") | crontab -
