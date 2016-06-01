#! /usr/bin/python


import datetime, sys, os, time, random, subprocess

logfile = '/home/ec2-user/cs183/NMP/client/client_log.txt'                      ##Name of the file to be created to store information about the client
   
date = datetime.date.today()                                                    ##Grabs the date in YYYY-MM-DD format
clienttime = time.strftime("%I:%M:%S")                                          ##Grabs the time on the computer in HH:MM:SS

status = 'GOOD'

command = "mpstat | awk \'{print $12}\'"                                        ##Runs mpstat command to get CPU load and grabs from the Idle column
cpu_load = subprocess.check_output(command, shell=True)
cpu_load = cpu_load[8:len(cpu_load)-1]                                          ##Formats the string
cpu_load = int(100.00 - float(cpu_load))                                        ##Since we got the percentage of idle CPU, we subtract that from 100 to get the amount in use

if cpu_load > 80:
    comp_temp = random.randint(75, 112)                                         ##Computer Temperature - Can't get the temperature of a virtual machine so we use a random number generator to try and simulate it
elif cpu_load <= 80 and cpu_load >40:                                           ##Semi-bases the number on computer load
    comp_temp = random.randint(40, 85)
else:
    comp_temp = random.randint(15, 39)

net_load = 0                                                                    ##Not implemented yet

cpumessage = ""
tempmessage = ""

##--TEST VALUES--                                                               ##Comment out all values except for 1 for each variable for testing. Or just use cpu_load since
                                                                                ##temperature is semi-based on load
#cpu_load = 15
#cpu_load = 50
#cpu_load = 90
#comp_temp = 15
#comp_temp = 50
#comp_temp = 90

if cpu_load > 80 or comp_temp > 80:                                             ##If CPU LOAD is above 80% or TEMPERATURE is above 80C then we are in a CRITICAL state
    status = "CRITICAL"                                                         ##The status of the machine is set to CRITICAL and warning messages are put into the client_log
    if cpu_load > 80:
        cpumessage = "CPU LOAD at or nearing maximum."
    if comp_temp > 80:
        tempmessage = "INTERNAL TEMPERATURE too high."
    client_log = cpumessage + " " + tempmessage
        
elif (cpu_load <= 80 or comp_temp <= 80) and (cpu_load > 20 or comp_temp > 40): ##If CPU LOAD is lower than 80% but above 20% or TEMPERATURE is below 80C but above 40C then we are in a WARNING state
    status = "WARNING"                                                          ##The status of the machine is set to WARNING and warning messages are put into the client_log
    if cpu_load > 20:
        cpumessage = "CPU LOAD approaching maximum."
    if comp_temp > 40:
        tempmessage = "INTERNAL TEMPERATURE approaching maximum threshold."
    client_log = cpumessage + " " + tempmessage
        
elif cpu_load <= 20 and comp_temp <= 40:                                        ##If both CPU LOAD and TEMPERATURE are below 40% and 20C respectively, then we are in a GOOD state
    status = "GOOD"                                                             ##The status of the machine is set to GOOD and a message is put into the client_log
    client_log = "CPU LOAD and INTERNAL TEMPERATURE well below maximum thresholds."


formatted = str(date) + ' ' + str(clienttime) + ',' + str(status) + ',' + str(cpu_load) + ',' + str(comp_temp) + ',' + str(net_load) + ',' + client_log + '\n'      ##Formats everything into a comma seperated list

with open(logfile, 'a') as myfile:                                              ##Put the comma seperated data into a file called client_log.txt. Will make one if one does not exist.
    myfile.write(formatted)