<?php
	
	define("SECRET", "UnFkFzLqOwNtXkS7KpFa2tTzSuFaUuNf");
	
	$mysql_host="localhost";
	$mysql_dbname="psychapp";
	$mysql_username="XPD1UqQay8jZ5FDQmGbHjQ==";
	$mysql_password="2FugMAyqfl9AxUnmB2hArfpDVaGAm9ZRusre7/G3lEM=";
    $mysql_secret="HappyBrainApp";
	
	function authenticateUserLogon($username)
	{	
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT Password, DisplayName FROM Users WHERE Username= ? and Active= 1");
			$query->execute([$username]);
			return $query->fetch();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function dbConnect()
	{
		// Create Connection to DB Server
		$conn = new PDO("mysql:host={$GLOBALS['mysql_host']};dbname={$GLOBALS['mysql_dbname']}", openssl_decrypt($GLOBALS['mysql_username'],"AES-256-CBC",$GLOBALS['mysql_secret']), openssl_decrypt($GLOBALS['mysql_password'],"AES-256-CBC",$GLOBALS['mysql_secret']));
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		return $conn;
	}
	
	function deleteGroup($groupId)
	{	
		try 
		{
			$conn = dbConnect();
			// Delete Group (virtual delete)
			$query = $conn->prepare("UPDATE Groups SET Active = 0 WHERE ID = ?");
			$query->execute([$groupId]);
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function deletePageFromGroup($pageId)
	{	
		try 
		{
			$conn = dbConnect();
			// Delete Page (virtual delete)
			$query = $conn->prepare("UPDATE Pages SET Active = 0 WHERE ID = ?");
			$query->execute([$pageId]);
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function determineNextPage($pageId = -1)
	{
		$groupId = -1;
		$pageNum = -1;
		
		try 
		{
			$conn = dbConnect();
			
			if($pageId === -1)
			{
				// Determine first group
				$query = $conn->prepare("SELECT MIN(GroupID) FROM Pages WHERE Active = 1");
				$query->execute();
				$groupId = $query->fetchColumn();
				
				$pageNum = 1;
			}
			else
			{
				// Get the group ID and page number of specified page
				$query = $conn->prepare("SELECT PageNum, GroupID FROM Pages WHERE ID = ? and Active = 1");
				$query->execute([$pageId]);
				$result = $query->fetch();
				
				$groupId = $result["GroupID"];
				$pageNum = $result["PageNum"];
				
				// Get the max page number of the specified group
				$query = $conn->prepare("SELECT MAX(PageNum) FROM Pages WHERE GroupID = ? and Active = 1");
				$query->execute([$groupId]);
				$maxPageNum = $query->fetchColumn();
				
				// Determine if no more pages are available in the current group
				if($pageNum >= $maxPageNum)
				{
					$pageNum = 1;
					
					// Determine next group
					$query = $conn->prepare("SELECT MIN(GroupID) FROM Pages WHERE GroupID > ? and Active = 1");
					$query->execute([$groupId]);
					$result = $query->fetchColumn();
			
					$groupId = $result > 0 ? $result : -1;
				}
				else
				{
					$pageNum = $pageNum + 1; // Get the next page in the group
				}
			}
			if($groupId > 0)
			{
				// Get the ID of the selected page
				$query = $conn->prepare("SELECT ID FROM Pages WHERE GroupID = :groupid and PageNum = :pagenum and Active = 1");
				$query->execute(array('groupid' => $groupId, 'pagenum' => $pageNum));
				return $query->fetchColumn();
			}
			else
			{
				return -1; // The last page of the last group has been viewed
			}
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getCommentCount($pageId)
	{
		try 
		{
			$conn = dbConnect();
	
			$query = $conn->prepare("SELECT COUNT(*) as CommentCount FROM PageAssignments WHERE PageID= ? and Comment IS NOT NULL and Active= 1");
			$query->execute([$pageId]);
			return $query->fetchColumn();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getComments($pageId) 
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT DeviceID, PageID, Comment, CommentDate FROM PageAssignments WHERE PageID = ? and Unread = 1");
			$query->execute([$pageId]);
			return $query->fetchAll();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getCurrentPage($deviceId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT PageID FROM PageAssignments WHERE DeviceID = ? and Active = 1");
			$query->execute([$deviceId]);
			$result = $query->fetchColumn();
			
			return $result > 0 ? $result : -1;
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getDevice($mobileId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT ID FROM Devices WHERE MobileId = ? and Active = 1");
			$query->execute([$mobileId]);
			return $query->fetchColumn();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getGroupAndPageTitles($pageId)
	{
		try 	
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT p.Title as PageTitle, g.Title as GroupTitle FROM Pages p, Groups g WHERE p.GroupID = g.ID and p.ID = ? and p.Active = 1");
			$query->execute([$pageId]);
			return $query->fetch();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getGroupCount()
	{
		try 
		{
			$conn = dbConnect();
			return $conn->query("SELECT COUNT(*) FROM Groups WHERE Active = 1")->fetchColumn();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getGroupList() 
	{
		try 
		{
			$conn = dbConnect();
			return $conn->query("SELECT ID, Title FROM Groups WHERE Active = 1")->fetchAll();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function getGroupTitle($groupId)
	{
		try 
		{
			$conn = dbConnect();
			// Get the group title
			$query = $conn->prepare("SELECT Title FROM Groups WHERE ID = ? and Active = 1");
			$query->execute([$groupId]);
			return $query->fetchColumn();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getNextPage($groupId)
	{
		$groupId = -1;
		$pageNum = -1;
		
		try 
		{
			$conn = dbConnect();
			
			// Determine max page number in group
			$query = $conn->prepare("SELECT IFNULL(MAX(PageNum), 0) FROM Pages WHERE GroupID = ? and Active = 1");
			$query->execute([$groupId]);
			return $query->fetchColumn() + 1;
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getNextPageNum($groupId)
	{
		try 
		{
			// Determine max page number in group
			$conn = dbConnect();
			// Get list of pages assigned to group
			$query = $conn->prepare("SELECT IFNULL(MAX(PageNum), 0) FROM Pages WHERE GroupID = ? and Active = 1");
			$query->execute([$groupId]);
			return $query->fetchColumn() + 1;
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getPageCount($groupId)
	{
		try 
		{
			$conn = dbConnect();
	
			$query = $conn->prepare("SELECT COUNT(*) as pageCount FROM Pages WHERE GroupID= ? and Active= 1");
			$query->execute([$groupId]);
			return $query->fetchColumn();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getPageInfo($pageId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT p.ID, p.PageNum, p.Title, p.Points, p.Message, p.Teaser, p.Reflect, p.URL, p.Filename, p.GroupID, p.ShareID FROM Pages p, Groups g WHERE p.GroupID = g.ID and p.ID = ? and p.Active = 1");
			$query->execute([$pageId]);
			return $query->fetch();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function getPageList($groupId) 
	{
		try 
		{
			$conn = dbConnect();
			// Get list of pages assigned to group
			$query = $conn->prepare("SELECT ID, PageNum, Title, Reflect FROM Pages WHERE GroupID = ? and Active = 1 ORDER BY PageNum");
			$query->execute([$groupId]);
			return $query->fetchAll();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function getSharedPage($shareId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT g.Title as GroupTitle, p.PageNum, p.Title as PageTitle, p.Points, p.Message, p.URL, p.Filename FROM Pages p, Groups g WHERE p.GroupID = g.ID and p.ShareID = ? and p.Active = 1");
			$query->execute([$shareId]);
			return $query->fetch();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function insertComment($deviceId, $pageId, $comment)
	{
		try 
		{
			$conn = dbConnect();
			
			$query = $conn->prepare("UPDATE PageAssignments SET Comment = :comment, CommentDate = now() WHERE DeviceID = :deviceid and PageID = :pageid");
			$query->execute(array('comment' => $comment, 'deviceid' => $deviceId, 'pageid' => $pageId));
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function insertNewDevice($mobileId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("INSERT INTO Devices (MobileID) VALUES (?)");
			$query->execute([$mobileId]);
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function insertNewGroup($title)
	{
		try 
		{
			$conn = dbConnect();
			// Create Query
			$query = $conn->prepare("INSERT INTO Groups (Title, Active) VALUES (:title, :active)");
			$query->execute(array('title' => $title, 'active' => 1));
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		} 
	}
	
	function insertNewPage($nextPageNum, $title, $points, $message, $teaser, $reflect, $url, $filename, $groupId, $shareId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("INSERT INTO Pages (PageNum, Title, Points, Message, Teaser, Reflect, URL, Filename, GroupID, ShareID, Active) VALUES (:pagenum, :title, :points, :message, :teaser, :reflect, :url, :filename, :groupid, :shareid, :active)");
			$query->execute(array('pagenum' => $nextPageNum, 'title' => $title, 'points' => $points, 'message' => $message, 'teaser' => $teaser, 'reflect' => $reflect, 'url' => $url, 'filename' => $filename, 'groupid' => $groupId, 'shareid' => $shareId, 'active' => 1));
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}
	}
	
	function insertNewPageAssignment($deviceId, $pageId)
	{
		try 
		{
			$conn = dbConnect();
			
			$query = $conn->prepare("UPDATE PageAssignments SET Active = 0 WHERE DeviceID = ?");
			$query->execute([$deviceId]);
			
			$query = $conn->prepare("INSERT INTO PageAssignments (DeviceID, PageID) VALUES (:deviceid, :pageid)");
			$query->execute(array('deviceid' => $deviceId, 'pageid' => $pageId));
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function doPageSwap($pageOneId, $pageOneNum, $pageTwoId, $pageTwoNum)
	{
		//echo $pageOneId . "<br />" . $pageOneNum . "<br />" . $pageTwoId . "<br />" . $pageTwoNum;
		try 
		{
			$conn = dbConnect();
			
			$query = $conn->prepare("UPDATE Pages SET PageNum = :pagenum WHERE ID = :pageid");
			
			$query->execute(array('pagenum' => $pageOneNum, 'pageid' => $pageTwoId));
			$query->execute(array('pagenum' => $pageTwoNum, 'pageid' => $pageOneId));
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function updateExistingPage($pageId, $title, $points, $message, $teaser, $reflect, $url, $filename = null)
	{
		try 
		{
			$conn = dbConnect();
			if($filename != null)
			{
				// Update the filename information
				$query = $conn->prepare("UPDATE Pages SET Title = :title, Points = :points, Message = :message, Teaser = :teaser, Reflect = :reflect, URL = :url, Filename = :filename WHERE ID = :pageid");
				$query->execute(array('title' => $title, 'points' => $points, 'message' => $message, 'teaser' => $teaser, 'reflect' => $reflect, 'url' => $url, 'filename' => $filename, 'pageid' => $pageId));
			}
			else 
			{
				// Do not update the filename information
				$query = $conn->prepare("UPDATE Pages SET Title = :title, Points = :points, Message = :message, Teaser = :teaser, Reflect = :reflect, URL = :url WHERE ID = :pageid");
				$query->execute(array('title' => $title, 'points' => $points, 'message' => $message, 'teaser' => $teaser, 'reflect' => $reflect, 'url' => $url, 'pageid' => $pageId));
			}
			
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	function updateGroup($groupId, $title)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("UPDATE Groups SET Title = :title WHERE ID = :groupid");
			$query->execute(array('title' => $title, 'groupid' => $groupId));
			return $query->rowCount();
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
	
	/**
	 * Verify that the specified mobile ID is unique.
	 * 
	 * Parameters:
	 *     mobileId - The mobile ID to test.
	 *
	 * Return:
	 *     true - The mobile ID is unique.
	 *     false - The mobile ID is not unique.  
	 */
	function verifyUniqueDevice($mobileId)
	{
		try 
		{
			$conn = dbConnect();
			$query = $conn->prepare("SELECT COUNT(*) as DeviceCount FROM Devices WHERE MobileID = ? and Active = 1");
			$query->execute([$mobileId]);
			return $query->fetchColumn() > 0 ? false : true;
		}
		catch(PDOException $e)
		{
			if(DEBUG)
			{
				echo "Database Connection Failed: " . $e->getMessage();
			}
			return null;
		}  
	}
?>