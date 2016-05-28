#!/usr/bin/python

import httplib
import sys
import MySQLdb
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import os

hostname = "localhost"
username = "root"
password = ""
database = "cs183"

# Functions

# Check if database contains an IP address
def isIPInDB(ip):
    db = MySQLdb.connect(host=hostname, # your host, usually localhost
                         user=username,          # username
                         passwd=password,        # password
                         db=database)       # name of the database

    # must create a cursor object
    cur = db.cursor()
    try:
        query_stmt = "SELECT EXISTS(SELECT 1 FROM most_recent_network_status WHERE computer_IP ='" + ip + "' LIMIT 1)"
        cur.execute(query_stmt)  
        res = 0
        for (result,) in  cur:
            res = result
            break
        
        # close database
        db.close()
        return res
    except:
        db.rollback()
        print('Rolling back database due to error')
    db.close()
    return 0

# Update MySQL database
def updateDatabase(update,ip,user='root',status='OK',load=5,temp=10,net_load=20):

    print('Connecting to Database...')
    db = MySQLdb.connect(host=hostname, # your host, usually localhost
                         user=username,          # username
                         passwd=password,        # password
                         db=database)       # name of the database

    print('Connected to Database')
    # must create a cursor object
    cur = db.cursor()
    try:
        query_stmt = "SELECT EXISTS(SELECT 1 FROM most_recent_network_status WHERE computer_IP ='" + ip + "' LIMIT 1)"
        cur.execute(query_stmt)  
        
        if update == 1:
            print('UPDATING')
            # Update data into mysql database
            # Data update into the table
            insert_stmt = (
                """UPDATE most_recent_network_status
                SET admin_username=%s, computer_status=%s, cpu_load=%s, computer_temp=%s, network_load=%s
                WHERE computer_IP=%s"""
            )
            data = (user, status, load, temp, net_load, ip)
                                                                           
            cur.execute(insert_stmt,data)
            db.commit()
    
        else:
            print('INSERTING')
            # Get number of rows 
            query_stmt = "SELECT COUNT(*) FROM most_recent_network_status"
            cur.execute(query_stmt)
            (comp_id,) = cur.fetchone()

            # Insert data into mysql database
            # Data Insert into the table
            insert_stmt = (
                "INSERT INTO most_recent_network_status (admin_username, computer_id, computer_IP, computer_status, cpu_load, computer_temp, network_load) "
                "VALUES (%s, %s, %s, %s, %s, %s, %s)"
            )
            data = (user, comp_id, ip, status, load, temp, net_load)
                                                                           
            cur.execute(insert_stmt,data)
            db.commit()

            # Get all infor from table
            cur.execute("SELECT * FROM most_recent_network_status") 
       
        # print all the first cell of all the rows
        for row in cur.fetchall():
            print row[0]
    except:
        db.rollback()
        print('Rolled back database due to error')
    
    db.close()


#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):
  
   #handle GET command
    def do_GET(self):
        rootdir = '/root/NMP/clientlogs/' #file location
        try:
            if self.path.endswith('.txt'):
                f = open(rootdir + self.path) #open requested file

            #send code 200 response
            self.send_response(200)

            #send header first
            self.send_header('Content-type','text-html')
            self.end_headers()

            #send file content to client
            self.wfile.write(f.read())
            f.close()
            return
      
        except IOError:
            self.send_error(404, 'file not found')

    #handle POST command
    def do_POST(self):
        rootdir = '/home/ec2-user/clientlogs/' #client log location
        if not os.path.exists(rootdir):
                os.makedirs(rootdir)
        clientAddress = self.client_address[0]
        
        # Update MySQL database 
        update = isIPInDB(clientAddress)
        if update == 0:
            updateDatabase(update,clientAddress) 
        else:
            updateDatabase(update,clientAddress) 
            try:
                if self.path.endswith('.txt'):
                    if os.path.exists(rootdir + self.path):
                        f = open(rootdir + self.path, "a") #open requested file, append
                    else:
                        f = open(rootdir + self.path, "w") #open requested file, new file
                
                    f.write(self.path + '\n') 
                    f.close()
            except IOError:
                self.send_error(404, 'file not found')
                return 

        #send code 200 response
        self.send_response(200)

        #send header first
        self.send_header('Content-type','text-html')
        self.end_headers()

        #send file content to client
        #self.wfile.write(f.read())
        return

# Start server 
def runServer():
    print('http server is starting...')
    
    server_address = ('172.31.17.185', 8080)

    httpd = HTTPServer(server_address, KodeFunHTTPRequestHandler)
    print('http server is running...')
    httpd.serve_forever() 

if __name__ == '__main__':
    runServer()

    #get http server ip
    #http_server = "169.235.217.141"
    #http_server = '52.10.214.107'
    #create a connection
    #conn = httplib.HTTPConnection(http_server,8080)

    #while 1:
    #    cmd = raw_input('input command (ex. GET index.html): ')
    #    cmd = cmd.split()

    #    if cmd[0] == 'exit': #tipe exit to end it
    #        break

        #request command to server
        #conn.request(cmd[0], cmd[1])
    #    conn.request("GET", "myIP.txt")
        
        #get response from server
    #    rsp = conn.getresponse()

        #print server response and data
    #    print(rsp.status, rsp.reason)
    #    data_received = rsp.read()
    #    print(data_received)
      
    #conn.close()
