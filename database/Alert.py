#!/usr/bin/python
import MySQLdb
import datetime
import subprocess
import os

#Critical Values 
CRITICAL_TEMP = 80
CRITICAL_CPU_LOAD = 80 
CRITICAL_NETWORK_LOAD = 80

#Warning Values
WARNING_TEMP = 40
WARNING_CPU_LOAD = 40
WARNING_NETWORK_LOAD = 40

#MySQL junk
db = MySQLdb.connect(host="localhost", 
                     user="root",      
                     passwd="",        
                     db="cs184")       

cur = db.cursor()
cur.execute("SELECT * FROM network_data GROUP BY computer_id ORDER BY time_of_update DESC")
#cur.execute("SELECT * FROM most_recent_network_status")

isPC_ON = True

for row in cur.fetchall():
    #check info
    if (row[3] < datetime.datetime.now() - datetime.timedelta(minutes=5)):
        print "haven't received logs from %s in over five minutes" % (row[2])
        emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) has been offline for more than 5 minutes\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
        os.system(emailcommand)
        isPC_ON = False

    #Only notify them if PC is on to reduce spam
    if (isPC_ON):
        if (row[5] > CRITICAL_CPU_LOAD):
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the critical CPU_LOAD threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)
        elif (row[5] > WARNING_CPU_LOAD):
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the warning CPU_LOAD threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)

        if (row[6] > CRITICAL_TEMP):
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the critical temperature threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)
        elif (row[6] > WARNING_TEMP):
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the warning temperature threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)


        if (row[7] > CRITICAL_NETWORK_LOAD):    
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the critical network_load threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)
        if (row[7] > WARNING_NETWORK_LOAD):    
            emailcommand = "echo -e \"Subject: NMP Notification\n\nYour PC(%s) is currently exceeding the warning network_load threshold specified\" | sendmail -F NMP -t nmptester@gmail.com" % (row[2])
            os.system(emailcommand)

    #reset status for next PC
    isPC_ON = True

db.close()
