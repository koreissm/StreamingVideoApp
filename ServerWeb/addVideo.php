<?php
	/*  Creation Date : 15/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Script to add a video to the server and in the database
	*/

	/* *** Add vidéo to the BDD ** */
	
	// function that removes accents (used for the title of the video)
	function suppr_accents($str, $encoding='utf-8'){
		$str = htmlentities($str, ENT_NOQUOTES, $encoding);	// convert accented characters into HTML entities
	 
		// Replace HTML entities for just the first non- accented characters
		// Example : "& ecute ; " => " E ", "& EXECUTE ; " => " E", " Ã" => "a" ...
		$str = preg_replace('#&([A-za-z])(?:acute|grave|cedil|circ|orn|ring|slash|th|tilde|uml);#', '\1', $str);
	 
		// Replace ligatures such as : å , Æ ...
		// Example "Å " "=>" oe "
		$str = preg_replace('#&([A-za-z]{2})(?:lig);#', '\1', $str);
		
		$str = preg_replace('#&[^;]+;#', '', $str);	// Delete everything else
	 
		return $str;
	}

	$reponse = array();	// Response to send Android side
	
	/* *** If the login and password fields are ok *** */
	if (isset($_POST['teacherID']) && isset($_POST['titleVideo'])) {
		/* *** Connexion à la BDD *** */
		$mysqli = mysqli_connect("localhost", "root", "", "video");

		/* *** Connection verification *** */
		if (mysqli_connect_errno()) {
			printf("Erreur connexion: %s\n", mysqli_connect_error());
			exit();
		}
		else{
			$inputTeacherID = (int)($_POST['teacherID']);
			$inputTitleVideo = suppr_accents($_POST['titleVideo']);
			
			if($inputTeacherID == -1){
				$response["success"] = 0;
				$response["message"] = "Identifiant de l'utilisateur indéfini";
			}
			else{
				$addQuery = "INSERT INTO Movies (teacherID, title) VALUES ('".$inputTeacherID."', '".$inputTitleVideo."')";

				if (mysqli_query($mysqli, $addQuery))	$response["success"] = 1;
				else{
					$response["success"] = 0;
					$response["message"] = "Vidéo déjà présente dans la liste";
				}
			}
			
			echo json_encode($response);
		}

		/* Connection cloded */
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