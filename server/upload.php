<?php
# CORS
header('Access-Control-Allow-Origin: *');

require 'config.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && !empty($_FILES)) {
	// auth
	$db = DB::getInstance();
	$client_id = isset($_POST['client_id']) ? $_POST['client_id'] : null;
	$user_id_exists = $db->GetCell('SELECT id FROM users WHERE fb_id = :fb_id', array(':fb_id' => $client_id));

	if (!$user_id_exists) die('not a valid client id');

	$ext_files = array('image/jpeg', 'image/jpg', 'image/png', 'video/mp4');
	$max_bytes = 5242880;

	// send the form ?
	if (isset($_POST['submit']) && $_FILES['file']['size'] > 0) {
		// no error with $_FILE ?
		if ($_FILES['file']['error'] === 0) {
			if ($_FILES['file']['size'] > $max_bytes) {
				// weight exceeded ?
				$response['error'] = 'Only supports images up 5 MB';
			} elseif (in_array($_FILES['file']['type'], $ext_files) !== true) {
				// format no allowed ?			
				$response['error'] = 'Solo se admiten imagenes en formato ( jpg, jpeg, png ) o videos en formato .mp4';
			} else {
				// no problem, upload ?
				if (in_array($_FILES['file']['type'], array('image/jpeg', 'image/jpg', 'image/png'))) {
					$imagen = getimagesize($_FILES['file']['tmp_name']);
					// get extension
					switch ($imagen[2]) {
						case 2:
							$ext = '.jpg';
						break;
						case 3:
							$ext = '.png';
						break;
					}
				} else { // is a video
					$ext = '.mp4';
				}

				if (is_uploaded_file($_FILES['file']['tmp_name'])) {
					// generate a new name
					$nuevo_nombre = uniqid(md5(microtime())) . $ext;
					// move the temporary file to the server hard disk
					if (move_uploaded_file($_FILES['file']['tmp_name'], 'uploads/' . $nuevo_nombre) !== false) {
						$response['success'] = 'Fichero subido con exito';
						$response['filename'] = 'https://familymelon.com/momentage/uploads/' . $nuevo_nombre;

						// save in database
						$db->Execute('INSERT INTO resources(src, mime_type, filesize, post_date, user_id) VALUES(:src,:mime_type,:file_size,CURRENT_TIMESTAMP(),:user_id)', array(
							':src' => $response['filename'], ':mime_type' => $_FILES['file']['type'], ':user_id' => $user_id_exists, ':file_size' => $_FILES['file']['size']
						));

						$response['order_id'] = '#' . $db->GetInsertID();
					} else {
						$response['error'] = 'Oops, no se pudo subir el fichero.';
					}
				}
			}
		}

		header('Content-Type: application/json');
		echo json_encode($response);
	}
}
?>