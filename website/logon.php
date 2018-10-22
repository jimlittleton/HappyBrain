<?php
	require 'common.php';

	/**
	 * Displays the logon form with the specified message.
	 *
	 * message - The message to display.
	 */
	function displayLogonForm($message="")
	{
		session_unset();
		session_destroy();
		
		displayPageHeader(false);
		echo "		<h2>Welcome to Happy Brain App!</h2>\n";
		echo "		<form action='" . HBA_URL . "logon.php' method='post'>\n";
		echo "			<table>\n";
		echo "				<tr><td style='color:red'>" . $message . "</td></tr>\n";
		echo "				<tr><td>Enter your username and password to log onto the system.</td></tr>\n";
		echo "					<tr><td>Username: <input type='text' id='username' name='username' size='25' required /><br /><br />\n";
		echo "					Password: <input type='password' id='password' name='password' size='25' required />\n";
		echo "					<input type='hidden' id='action' name='action' value='login' /></td></tr>\n";
		echo "					<tr><td style='text-align: right;'><p><input type='reset' name='Clear' value='Clear'>\n";
		echo "					<input type='Submit' name='Login' value='Login'></td></tr>\n";
		echo "			</table>\n";
		echo "		</form>\n";
		displayPageFooter();
	}
	
	/**
	 * Validates the credentials provided by the logon form.
	 *
	 * username - The username provided by the user.
	 * password - The password provided by the user.
	 */
	function authenticateUser($username, $password)
	{
		$result = authenticateUserLogon($username);
		
		if($result["Password"] != "")
		{
			if(password_verify($password, $result["Password"]))
			{
				session_start();
				$_SESSION["displayname"] = $result["DisplayName"];
				header("Location: ./groups.php");
			}
			else
			{
				displayLogonForm("Invalid Password! Please Try Again.");
			}
		}
		else
		{
			displayLogonForm("Invalid Username! Please Try Again.");
		}
	}
	
	if($_SERVER['REQUEST_METHOD'] === 'POST')
	{
		if(isset($_POST['username']) && isset($_POST['password']))
		{
			authenticateUser($_POST['username'], $_POST['password']);
		}
		else
		{
			displayLogonForm("An Unexpected Error Occurred! Please Try Again.");
		}
	}
	else 
	{
		displayLogonForm();
	}
?>