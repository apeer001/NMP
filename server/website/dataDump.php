<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="refresh" content="60" />
<title>NMP</title>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/jquery-1.12.3.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/initTables.js"></script>

</head>
<body>
<div class="menu-wrapper">
  <div class="menu">
    <ul>
      <li><a href="index.php">home</a></li>
      <li><a href="dataDmp.php" class="active" >All Data</a></li>
      <!--
      <li><a href="services.html">services</a></li>
      <li><a href="works.html">works</a></li>
      <li><a href="contact.html">contact</a></li>
       -->
    </ul>
  </div>
</div>
<!--- menu-wrapper div end -->
<div class="clearing"></div>
<div class="border-bottom"></div>
<div class="logo-wrapper">
  <div class="logo">
    <h1>All Data</h1>
  </div>
</div>

<div class="tableMargin">

  <?php 
    // Connect to the MySQL db 
    $username = "root";
    $password = "";
    $hostname = "localhost";
    $conn = mysql_connect($hostname, $username, $password) or die("Unable to connect to MySQL"); 
    $select = mysql_select_db("cs184", $conn) or die("Could not select CS184");
    
    $cnt = mysql_query("SELECT COUNT(*) FROM network");  
    $result1 = mysql_query("SELECT * FROM network");  
    while($row1 = mysql_fetch_array($result1)) {
      $id = $row1['computer_id'];
      $result2 = mysql_query("SELECT * FROM network_data WHERE computer_id='" . $id . "'");  
          
      // Label for table
      echo "<div>";
      echo "<h1><b>Client Computer " . $id . "</b></h1>";
      echo "</div>";
      // Build table
      echo "<script type=\"text/javascript\" src=\"js/initTables.js\">init_table('#example" . $id ."')</script>"
      echo "<table id=\"example" . $id . "\" class=\"display\" cellspacing=\"0\" width=\"100%\" align=\"center\" >";
      echo "<thead>";
      echo " <tr> <th>admin_username</th> <th>computer_id</th> <th>time_of_update</th> <th>computer_status</th>  <th>cpu_load</th>  <th>computer_temp</th>  <th>network_load</th>  <th>status_description</th> </tr>";
      echo "</thead>";
      echo "<tbody>";
      // Insert row data
      while($row2 = mysql_fetch_array($result2))
      {
        echo "<tr>";
        echo "<td align=\"center\">" . $row2['admin_username'] . "</td>";
        echo "<td align=\"center\">" . $row2['computer_id'] . "</td>";
        echo "<td align=\"center\">" . $row2['time_of_update'] . "</td>";  
        echo "<td align=\"center\">" . $row2['computer_status'] . "</td>";
        echo "<td align=\"center\">" . $row2['cpu_load'] . "</td>";
        echo "<td align=\"center\">" . $row2['computer_temp'] . "</td>";
        echo "<td align=\"center\">" . $row2['network_load'] . "</td>";
        echo "<td align=\"center\">" . $row2['status_description'] . "</td>";
        echo "</tr>";
      }
      echo "</tbody>";
      echo "</table>";
    }
  
    mysql_free_result($result2); 
    mysql_free_result($result1);
    mysql_free_result($cnt); 
    mysql_close($conn);
  ?> 
</div>

</body>
</html>
