<?php

define('DB_HOST', 'localhost');
define('DB_NAME', 'cs184');
define('DB_USER','root');
define('DB_PASSWORD','');

echo "HELLO"

$con=mysql_connect(DB_HOST,DB_USER,DB_PASSWORD) or die("Failed to connect to MySQL: " . mysql_error());
$db=mysql_select_db(DB_NAME,$con) or die("Failed to connect to MySQL: " . mysql_error());

/*
$ID = $_POST['user'];
$Password = $_POST['pass'];
*/
/*
$username = "root";
$password = "";
$hostname = "localhost";
$conn = mysql_connect($hostname, $username, $password) or die("Unable to connect to MySQL"); 
$select = mysql_select_db("cs184", $conn) or die("Could not select CS184");
*/
function SignIn()
{
		session_start();   //starting the session for user profile page
		if(!empty($_POST['NMP_username']))   //checking the 'user' name which is from Sign-In.html, is it empty or have some text
		{
			$query = mysql_query("SELECT *  FROM users WHERE userName = '$_POST[NMP_username]' AND password = '$_POST[NM_password]'") or die(mysql_error());
			$row = mysql_fetch_array($query) or die(mysql_error());
			if(!empty($row['username']) AND !empty($row['password']))
			{
				$_SESSION['username'] = $row['password'];
				header("Location: http://52.32.201.77/home.php");
			}
			else
			{
				header("Location: http://52.32.201.77");
			}
		}
}

SignIn();

/*
if(isset($_POST['submit']))
{
			SignIn();
}
*/
?>

