#!/bin/sh
sudo mysql < initDatabase.txt
sudo mysql < createTables.txt
sudo mysql < fillTables.txt
