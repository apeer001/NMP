<h1>NMP NETWORK DATA</h1> 
<pre> 
	<?php 
		// Connect to the MySQL db 
		$conn = mysql_connect('localhost', 'root', '') or die(mysql_error()); 
		// Select the cs183 database 
		mysql_select_db("cs184") or die(mysql_error()); 
		// SQL query 
		$result = mysql_query("select * from users;") or 
		die(mysql_error()); 
		// Display the result 
		while ($row = mysql_fetch_array($results)) {
				printf("%s", $row["username"]); 
		}
		// Free up resources 
		mysql_free_result($result); 
		mysql_close($conn); 
	?> 
</pre>
