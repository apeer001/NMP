#!/bin/sh

# Uninstall the cronjob for pulling client data
PULLPATH=`pwd`
PULLPATH="${PULLPATH}/pullLogs.py"
echo $PULLPATH

crontab -l | grep -v "$PULLPATH"  | crontab -
