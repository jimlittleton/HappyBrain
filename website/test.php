<?php
echo uniqid("UNF_");

//$mobileId = "UNF_0123456789";

//echo json_encode($mobileId);

/* echo 'file_uploads: '. ini_get('file_uploads'). '<br />'; 
echo 'upload_tmp_dir: '. ini_get('upload_tmp_dir'). '<br />'; 
echo 'upload_max_filesize: '. ini_get('upload_max_filesize'). '<br />'; 
echo 'max_file_uploads: '. ini_get('max_file_uploads'). '<br />'; 
 */

//require 'common.php';
/* define("DEBUG", False);



    try {
		$conn = new PDO("mysql:host={$mysql_host};dbname={$mysql_dbname}", openssl_decrypt($mysql_username,"AES-256-CBC",$mysql_secret), openssl_decrypt($mysql_password,"AES-256-CBC",$mysql_secret));
		// set the PDO error mode to exception
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		echo "Connected successfully"; 
		$stmt = $conn->prepare("SELECT DisplayName FROM Users"); 
		$stmt->execute();

		// set the resulting array to associative
		$result = $stmt->setFetchMode(PDO::FETCH_ASSOC); 
		foreach(new TableRows(new RecursiveArrayIterator($stmt->fetchAll())) as $k=>$v) { 
			echo $v;
		}
    }
    catch(PDOException $e)
    {
        echo "Connection failed: " . $e->getMessage();
    }
 */
 //phpinfo();
?>
