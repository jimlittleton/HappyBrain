<?php
    
	require 'dbConnect.php';
	
	session_start();
	
	if(!isset($_SESSION["displayname"]) && basename($_SERVER['PHP_SELF']) != "logon.php")
	{
		header("Location: ./logon.php");
	}
	
	define("DEBUG", True);
	define("HBA_URL", "http://www.happybrainapp.com/");
	define("IMAGE_DIR", "./images/"); 
	
	function displayPageFooter()
	{
		echo "	</body>\n";
		echo "</html>\n";
	}
	
	function displayPageHeader($sessionSet)
	{
		echo "<!DOCTYPE HTML>\n";
		echo "<html>\n";
		echo "	<head>\n";
		echo "		<title>Happy Brain App</title>\n";
		echo "      <script src='./script.js?v=" . microtime(true) . "'></script>\n";
		echo "      <link rel='stylesheet' href='site.css'>\n";
		echo "	</head>\n";
		echo "	<body>\n";
		if($sessionSet)
		{
			echo "      <p>Welcome " . $_SESSION['displayname'] . " <a href='javascript:confirmLogout()'>[Logout]</a></p>\n";
		}
	}

?>
