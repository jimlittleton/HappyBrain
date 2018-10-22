var URL = "http://www.happybrainapp.com/";

function addGroup() 
{
	window.location.href = URL + "groups.php?form=add";
    return false;
}

function addPage(groupId, title)
{
	window.location.href = URL + "pages.php?form=add&id=" + groupId + "&title=" + title;
    return false;
}

function cancelEditGroup()
{
	if (confirm("Are You Sure You Want to Cancel the Group Edits?")) 
	{
		window.location.href = URL + "groups.php";
		return true;
    }
    else
	{
        return false;
    }
}

function cancelEditPage(groupId)
{
	if (confirm("Are You Sure You Want to Cancel the Page Edits?")) 
	{
		window.location.href = URL + "groups.php?form=edit&id=" + groupId;
		return true;
    }
    else
	{
        return false;
    }
}

function confirmGroupDelete(groupId)
{
	if (confirm("Are You Sure You Want to Delete the Selected Group?\nThis Operation Cannot be Undone!")) 
	{
		window.location.href = URL + "groups.php?action=delete&id=" + groupId;
        return true;
    }
    else
	{
        return false;
    }
}

function confirmPageDelete(pageId, groupId)
{
	if (confirm("Are You Sure You Want to Delete the Selected Page?\nThis Operation Cannot be Undone!")) 
	{
		window.location.href = URL + "pages.php?action=delete&pageid=" + pageId + "&groupid=" + groupId;
        return true;
    }
    else
	{
        return false;
    }
}

function confirmLogout()
{
    if (confirm("Are You Sure You Want to Logout of Happy Brain App?")) 
	{
		window.location.href = URL + "logon.php";
        return true;
    }
    else
	{
        return false;
    }
}

function movePageDown(posNum)
{ 
	var pos = parseInt(posNum) + 1;
	var id1 = document.getElementById("page_id" + posNum).value;
	var id2 = document.getElementById("page_id" + pos).value;
	var pg1 = document.getElementById("page_num" + posNum).value;
	var pg2 = document.getElementById("page_num" + pos).value;
	alert("ID1: " + id1 + "\nID2: " + id2 + "\nPG1: " + pg1 + " \nPG2: " + pg2);
}

function movePageUp(posNum)
{
	alert("World");
	
}

function updatePageTitle()
{
	var title = document.getElementById("editTitle").value;
	
	if(title.length > 0)
	{
		document.getElementById("title").value = title;
		var htmlcode = "<h2>Edit Page: " + title + " <a href='javascript:editPageTitle()'>[Edit]</a></h2>";
		document.getElementById("pageTitle").innerHTML = htmlcode;
	}
	else
	{
		alert("Input Error: The Page Title Cannot be Blank!");
		return false;
	}
}

function editGroup(groupId, title)
{
	window.location.href = URL + "groups.php?form=edit&id=" + groupId + "&title=" + title;
    return true;
}

function editPage(pageId)
{
	window.location.href = URL + "pages.php?form=edit&id=" + pageId;
    return true;
}

function editGroupTitle()
{
	var htmlcode = "<h2>Edit Group: <input type='text' id='editTitle' value='" + document.getElementById("title").value + "' /> <a href='javascript:updateGroupTitle()'>[Update]</a></h2>";
	document.getElementById("groupTitle").innerHTML = htmlcode;
}

function updateGroupTitle()
{
	var title = document.getElementById("editTitle").value;
	
	if(title.length > 0)
	{
		document.getElementById("title").value = title;
		var htmlcode = "<h2>Edit Group: " + title + " <a href='javascript:editGroupTitle()'>[Edit]</a></h2>";
		document.getElementById("groupTitle").innerHTML = htmlcode;
	}
	else
	{
		alert("Input Error: The Group Title Cannot be Blank!");
		return false;
	}
}

function editPageTitle()
{
	var htmlcode = "<h2>Edit Page: <input type='text' id='editTitle' value='" + document.getElementById("title").value + "' /> <a href='javascript:updatePageTitle()'>[Update]</a></h2>";
	document.getElementById("pageTitle").innerHTML = htmlcode;
}

function manageUsers()
{
    window.location.href = URL + "users.php";
    return false;
}

function saveEditGroup(groupId)
{
	window.location.href = URL + "groups.php?action=update&id=" + groupId + "&title=" + document.getElementById("title").value;
	return true;
}

function updateReflectionValue()
{
	if(document.getElementById("reflectionYes").checked == true)
	{
		document.getElementById("reflect").value = "1";
	}
	else
	{
		document.getElementById("reflect").value = "0";
	}
	return true;
}

function viewPageComments(pageId)
{
	window.location.href = URL + "pages.php?action=view_comments&pageid=" + pageId;
    return true;
}

function verifyFileUpload()
{
	if(document.getElementById("fileSelected").value == "false")
	{
		alert("Please select a file to upload!");
		return false;
	}
	return true;
}

function viewReports()
{
    window.location.href = URL + "reports.php";
    return false;
}