<?php
	
	/*  Creation Date : 15/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Upload video script
	*/
	$general_folder = 'videosToStore';	
	
	function uploadFile($inputTeacherID, $fileName){
		global $general_folder;
		$file_path = $general_folder.'/'.$inputTeacherID."/".$fileName;
		$file_size = $_FILES['uploaded_file']['size'];
		
		$extensions = array("mp4", "3gp", "avi", "mkv", "mpg");
		
		list($name, $type) = explode(".", $fileName);
		
		$error = false;
		// It checks whether the extension of the file upload is permitted
		if(in_array($type,$extensions) === false) {
			$error = true;
			$response["success"] = 0;
			$response['message'] = "L'extension n'est pas compatible";
		}

		// We check whether our file does not exceed the limit
		if($file_size == 0) {
			$error = true;
			$response["success"] = 0;
			$response['message'] = "La taille du fichier n'est pas adapté";
		}
		
		if (!$error) {
			if (!file_exists($general_folder))	mkdir($general_folder, 0777, true);	// Create the general file
			// Create subfolder
			if (!file_exists($general_folder.'/'.$inputTeacherID))	mkdir($general_folder.'/'.$inputTeacherID, 0777, true);
			
			// Upload file in folder
			if (file_exists($general_folder.'/'.$inputTeacherID.'/'.$fileName)){
				$response["success"] = 0;
				$response["message"] = "Vidéo déjà présente dans la liste";
			}
			else{
				if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path))	$response["success"] = 1;
				else{
					$response["success"] = 0;
					$response["message"] = "Une erreur est survenue lors de l'import vers le serveur";
				}
			}
		}
		
		echo json_encode($response);
	}
	
	uploadFile($_POST['teacherID'],basename( $_FILES['uploaded_file']['name']));
?>