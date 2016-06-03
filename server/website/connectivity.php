<?php
define('DB_HOST', 'localhost');
define('DB_NAME', 'cs184');
define('DB_USER','root');
define('DB_PASSWORD','');

$con=mysql_connect(DB_HOST,DB_USER,DB_PASSWORD) or die("Failed to connect to MySQL: " . mysql_error());
$db=mysql_select_db(DB_NAME,$con) or die("Failed to connect to MySQL: " . mysql_error());

function SignIn()
{
	$username = $_POST['NMP_username'];
	$password = $_POST['NMP_password'];
	
	session_start();   //starting the session for user profile page
	if(!empty($username))   //checking the 'user' name which is from Sign-In.html, is it empty or have some text
	{
		$query = mysql_query("SELECT *  FROM users where username = '$username' AND password = '$password'") or die(mysql_error());
		$row = mysql_fetch_array($query) or die(mysql_error());
		if(isset($row))
		//if(!empty($row['username']) AND !empty($row['password']))
		{	
			$_SESSION['username'] = $row['password'];
			echo "SUCCESS!";
			header("Location:home.php");
		}
		else
		{
			echo "WRONG INFORMATION";
		}
	}
}

if(isset($_POST['submit']))
{
	echo "If you still see this, then you clearly entered the wrong information";
	SignIn();
}
?>
