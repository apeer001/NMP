#!/bin/python
import ssl
import BaseHTTPServer, SimpleHTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler

class HttpHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        file_content = self.rfile.read(content_length)

        # Do what you wish with file_content
        print file_content

        # Respond with 200 OK
        self.send_response(200)
        
        #print "got POST message"
        # how do I get the file here?
        print self.request.FILES


httpd = BaseHTTPServer.HTTPServer(('localhost', 4443), HttpHandler)
httpd.socket = ssl.wrap_socket(httpd.socket, certfile='./server.pem', server_side=True)
httpd.serve_forever()    



$curl -X POST -d @../some.file https://localhost:4443/resource --insecure
