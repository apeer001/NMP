<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>NMP</title>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="menu-wrapper">
  <div class="menu">
    <ul>
      <li><a href="index.php" class="active">home</a></li>
      <li><a href="about.html">about</a></li>
      <li><a href="services.html">services</a></li>
      <li><a href="works.html">works</a></li>
      <li><a href="contact.html">contact</a></li>
    </ul>
  </div>
</div>
<!--- menu-wrapper div end -->
<div class="clearing"></div>
<div class="border-bottom"></div>
<div class="logo-wrapper">
  <div class="logo">
    <h1>NMP Alert viewer</h1>
  </div>

</div>
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
</body>
</html>
