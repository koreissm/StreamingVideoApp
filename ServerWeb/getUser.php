<?php

	/*  Creation Date : 15/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Script user authentication,
					  the script returns a list of videos if a teacher
                      the script returns a list of teacher if a student
                      The returns is formatted in JSON for Android can recover
					  the necessary information
	*/
	
	/* *** Get users list** */
 
	$reponse = array();	//  Response to send to Android side
	
	/* *** If the login and password fields are ok *** */
	if (isset($_POST['login']) && isset($_POST['password'])) {
		/* *** BDD connection *** */
		$mysqli = mysqli_connect("localhost", "root", "", "video");

		/* *** Connection verification *** */
		if (mysqli_connect_errno()) {
			printf("Erreur connexion: %s\n", mysqli_connect_error());
			exit();
		}
		else{
			// Recovering the login and pwd
			$inputLogin = html_entity_decode($_POST['login']);
			$inputPassword = html_entity_decode($_POST['password']);
			
			//Verify query authentication
			$resultat = $mysqli->query("SELECT * FROM User WHERE login='".$inputLogin."' AND password = '".$inputPassword."'");
			$row_cnt = $resultat->num_rows;
			
			//If result
			if($row_cnt > 0){
				while ($row = $resultat->fetch_assoc()) {
					$response["type"]	= $row['type'];
					$response["userID"] = $row['userID'];
				}
				//If professor
				if($response['type'] == "teacher"){
					//Retrieving request videos for teacher
					$resultat2 = $mysqli->query("SELECT * FROM Movies WHERE teacherID='".$response["userID"]."'");
					$row_cnt2 = $resultat2->num_rows;
					$response["videos"]	= array();
					if($row_cnt2 > 0){
						while ($row2 = $resultat2->fetch_assoc()) {
							$infos = array();
							//Recovering the title of the video
							$infos["title"] = htmlentities($row2["title"]);
							array_push($response["videos"], $infos);
						}
					}
				}
				//If student
				if($response['type'] == "student"){
					//Retrieve query faculty where the student is enrolled in their group
					$resultat2 = $mysqli->query("SELECT * FROM user WHERE userID IN (SELECT teacherID FROM user u, typegroup g WHERE u.userID = g.studentID AND g.studentID = '".$response["userID"]."')");
					$row_cnt2 = $resultat2->num_rows;
					$response["teachers"] = array();
					if($row_cnt2 > 0){
						while ($row2 = $resultat2->fetch_assoc()) {
							$infos = array();
							//Recovery teacher data for the application
							$infos["idTeacher"] = htmlentities($row2["userID"]);
							$infos["teacherLogin"] = htmlentities($row2["login"]);
							array_push($response["teachers"], $infos);
						}
					}
				}
			
				$response["success"] = 1;
				echo json_encode($response);
			}
			else {
				$response["success"] = 0;
				if(empty($_POST['login']) || empty($_POST['password']))	$response["message"] = "Champ(s) manquant(s)";
				else $response["message"] = "Combinaison incorrecte";

				echo json_encode($response);
			}
		}

		/* Connection closed */
		mysqli_close($mysqli);
	}
	else{
		// field(s) missing
		$reponse["success"] = 0;
		$reponse["message"] = "Champ(s) non envoyés de la part d'android";
	 
		// Display JSON response
		echo json_encode($reponse);
	}
	
?>