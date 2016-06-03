<?php
	$username = "root";
	$password = "";
	$hostname = "localhost";
	$conn = mysql_connect($hostname, $username, $password) or die("Unable to connect to MySQL"); 
	$select = mysql_select_db("cs184", $conn) or die("Could not select CS184");

	$cnt = mysql_query("SELECT COUNT(*) FROM users WHERE username = 'root'");  
	$result = mysql_query("SELECT * FROM users WHERE username = 'root'");

	/*** begin our session ***/
	session_start();

	/*** set a form token ***/
	$form_token = md5( uniqid('auth', true) );

	/*** set the session form token ***/
	$_SESSION['form_token'] = $form_token;
?>

<html>
	<head>
		<title>NMP Login</title>
	</head>
	<body>
		<h2>LOGIN</h2>
		<form action="connectivity.php" method="post">
			<fieldset>
				<p>
					<label for="NMP_username">Username</label>
					<input type="text" id="NMP_username" name="NMP_username" value="" maxlength="255" />
				</p>
				<p>
					<label for="NMP_password">Password</label>
					<input type="password" id="NMP_password" name="NMP_password" value="" maxlength="255" />
				</p>
				<p>
					<input type="hidden" name="form_token" value="<?php echo $form_token; ?>" />
					<input type="submit" name="submit"  value="&rarr; Login" />
				</p>
			</fieldset>
		</form>
	</body>
</html>
