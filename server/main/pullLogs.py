#!/usr/bin/python
# Author: Aaron Peery

from datetime import datetime
from datetime import timedelta
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import httplib
import sys
import MySQLdb
import os


# Initial mysql db login
hostname = "localhost"
username = "root"
password = ""
database = "cs184"

# Name of tables used
table_user = "users"                            # system admin info
table_net = "network"                           # client general info
table_data = "network_data"                     # all client log data
table_recent = "most_recent_network_status"     # most recent (daily) log data for website

# Functions
def getLogFromClient(ip):
    #get http server ip
    http_server = ip
    #create a connection
    conn = httplib.HTTPConnection(http_server,8080)

    #request command to server
    conn.request("GET", "")
         
    #get response from server
    rsp = conn.getresponse()

    #print server response and data
    print(rsp.status, rsp.reason)
    data_received = rsp.read()
    print(data_received)
    
    if not rsp.status == 200:
        data_received = None

    conn.close()

    return data_received

def updateLogDB(cur,ip,comp_id,logpart,user='root',table=table_data):
    print('INSERTING NEW LOG')   
    if len(logpart) ==  6:                                                  
        timestamp = logpart[0]
        comp_status = logpart[1]
        cpu_load = logpart[2]
        comp_temp = logpart[3]
        net_load = logpart[4]
        description = logpart[5]
    else:
    	d = datatime.datetime.now()
    	d.strftime("YY-MM-DD HH:mm:ss")
        timestamp = d
        comp_status = 'CRITICAL'
        cpu_load = -1
        comp_temp = -1
        net_load = -1
        description = 'default log entry for testing'        
    
    # Insert data into mysql database
    # Data Insert into the table
    insert_stmt = (
        "INSERT INTO " + table + " (admin_username, computer_id, computer_IP, time_of_update, computer_status, cpu_load, computer_temp, network_load, status_description) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
    )
    data = (user, comp_id, ip, timestamp, comp_status, cpu_load, comp_temp, net_load, description)
    print (data) 
    cur.execute(insert_stmt,data)

# Start pulling logs from clients
def getLogs():
    print('http pull requests is starting...')
    
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    # must create a cursor object
    cur = db.cursor()
    
    # Query for all ip addresses in network table
    query_stmt = "SELECT * FROM " + table_net
    cur.execute(query_stmt)  
    for (admin_user,comp_id,comp_ip) in cur.fetchall():
        print(admin_user + " " + str(comp_id) + " " + str(comp_ip))
        # Get log data
        try:
            data_received = getLogFromClient(comp_ip)
            if not data_received == None:
                # Input new log data into log table in db
                try:
                    if len(data_received) > 0:
                        data_received = data_received.strip('\n')
                        lines = data_received.split('\n') 
                        for line in lines:
                            logPart = line.split(',')
                            updateLogDB(cur,comp_ip,comp_id,logPart,admin_user)
                            db.commit()         
                except:
                    db.rollback()
                    print('Rolling back database due to error')
            else:
                d = datetime.now()
                d.strftime("YY-MM-DD HH:mm:ss") 
                logPart = [d,'OFFLINE',-1,-1,-1,'This client is unable to be reached. Needs Attention!']
                updateLogDB(cur,comp_ip,comp_id,logPart,admin_user)
        except:
            print('Unable to connect to ' + comp_ip)
    
    db.close()
    return 0


# Checks the 'network_data' table and finds all logs from the last 10 minutes
# and places them in the 'most_recent_network_status' table
def updateMostRecentErrorsDB():
	print('updating ' +  table_recent + ' requests is starting...')

	db = MySQLdb.connect(host=hostname,     # your host, usually localhost
	                     user=username,     # username
	                     passwd=password,   # password
	                     db=database)       # name of the database

	# must create a cursor object
	cur = db.cursor()

	# Query for all ip addresses in network table
	upperBound = datetime.now()
	lowerBound = upperBound - timedelta(minutes=10)
	upperBound.strftime("YY-MM-DD HH:mm:ss") 
	lowerBound.strftime("YY-MM-DD HH:mm:ss") 
	print(lowerBound)
	print(upperBound)
	query_stmt = "SELECT * FROM " + table_data + " WHERE time_of_update BETWEEN '" + str(lowerBound) +"' '" + str(upperBound) + "'"
	cur.execute(query_stmt)  
	for (admin_user, comp_id, comp_ip, timestamp, comp_status, cpu_load, comp_temp, net_load, description) in cur.fetchall():
	    print(admin_user + " " + str(comp_id) + " " + str(comp_ip))
	    # update log row
	    try:
			for line in lines:
				logPart = [timestamp, comp_status, cpu_load, comp_temp, net_load, description]
				updateLogDB(cur,comp_ip,comp_id,logPart,admin_user,table_recent)
				db.commit()         
	    except:
	        db.rollback()
	        print('Rolling back database due to error')
	        
	db.close()
 
# Main
if __name__ == '__main__':
    getLogs()
    updateMostRecentErrorsDB()

