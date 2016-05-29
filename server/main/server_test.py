#! /usr/bin/python

# Echo server program
import socket
import sys
import thread
import time

HOST = None               # Symbolic name meaning all available interfaces
PORT = 12359              # Arbitrary non-privileged port
s = None

client_list = [];

def client_thread(conn):
	try:
		while True:
			conn.send("PING");
			data = conn.recv(1024);
			if data:
				print "Client says: \"" + data + "\"";
			time.sleep(60);

	except Exception, e:
		print repr(e);

	conn.close();
	return;


for res in socket.getaddrinfo(HOST, PORT, socket.AF_UNSPEC,
                              socket.SOCK_STREAM, 0, socket.AI_PASSIVE):
    af, socktype, proto, canonname, sa = res
    try:
        s = socket.socket(af, socktype, proto)
    except socket.error as msg:
        s = None
        continue
    try:
        s.bind(sa)
        s.listen(1)
    except socket.error as msg:
        s.close()
        s = None
        continue
    break
if s is None:
    print('could not open socket')
    sys.exit(1)
	
while True:
    conn, addr = s.accept();
    client_list.append(conn);
    print 'Connected with ' + addr[0] + ':' + str(addr[1])
    
    thread.start_new_thread(client_thread ,(conn,))

f.close();
s.close();