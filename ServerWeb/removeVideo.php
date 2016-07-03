<?php

	/*  Creation Date : 15/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Remove video script
	*/
	
	/* *** Remove video BDD ** */

	$general_folder = 'videosToStore';
	
	function directory_isEmpty($directory) {
		$dirItems = count(scandir($directory));
		if($dirItems > 2) return false;
		else return true;
	}
	
	function removeFile($inputTeacherID, $fileName){
		global $general_folder;
		if (file_exists($general_folder.'/'.$inputTeacherID.'/'.$fileName))	unlink($general_folder.'/'.$inputTeacherID.'/'.$fileName);	// Suppression du fichier
		else	return 0;
		//Delete folder if it is empty
		if(directory_isEmpty($general_folder.'/'.$inputTeacherID))	rmdir($general_folder.'/'.$inputTeacherID);
		if(directory_isEmpty($general_folder))	rmdir($general_folder);
		return 1;
	}
	
	$reponse = array();	// Response to send to Android side
	
	if (isset($_POST['teacherID']) && isset($_POST['titleVideo'])) {
		/* *** BDD connection *** */
		$mysqli = mysqli_connect("localhost", "root", "", "video");

		/* *** Connection verification *** */
		if (mysqli_connect_errno()) {
			printf("Erreur connexion: %s\n", mysqli_connect_error());
			exit();
		}
		else{
			$inputTeacherID = (int)($_POST['teacherID']);
			$inputTitleVideo = $_POST['titleVideo'];
			
			if($inputTeacherID == -1){
				$response["success"] = 0;
				$response["message"] = "Identifiant de l'utilisateur indéfini";
			}
			else{
				$removeQuery = "DELETE FROM Movies WHERE teacherID=".$inputTeacherID." AND title='".$inputTitleVideo."'";
				if(removeFile($inputTeacherID, $inputTitleVideo) == 1){
					if (mysqli_query($mysqli, $removeQuery))	$response["success"] = 1;	
					else{
						$response["success"] = 0;
						$response["message"] = "Une erreur est survenue";
					} 
				}
				else{
					$response["success"] = 0;
					$response["message"] = "Fichier non présent sur le serveur";
				}	
			}
			
			echo json_encode($response);
		}

		/* Connection closed */
		mysqli_close($mysqli);
	}
	else{
		// Field(s) missing
		$reponse["success"] = 0;
		$reponse["message"] = "Champ(s) non envoyés de la part d'android";
	 
		// Display JSON response
		echo json_encode($reponse);
	}
?>