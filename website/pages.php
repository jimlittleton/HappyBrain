<?php
require 'common.php';

if($_SERVER['REQUEST_METHOD'] === 'POST')
{
	if(isset($_POST['action']))
	{
		if($_POST['action'] === 'add' && isset($_POST['title']) && isset($_POST['message']) && isset($_POST['groupid']) )
		{
			$points = $_POST['point1'] . "|" . $_POST['point2'] . "|" . $_POST['point3'];
			createPage($_POST['title'], $points, $_POST['message'], $_POST['teaser'], $_POST['reflect'], $_POST['url'], $_FILES['upload'], $_POST['groupid']);
		}
		if($_POST['action'] === 'update')
		{
			$points = $_POST['point1'] . "|" . $_POST['point2'] . "|" . $_POST['point3'];
			updatePage($_POST['pageid'], $_POST['title'], $points, $_POST['message'], $_POST['teaser'], $_POST['reflect'], $_POST['url'], $_FILES);
		}
	}
	else
	{
		displayLogonForm("An Unexpected Error Occurred! Please Try Again.");
	}
}
else if(isset($_GET['form']))
{
	if($_GET['form'] === 'add' && isset($_GET['id']) && isset($_GET['title'])) 
	{
		displayAddPageForm($_GET['id'], $_GET['title']);
	}
	else if($_GET['form'] === 'edit' && isset($_GET['id']))
	{
		displayEditPageForm($_GET['id']);
	}
}
else if(isset($_GET['action']) && isset($_GET['pageid']) && isset($_GET['groupid']))
{
	if($_GET['action'] === 'delete')
	{
		$count = deletePageFromGroup($_GET['pageid']);
		header("Location: ./groups.php?form=edit&id=" . $_GET['groupid'] . "&message=Deleted " . $count . " Page(s)");
	}
}
else if(isset($_GET['action']) && isset($_GET['pageid']))
{
	if($_GET['action'] == 'view_comments')
	{
		displayPageComments($_GET['pageid']);
	}
}
else 
{
	//displayPageForm();
	echo "Unexpected Request! Contact the Administrator.";
}

/**
 * Displays the add page form.
 */
function displayAddPageForm($id, $title)
{
	displayPageHeader(true);
	echo "		<h2>Happy Brain App!</h2>\n";
	echo "		<h2>Add Page to Group: " . $title . "</h2>\n";
	echo "		<form action='" . HBA_URL . "pages.php' method='post' enctype='multipart/form-data' onsubmit='return (verifyFileUpload() && updateCheckboxValue())'>\n";
	echo "			<table>\n";
	echo "				<tr>\n";
	echo "					<td>Enter the requested information to add a page to the group.</td>\n";
	echo "					<td style='text-align: center;' rowspan=6>\n";
	echo "						<img id='preview' src='" . HBA_URL . "images/upload.png' heigth='225' width='150' /><br />\n";
	echo "						<input type='button' id='uploadbutton' value='Select Image' style'display: block;' onclick='document.getElementById(\"upload\").click()' />\n";
	echo "						<input type='file' id='upload' name='upload' style='display: none;' accept='.jpg, .png' required />\n";
	echo "					</td>\n";
	echo "				</tr>\n";
	echo "				<tr><td>Title: <input type='text' name='title' required /></td></tr>\n";
	echo "				<tr><td>Reflection Page: <input type='radio' id='reflectionYes' name='reflection' value='Yes' />Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' id='reflectionNo' name='reflection' value='No' checked />No</td></tr>\n";
	echo "				<tr><td>Bullet Point 1: <input type='text' name='point1' size='50' /></td></tr>\n";
	echo "				<tr><td>Bullet Point 2: <input type='text' name='point2' size='50' /></td></tr>\n";
	echo "				<tr><td>Bullet Point 3: <input type='text' name='point3' size='50' /></td></tr>\n";
	echo "				<tr><td><label style='vertical-align: top;' for='message'>Message: </label><textarea id='message' name='message' rows='5' cols='56' required></textarea></td></tr>\n";
	echo "				<tr><td>Teaser: &nbsp;<input type='text' name='teaser' required size='56' /></td></tr>\n";
	echo "				<tr><td>URL: <input type='text' name='url' required size='58' /></td></tr>\n";
	echo "				<tr><td style='text-align: right;'>\n";
	echo "					<p><input type='reset' name='Clear' value='Clear'>\n";
	echo "					<input type='Submit' name='addpage' value='Add Page' /></p>\n";
	echo "				</td></tr>\n";
	echo "				<tr><td>\n";
	echo "					<input type='hidden' name='action' value='add' />\n";
	echo "					<input type='hidden' name='groupid' value='" . $id . "' /><br /><br />\n";
	echo "				    <input type='hidden' id='reflect' name='reflect' value = '0' />\n";
	echo "				</td></tr>\n";
	echo "			</table>\n";
	echo "		</form>\n";
	echo "		<script src='./pages.js'></script>\n";
	displayPageFooter();
}

/**
 * Displays the selected page for editing.
 *
 * id - The ID of the group.
 * title - The title of the group.
 * pageID - The ID of the page.
 */
function displayEditPageForm($pageId)
{
	$db = getPageInfo($pageId);
	$bps = explode("|", $db["Points"]);
	
	displayPageHeader(true);
	echo "		<h2>Happy Brain App!</h2>\n";
	echo "		<div id='pageTitle'><h3>Edit Page: " . $db["Title"] . " <a href='javascript:editPageTitle()'>[Edit]</a></h3></div>\n";
	echo "		<form action='" . HBA_URL . "pages.php' method='post' enctype='multipart/form-data' onsubmit='return updateReflectionValue();'>\n";
	echo "			<table>\n";
	echo "				<tr>\n";
	echo "					<td>Update the current page information.</td>\n";
	echo "					<td style='text-align: center;' rowspan=6>\n";
	echo "						<img id='preview' src='" . HBA_URL . IMAGE_DIR . $db["Filename"] . "' heigth='225' width='150' /><br />\n";
	echo "						<input type='button' id='uploadbutton' value='Change Image' style'display: block;' onclick='document.getElementById(\"upload\").click()' />\n";
	echo "						<input type='file' id='upload' name='upload' style='display: none;' accept='.jpg, .png' />\n";
	echo "					</td>\n";
	echo "				</tr>\n";
	echo "				<tr><td>Reflection Page: <input type='radio' id='reflectionYes' name='reflection' value='Yes' ";
	if($db["Reflect"] == 1)
	{
		echo "checked";
	}
	echo " />Yes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' id='reflectionNo' name='reflection' value='No' ";
	if($db["Reflect"] == 0)
	{
		echo "checked";
	}
	echo " />No</td></tr>\n";
	echo "				<tr><td>Bullet Point 1: <input type='text' id='point1' name='point1' size='50' value='" . $bps[0] . "' /></td></tr>\n";
	echo "				<tr><td>Bullet Point 2: <input type='text' id='point2' name='point2' size='50' value='" . $bps[1] . "' /></td></tr>\n";
	echo "				<tr><td>Bullet Point 3: <input type='text' id='point3' name='point3' size='50' value='" . $bps[2] . "' /></td></tr>\n";
	echo "				<tr><td><label style='vertical-align: top;' for='message'>Message: </label><textarea id='message' name='message' rows='5' cols='56' required>" . $db["Message"] . "</textarea></td></tr>\n";
	echo "				<tr><td>Teaser: &nbsp;<input type='text' name='teaser' required size='56' value='" . $db["Teaser"] . "' /></td></tr>\n";
	echo "				<tr><td>URL: <input type='text' name='url' required size='58' value='" . $db["URL"] . "' /></td></tr>\n";
	echo "				<tr><td style='text-align: right;'>\n";
	echo "					<input type='button' onclick='javascript:cancelEditPage(" . $db["GroupID"] . ")' value='Cancel' />\n";
	echo "					<input type='reset' name='Clear' value='Clear' />\n";
	echo "					<input type='submit' name='savepage' value='Save Page' />\n";
	echo "				</td></tr>\n";
	echo "				<tr><td>\n";
	echo "					<input type='hidden' id='action' name='action' value='update' />\n";
	echo "					<input type='hidden' id='pageid' name='pageid' value='" . $db["ID"] . "' />\n";
	echo "					<input type='hidden' id='title' name='title' value='" . $db["Title"] . "' />\n";
	echo "					<input type='hidden' id='groupid' name='groupid' value='" . $db["GroupID"] . "' /><br /><br />\n";
	echo "				    <input type='hidden' id='reflect' name='reflect' value = '". $db["Reflect"] . "' />\n";
	echo "				    <input type='hidden' id='fileSelected' value = 'false' />\n";
	echo "                  \n";
	echo "				</td></tr>\n";
	echo "			</table>\n";
	echo "		</form>\n";
	echo "		<script src='./pages.js'></script>\n";
	displayPageFooter();
} 

function displayPageComments($pageId)
{
	$titles = getGroupAndPageTitles($pageId);
	$comments = getComments($pageId);
	
	displayPageHeader(true);
	echo "		<h2>Happy Brain App!</h2>\n";
	echo "      Displaying Comments for<br /><br />\n";
	echo "      Group: " . $titles['GroupTitle'] . "<br />\n";
	echo "      Page: " . $titles['PageTitle'] . "<br /><br />\n";
	
	echo "      <button type='button' onclick='javascript:markCommentsAsRead(\"" . $pageId . "\")'>Mark All Read</button><br /><br />\n";
	
	if(count($comments) > 0)
	{
		foreach ($comments as $row) 
		{
			echo "Submitted On: " . date('m/d/Y H:i:s', strtotime($row['CommentDate'])) . "<br />\n";
			echo "<textarea style='overflow-y: scroll;' rows='4' cols='100'>" . $row['Comment'] . "</textarea><br /><br />\n";
		}
	}
	displayPageFooter();
}

function updatePage($pageId, $title, $points, $message, $teaser, $reflect, $url, $file)
{
	if(count($_FILES) > 0)
	{
		$filename = uploadFile($file['upload']);
		// Update the filename information
		updateExistingPage($pageId, $title, $points, $message, $teaser, $reflect, $url, $filename);
	}
	else 
	{
		// Do not update the filename information
		updateExistingPage($pageId, $title, $points, $message, $teaser, $reflect, $url);
	}
	
	header("Location: ./groups.php?form=edit&id=" . $_POST['groupid']);
}

/**
 * Creates a unique filename for the selected file to upload.
 *
 * file - The file to upload.
 *
 * Returns the name of the unique file.
 */
function uploadFile($file)
{
	$ext = ".".end((explode(".", $file['name'])));
	$targetfile = uniqid().$ext;

	if (move_uploaded_file($file['tmp_name'], IMAGE_DIR.$targetfile)) 
	{
		return $targetfile;
	} 
	else 
	{ 
		return null;
	}
}

/**
 * Creates a new page using the information provided by the Add Page form.
 *
 * title - The title to use for the new page.
 * points - The bullet points for the new page (delimited with the "|" character).
 * message - The message of the new page.
 * file - The file to upload.
 * groupId - The ID of the group to which the new page is added.
 */
function createPage($title, $points, $message, $teaser, $reflect, $url, $file, $groupId)
{
	$filename = uploadFile($file);
	
	if($filename != null) 
	{
		$nextPageNum = getNextPageNum($groupId);
		$shareId = uniqid('', true);
		$result = insertNewPage($nextPageNum, $title, $points, $message, $teaser, $reflect, $url, $filename, $groupId, $shareId);
			
		if($result == 1)
		{
			header("Location: ./groups.php?form=edit&id=". $groupId);
		}
	}
	else
	{
		header("Location: ./groups.php?form=edit&id=" . $groupId . "&message=An Error Occurred While Attempting to Add a Page to the Group!");
	}
}
?>