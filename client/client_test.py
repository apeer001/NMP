#! /usr/bin/python

# Echo client program
import socket
import datetime
import sys

HOST = '198.199.105.122'    # The remote host
PORT = 12359              # The same port as used by the server
s = None
for res in socket.getaddrinfo(HOST, PORT, socket.AF_UNSPEC, socket.SOCK_STREAM):
    af, socktype, proto, canonname, sa = res
    try:
        s = socket.socket(af, socktype, proto)
    except socket.error as msg:
        s = None
        continue
    try:
        s.connect(sa)
    except socket.error as msg:
        s.close()
        s = None
        continue
    break
	
if s is None:
    print('could not open socket')
    sys.exit(1)

data = "";
try:
	while True:
		data = s.recv(1024)
		if not data:
			break;
		if data == "PING":
			msg = "My current time is " + str(datetime.datetime.now());
			s.send(msg);
			print "I sent the server some shit";
		#print data;
except Exception, e:
	print repr(e);
	
s.close();

q = "";
q = input("Enter the anykey to quit: ");
