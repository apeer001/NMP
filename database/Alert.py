#!/usr/bin/python
import MySQLdb
import datetime

print datetime.datetime

db = MySQLdb.connect(host="localhost", 
                     user="root",      
                     passwd="",        
                     db="cs184")       

cur = db.cursor()
cur.execute("SELECT time_of_update FROM network_data ORDER BY time_of_update DESC")

# print all the first cell of all the rows
for row in cur.fetchall():
        print row
        break

db.close()
