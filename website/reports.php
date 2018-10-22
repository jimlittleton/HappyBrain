<?php
	require 'common.php';
	
	if($_SERVER['REQUEST_METHOD'] === 'POST')
	{
		
	}
	else 
	{
		displayReportsForm();
	}
	
	function displayReportsForm()
	{
		displayPageHeader(true);
		echo "		<h2>Happy Brain App!</h2>\n";
		echo "		<h2>View Reports</h2>\n";
		displayPageFooter();
	}
?>