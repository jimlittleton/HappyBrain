<?php
	require 'common.php';
	
	if($_SERVER['REQUEST_METHOD'] === 'POST')
	{
		if(isset($_POST['action']))
		{
			if($_POST['action'] === 'add' && isset($_POST['title'])) 
			{
				createGroup($_POST['title']);
			}
		}
		else
		{
			displayLogonForm("An Unexpected Error Occurred! Please Try Again.");
		}
	}
	else if(isset($_GET['form']))
	{
		if($_GET['form'] === 'add') 
		{
			displayAddGroupForm();
		}
		else if($_GET['form'] === 'edit' && isset($_GET['id']))
		{
			if(!isset($_GET['message']))
			{
				displayEditGroupForm($_GET['id']);
			}
			else 
			{
				displayEditGroupForm($_GET['id'], $_GET['message']);
			}
		}
	}
	else if(isset($_GET['action']) && isset($_GET['id']))
	{
		if($_GET['action'] === 'delete')
		{
			$count = deleteGroup($_GET['id']);
			displayGroupForm("Deleted " . $count . " Group(s)");
		}
		else if($_GET['action'] === 'update')
		{
			updateGroup($_GET['id'], $_GET['title']);
			displayGroupForm();
		}
	}
	else if(isset($_GET['action']))
	{
		if($_GET['action'] === 'move')
		{
			swapPages($_GET['group'], $_GET['id1'], $_GET['page1'], $_GET['id2'], $_GET['page2']);
		}
	}
	else 
	{
		displayGroupForm();
	}
	
	/**
	 * Displays the add group form.
	 */
	function displayAddGroupForm()
	{
		displayPageHeader(true);
		echo "		<h2>Add a Group to the Happy Brain App!</h2>\n";
		echo "		<form action='" . HBA_URL . "groups.php' method='post'>\n";
		echo "			<table>\n";
		echo "				<tr><td>Enter the requested information to create a new group.</td></tr>\n";
		echo "					<tr><td>Title: <input type='text' id='title' name='title' required /><br /><br />\n";
		echo "					<input type='hidden' id='action' name='action' value='add' /></td></tr>\n";
		echo "					<tr><td style='text-align: right;'><p><input type='reset' name='Clear' value='Clear'>\n";
		echo "					<input type='Submit' name='addgroup' value='Add Group'></p></td></tr>\n";
		echo "			</table>\n";
		echo "		</form>\n";
		displayPageFooter();
	}
	
	/**
	 * Displays the selected group for editing.
	 *
	 * groupId - The ID of the group.
	 * message - A message to display to the user.
	 */
	function displayEditGroupForm($groupId, $message="")
	{
		$title = getGroupTitle($groupId);
		 
		displayPageHeader(true);
		echo "		<h2>Happy Brain App!</h2>\n";
		echo "		<div id='groupTitle'><h3>Edit Group: " . $title . " <a href='javascript:editGroupTitle()'>[Edit]</a></h3></div>\n";
		echo "		<input type='button' onclick='javascript:addPage(\"" . $groupId . "\", \"" . $title . "\")' value='Add Page' />\n";
		echo "		<table>\n";
		echo "		    <tr>\n";
		echo "				<th>Page Title</th>\n";
		echo "				<th>Day #</th>\n";
		echo "				<th>&nbsp;&nbsp;Reflection&nbsp;&nbsp;</th>\n";
		echo "				<th>&nbsp;&nbsp;Comments&nbsp;&nbsp;</th>\n";
		echo "				<th>&nbsp;</th>\n";
		echo "				<th>Delete</th>\n";
		echo "		    </tr>\n";
		
		$result = getPageList($groupId);
		
		if(count($result) > 0)
		{
			$x = 1;
			foreach ($result as $row) 
			{
				$count = getCommentCount($row['ID']);
				$reflect = $row['Reflect'] == 1 ? "Yes" : "";
				
				echo "		    <tr>\n";
				echo "		        <td><a href='javascript:editPage(\"" . $row['ID'] . "\")'>" . $row['Title'] . "</a></td>\n";
				echo "		        <td style='text-align:center'>" . $row['PageNum'] . "</td>\n";
				echo "		        <td style='text-align:center'>" . $reflect . "</td>\n";
				if($count > 0)
				{
					echo "		        <td style='text-align:center'><a href='javascript:viewPageComments(\"" . $row['ID'] . "\")'>View " . $count . "</td>\n";
				}
				else
				{
					echo "		        <td style='text-align:center'>&nbsp;</td>\n";
				}
				echo "		        <td>&nbsp;
				<a href='javascript:movePageUp(\"" . $x . "\", \"" . $groupId . "\")'>&#9650;</a>&nbsp;&nbsp;
				<a href='javascript:movePageDown(\"" . $x . "\", \"" . $groupId . "\")'>&#9660;</a>&nbsp;&nbsp;
				</td>\n";
				echo "		        <td style='text-align:center'><a href='javascript:confirmPageDelete(\"" . $row['ID'] . "\", \"" . $groupId . "\")'>X</a></td>";
				echo "		    </tr>\n";
				echo "
				<input type='hidden' id='page_id" . $x . "' value='" . $row['ID'] . "' />
				<input type='hidden' id='page_num" . $x . "' value='" . $x++ . "' />
				\n";
			}
		}
		else
		{
			echo "		    <tr><td>No Pages Currently Exist!</td></tr>\n";
		}
		echo "		    <tr><td style='text-align: right;'>\n";
		echo "				<input type='button' onclick='javascript:cancelEditGroup()' value='Cancel' />\n";
		echo "				<input type='button' onclick='javascript:saveEditGroup(\"" . $groupId . "\")' value='Save' />\n";
		echo "		    </td></tr>\n";
		echo "		    <tr><td><input type='hidden' id='title' value='" . $title . "' /></td></tr>\n";
		echo "		</table>\n";
		displayPageFooter();
		
	} 
	
	/**
	 * Displays the group form.
	 *
	 * message - The message to display.
	 * msgType - 0 is plain message, 1 is error message.
	 */
	function displayGroupForm($message="", $msgType=0)
	{
		// ToDO: Display message on form.
		
		displayPageHeader(true);
		echo "		<h2>Happy Brain App!</h2>\n";
		echo "		<h3>Manage Groups</h3>\n";
		echo "		<input type='button' onclick='javascript:addGroup()' value='Add Group' />\n";
		echo "		<table>\n";
		
		$groupCount = getGroupCount();
		
		if($groupCount > 0)
		{
			$result = getGroupList();
			
			echo "			<tr>\n";
			echo "				<th>Group Title</th>\n";
			echo "				<th># Pages</th>\n";
			echo "				<th>Delete</th>\n";
			echo "			</tr>\n";
			
			foreach ($result as $row) {
				echo "			<tr>\n";
				echo "				<td><a href='javascript:editGroup(\"" . $row['ID'] . "\", \"" . $row['Title'] . "\")'>" . $row['Title'] . "</a></td>\n";
				$pageCount = getPageCount($row['ID']);
				echo "				<td style='text-align:center'>" . $pageCount . "</td>\n";
				echo "				<td><a href='javascript:confirmGroupDelete(\"" . $row['ID'] . "\")'>X</a></td>";
				echo "			</tr>\n";
			}
		}
		else
		{
			echo "			<tr><td>No Groups Currently Exist!</td></tr>\n";
		}
		
		echo "			<tr><td>&nbsp;</td></tr>\n";
		echo "			<tr><td colspan=3 style='text-align: center;'>\n";
		echo "				<input type='button' onclick='javascript:viewReports()' value='View Reports' disabled />\n";
		echo "				<input type='button' onclick='javascript:manageUsers()' value='Manage Users' disabled />\n";
		echo "				<input type='button' onclick='javascript:viewSettings()' value='Settings' disabled />\n";
		echo "			</td></tr>\n";
		echo "		</table>\n";
		displayPageFooter();
	}
	
	/**
	 * Creates a new group using the title provided by the Add Group form.
	 *
	 * title - The title to use for the new group.
	 */
	function createGroup($title)
	{
		$count = insertNewGroup($title);
		
		if($count == 1)
		{
			displayGroupForm("The " . $title . " Group Successfully Added!");
		}
		else
		{
			displayGroupForm("An Error Occurred While Attempting to Add the " . $title . " Group!");
		}
	}
	
	function swapPages($groupId, $pageOneId, $pageOneNum, $pageTwoId, $pageTwoNum)
	{
		//echo "PHP<br />" . $groupId . "<br />" . $pageOneId . "<br />" . $pageOneNum . "<br />" . $pageTwoId . "<br />" . $pageTwoNum;
		doPageSwap($pageOneId, $pageOneNum, $pageTwoId, $pageTwoNum);
		
		header("Location: ./groups.php?form=edit&id=" . $groupId);
	}
?>










