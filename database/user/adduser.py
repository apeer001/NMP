#!/usr/bin/python

# adduser.py 
# use this script to add users to the gain access
# to the main server database from android and web application

import MySQLdb

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

# add user 
def addUser(UN,PW):
    db = MySQLdb.connect(host=hostname,     # your host, usually localhost
                         user=username,     # username
                         passwd=password,   # password
                         db=database)       # name of the database

    # must create a cursor object
    cur = db.cursor()
    try:
        insert_stmt = (
            "INSERT INTO " + table_user + "(username, password, network_size) "
            "VALUES(%s, %s, %s)"
             
        )
        data = (UN,PW,0)
        cur.execute(insert_stmt, data) 
        print('Successfully added the user: ' + UNAME + ' to the database') 
        db.commit()
        db.close()
        return True
    except:
        db.rollback()
        print('Rolling back database due to error. Could not add user')
    db.close()
    return False
    

if __name__ == '__main__':
    
    UNAME = ""
    PWORD = ""
    TWORD = ""
    while 1:
        UNAME = raw_input('Enter a username: ')
        PWORD = raw_input('Enter a password: ')
        if PWORD == "quit":
            print('Quitting... Bye!')
            exit(0)

        TWORD = raw_input('Re-enter the password: ')
        if TWORD == "quit":
            print('Quitting... Bye!')
            exit(0)
        if PWORD == TWORD: 
            if addUser(UNAME, PWORD):
                exit(0)


