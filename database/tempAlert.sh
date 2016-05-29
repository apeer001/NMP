#!/bin/sh
mysql -u 'root' << eof
use cs184;
select computer_temp from network_data where admin_username = 'Bob' order by time_of_update desc;
eof
