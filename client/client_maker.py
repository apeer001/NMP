#! /usr/bin/python

import datetime, sys, os, time, random, subprocess

logfile = 'client_log.txt'                                                     ##Name of the file to be created to store information about the client
   
date = datetime.date.today()                                                    ##Grabs the date in YYYY-MM-DD format
clienttime = time.strftime("%I:%M:%S")                                          ##Grabs the time on the computer in HH:MM:SS

command = "mpstat | awk \'{print $11}\'"                                        ##Runs mpstat command to get CPU load and grabs from the Idle column
cpu_load = subprocess.check_output(command, shell=True)
cpu_load = cpu_load[8:len(cpu_load)-1]                                          ##Formats the string
cpu_load = 100.00 - float(cpu_load)                                             ##Since we got the percentage of idle CPU, we subtract that from 100 to get the amount in use

if cpu_load > 80:
    comp_temp = random.randint(75, 112)                                         ##Computer Temperature - Can't get the temperature of a virtual machine so we use a random number generator to try and simulate it
elif cpu_load <= 80 and cpu_load >40:                                                                           ##Semi-bases the number on computer load
    comp_temp = random.randint(35, 85)
else:
    comp_temp = random.randint(15, 60)

net_load = 0                                                                    ##Not implemented yet

if cpu_load > 80 or comp_temp > 80:
    client_log = "STATUS: CRITICAL - CPU LOAD AT OR NEAR MAX OR TEMPERATURE TOO HIGH"
elif (cpu_load <= 80 or comp_temp <= 80) and (cpu_load > 40 or comp_temp > 20):
    client_log = "STATUS: WARNING - CPU LOAD APPROACHING MAX OR TEMPERATURE APPROACHING THRESHOLD"
elif cpu_load <= 20 and comp_temp <= 40:
    client_log = "STATUS: GOOD - CPU LOAD AND TEMPERATURE WELL BELOW MAXIMUM THRESHOLDS"


formatted = str(date) + ' ' + str(clienttime) + ',' + str(cpu_load) + ',' + str(comp_temp) + ',' + str(net_load) + ',' + client_log + '\n'

with open(logfile, 'a') as myfile:
    myfile.write(formatted)
