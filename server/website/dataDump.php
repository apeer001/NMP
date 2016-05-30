<h1>NMP NETWORK DATA</h1> 
<pre> 
	<?php 
		// Connect to the MySQL db 
		$username = "root";
		$password = "";
		$hostname = "localhost";
		$conn = mysql_connect($hostname, $username, $password) or die("Unable to connect to MySQL"); 
		echo "<br>Connected to MySQL<br>";
		$select = mysql_select_db("cs184", $conn) or die("Could not select CS184");
		echo "Selected CS184<br>";

		$result = mysql_query("SELECT * FROM network_data");
		while($row = mysql_fetch_array($result)){
				echo '';
				foreach($row as $field){
						echo htmlspecialchars($field);
				}
				echo '<br>';
		}
		mysql_free_result($result); 
		mysql_close($conn);
	?> 
</pre>
