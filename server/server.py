#!/usr/bin/python

import httplib
import sys

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import os

#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):
  
  #handle GET command
   #def do_GET(self):
   # rootdir = '/root/NMP/clientlogs/' #file location
   # try:
   #   if self.path.endswith('.txt'):
   #     f = open(rootdir + self.path) #open requested file

        #send code 200 response
   #     self.send_response(200)

        #send header first
   #     self.send_header('Content-type','text-html')
   #     self.end_headers()

   #     #send file content to client
   #     self.wfile.write(f.read())
   #     f.close()
   #     return
      
   # except IOError:
   #   self.send_error(404, 'file not found')

   

    #handle POST command
    def do_POST(self):
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
  
def run():
    print('http server is starting...')

if __name__ == '__main__':
    run()

    #get http server ip
    #http_server = "169.235.217.141"
    http_server = '52.10.214.107'
    #create a connection
    conn = httplib.HTTPConnection(http_server,8080)

    while 1:
        cmd = raw_input('input command (ex. GET index.html): ')
        cmd = cmd.split()

        if cmd[0] == 'exit': #tipe exit to end it
            break

        #request command to server
        #conn.request(cmd[0], cmd[1])
        conn.request("GET", "myIP.txt")
        
        #get response from server
        rsp = conn.getresponse()

        #print server response and data
        print(rsp.status, rsp.reason)
        data_received = rsp.read()
        print(data_received)
      
    conn.close()
