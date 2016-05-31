#!/usr/bin/python

import httplib
import sys
import MySQLdb
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
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

# Check if database contains an IP address
def isIPInDB(ip):
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    # must create a cursor object
    cur = db.cursor()
    try:
        query_stmt = "SELECT EXISTS(SELECT 1 FROM " + table_net + " WHERE computer_IP ='" + ip + "' LIMIT 1)"
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
def installNewClient(ip,usr='root'):

    print('Connecting to Database...')
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    print('Connected to Database')
    # must create a cursor object
    cur = db.cursor()
    try:
        print('INSERTING')
        # Get number of rows 
        query_stmt = "SELECT COUNT(*) FROM " + table_net
        cur.execute(query_stmt)
        (comp_id,) = cur.fetchone()
        print(comp_id)
        # Insert data into mysql database
        # Data Insert into the table
        insert_stmt = (
            "INSERT INTO " + table_net + " (username, computer_id, computer_IP) "
            "VALUES (%s, %s, %s)"
        )
        data = (usr, comp_id, ip)
                                                                           
        cur.execute(insert_stmt,data)
        db.commit()

        print('commited all data')
        # Get all infor from table
        cur.execute("SELECT * FROM " + table_recent) 
       
        # print all the first cell of all the rows
        for row in cur.fetchall():
            print row[0]
    except:
        db.rollback()
        print('Rolled back database due to error')
    
    db.close()

# Update MySQL database
def pullDatabaseData():

    print('Connecting to Database...')
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    print('Connected to Database')
    # must create a cursor object
    cur = db.cursor()
    data = None
    try:
        print('SELECTING data for Android')
        # Get number of rows 
        query_stmt = "SELECT * FROM " + table_data
        cur.execute(query_stmt)
    
        # print all the first cell of all the rows
        data = ""
        for row in cur.fetchall():
            length = len(row) - 1 
            for r in range(0,length):
                data += r + ","
            data += row[-1] + "\n"
    except:
        db.rollback()
        print('Rolled back database due to error')
    
    db.close()

    return data


#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):
  
   #handle GET command
    def do_GET(self):
        try:
            data_to_send = pullDatabaseData()
            if  not data_to_send == None:
                #send code 200 response
                self.send_response(200)

                #send header first
                self.send_header('Content-type','text-html')
                self.end_headers()

                #send data content to android
                self.wfile.write(data_to_send)
                return
            else:
                self.send_error(404, 'Database could not be reached')
      
        except IOError:
            self.send_error(404, 'Server could not handle request')

    #handle POST command
    def do_POST(self):
        rootdir = '/home/ec2-user/clientlogs/' #client log location
        if not os.path.exists(rootdir):
            os.makedirs(rootdir)
        clientAddress = self.client_address[0]
        
        # Update MySQL database 
        update = isIPInDB(clientAddress)
        if update == 0:
            installNewClient(clientAddress) 

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




#try:
#                if self.path.endswith('.txt'):
#                    if os.path.exists(rootdir + self.path):
#                        f = open(rootdir + self.path, "a") #open requested file, append
#                    else:
#                        f = open(rootdir + self.path, "w") #open requested file, new file
#                
#                    f.write(self.path + '\n') 
#                    f.close()
#            except IOError:
#                self.send_error(404, 'file not found')
#                return 

