#!/usr/bin/python

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import os

#Create custom HTTPRequestHandler class
class KodeFunHTTPRequestHandler(BaseHTTPRequestHandler):
		
  def do_GET(self):
    print "sending junk"
    rootdir = '/home/ec2-user/cs183/NMP/client/' #file location
    try:
      if self.path.endswith('.txt'):
        f = open(rootdir + self.path) #open requested file

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
  
def run():
  print('http fileserver is starting...')

  #ip and port of servr
  #by default http server port is 80
  #server_address = ('198.199.105.122', 8080)
  server_address = ('172.31.26.65', 8080)
  httpd = HTTPServer(server_address, KodeFunHTTPRequestHandler)
  print('http server is running...')
  httpd.serve_forever()
  
if __name__ == '__main__':
  run()
