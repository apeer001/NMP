<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="refresh" content="5" />
<title>NMP</title>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/jquery-1.12.3.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript">
  	$(document).ready(function() {
    	$('#example').DataTable( {
      	"scrollY":        "200px",
        "scrollCollapse": true,
        "paging":         false
    	} );
		} );
</script>

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
		$select = mysql_select_db("cs184", $conn) or die("Could not select CS184");

		$result = mysql_query("SELECT * FROM network_data");	
					
		echo "<table id=\"example\" class=\"display\" cellspacing=\"0\" width=\"100%\">";
		echo "<thead>";
		echo " <tr> <th>admin_username</th> <th>computer_id</th> <th>time_of_update</th> <th>computer_status</th>  <th>cpu_load</th>  <th>computer_temp</th>  <th>network_load</th>  <th>status_description</th> </tr>";

		echo "</thead>";
		echo "<tbody>";
		while($row = mysql_fetch_array($result))
		{
			echo "<tr>";
			echo '<td>' . $row['admin_username'] . '</td>';
			echo "<td>" . $row['computer_id'] . "</td>";
			echo "<td>" . $row['time_of_update'] . "</td>";	
			echo "<td>" . $row['computer_status'] . "</td>";
			echo "<td>" . $row['cpu_load'] . "</td>";
			echo "<td>" . $row['computer_temp'] . "</td>";
			echo "<td>" . $row['network_load'] . "</td>";
			echo "<td>" . $row['status_description'] . "</td>";
			echo "</tr>";
		}
		echo "</tbody>";
		echo "</table>";
	
		mysql_free_result($result); 
		mysql_close($conn);
	?> 
	
</body>
</html>
