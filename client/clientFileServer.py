#!/usr/bin/python

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import os
import httplib

logFile = 'client_log.txt'

#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):
		
  def do_GET(self):
    print "sending junk"
    rootdir = '/home/ec2-user/cs183/NMP/client/' #file location
    if not os.path.exists(rootdir):
        os.makedirs(rootdir)
    try:
      if os.path.exists(rootdir + logFile):
        f = open(rootdir + logFile) #open requested file

        #send code 200 response
        self.send_response(200)

        #send header first
        self.send_header('Content-type','text-plain')
        self.end_headers()

        #send file content to client
        self.wfile.write(f.read())
        f.close()
        return
      
    except IOError:
      self.send_error(404, 'file not found')

# Send Init Client data  
def initClientOnMain():

    print('Preparing to send client data...')
    #Get http server ip and port
    http_server = '52.32.201.77'
    http_port = 8080

    #Create a connection
    conn = httplib.HTTPConnection(http_server,http_port)

    status = 0
    tries = 0
    while status != 200 and tries < 5:
        #Request command to server
        conn.request("POST", "172.31.26.65.txt")
        print('Connection established with ' + http_server)

        #Get response from server
        rsp = conn.getresponse()
        status = rsp.status
        print('Status: ' + str(status))
        #Print server response and data
        print(rsp.status, rsp.reason)
        data_received = rsp.read()
        print(data_received)
        
        #update try
        tries += 1

    conn.close()
    print('Connection to ' + http_server + ' closed!')
    return status

# Client File Server
def runClientFileServer():
  print('http fileserver is starting...')

  #ip and port of servr
  #by default http server port is 80, we set it to 8080
  server_address = ('172.31.26.65', 8080)
  httpd = HTTPServer(server_address, KodeFunHTTPRequestHandler)
  print('http server is running...')
  httpd.serve_forever()
  
if __name__ == '__main__':
    # Send init data to main Server
    status = initClientOnMain()
    
    if status == 200:
        # Serve any request from the main Server
        runClientFileServer()
    else:
        print('Error: Unable to send client init data')

