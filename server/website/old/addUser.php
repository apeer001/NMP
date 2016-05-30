<?php

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
		<h2>Add user</h2>
		<form action="adduser_submit.php" method="post">
			<fieldset>
				<p>
					<label for="NMP_username">Username</label>
					<input type="text" id="NMP_username" name="NMP_username" value="" maxlength="20" />
				</p>
				<p>
					<label for="NMP_password">Password</label>
					<input type="text" id="NMP_password" name="NMP_password" value="" maxlength="20" />
				</p>
				<p>
					<input type="hidden" name="form_token" value="<?php echo $form_token; ?>" />
					<input type="submit" value="&rarr; Login" />
				</p>
			</fieldset>
		</form>
	</body>
</html>
