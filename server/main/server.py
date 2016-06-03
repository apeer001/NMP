#!/usr/bin/python


from sys import version as python_version
from cgi import parse_header, parse_multipart

if python_version.startswith('3'):
    from urllib.parse import parse_qs
    from http.server import BaseHTTPRequestHandler, HTTPServer
else:
    from urlparse import parse_qs
    from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

import httplib
import sys
import MySQLdb
import os
import base64

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
        # Get rows for most recent
        query_stmt = "SELECT * FROM " + table_recent
        cur.execute(query_stmt)
    
        # print all the first cell of all the rows
        data = ""
        for row in cur.fetchall():
            length = len(row) - 1
            for i in range(0,length):
                data += str(row[i]) + ","
            data += str(row[-1]) + "\n"

        data += "--------------------------------------\n"

        # Get rows for all data 
        query_stmt = "SELECT * FROM " + table_data
        cur.execute(query_stmt)
    
        for row in cur.fetchall():
            length = len(row) - 1
            for i in range(0,length):
                data += str(row[i]) + ","
            data += str(row[-1]) + "\n"

    except:
        print('database could not be reached due to error')
    
    db.close()
    return data

def get_password_from_username(user):
# returns nothing if username does not exist
    data = "";
    print('Connecting to Database...')
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    print('Connected to Database')
    # must create a cursor object
    cur = db.cursor()
    try:
        # Get rows for most recent
        query_stmt = "SELECT u.password FROM " + table_user + " u WHERE u.username='" + user + "'";
        cur.execute(query_stmt);
    
        # print all the first cell of all the rows
        #for row in cur.fetchall():
         #   length = len(row) - 1
          #  for i in range(0,length):
           #     data += str(row[i]) + ","
            #data += str(row[-1]) + "\n"
        if cur.rowcount == 0:
            return data;

        data += cur.fetchall()[0][0];
    except:
        print "database could not be reached due to error";


    return data;

#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):

    def parse_POST(self):
        try:
            headers = self.headers['content-type']
            ctype, pdict = parse_header(self.headers['content-type'])
            if ctype == 'multipart/form-data':
                postvars = parse_multipart(self.rfile, pdict)
            elif ctype == 'application/x-www-form-urlencoded':
                length = int(self.headers['content-length'])
                postvars = parse_qs(self.rfile.read(length), keep_blank_values=1)
            else:
                postvars = {}
            return postvars
        except:
            postvars = {}
            return postvars
 
  
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
        #readFile = self.rfile.readline()
        #print("data: " + readFile)
        postvars = self.parse_POST()
        if postvars:
            print('there is data')
            print(postvars)
            username = postvars['param1']
            password = postvars['param2']
            if len(username) == 1 and len(password) == 1:
                username = username[0]
                password = password[0]
                authentic_password = get_password_from_username(username)
                print authentic_password
                print username
                print password

                if not authentic_password:
                    self.send_response(403)
                    return
                if password != authentic_password:
                    self.send_response(403)
                    return
                self.send_response(200)
                self.send_header('Content-type', 'text-html')

                self.end_headers()
                
                key = base64.b64encode(password)
                self.wfile.write('key:' + str(key) + "\r\n")
                print(key)

                return

            self.send_response(403)
            return

        else:
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

