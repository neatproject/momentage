<?php
//header('Access-Control-Allow-Origin: *');
require 'config.php';

/**
 * get details from to facebook graph by id
 */
function get_user_details($user_fb_id){
    $ch = curl_init();

    // curl_setopt($ch, CURLOPT_AUTOREFERER, TRUE);
    // curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_URL, 'https://graph.facebook.com/' . $user_fb_id);
    // curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);       

    $data = curl_exec($ch);
    curl_close($ch);

    return json_decode($data);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
	$db = DB::getInstance();
	$userID =  isset($_POST['userID']) ? $_POST['userID'] : null;
	$response = array();
        

    if ($user_info = get_user_details($userID)) {
		$user_id = $user_info->id;

		$user_id_exists = $db->GetCell('SELECT id FROM users WHERE fb_id = :fb_id', array(':fb_id' => $user_id));

		if (!$user_id_exists) { // then register
			$db->Execute('INSERT INTO users(fb_id, register_at) VALUES(:fb_id, CURRENT_TIMESTAMP())', array(':fb_id' => $user_id));
			$user_id_exists = $db->GetInsertID();
		}

		$user_media = $db->GetAll('
		SELECT t2.*
		FROM users t1
		INNER JOIN resources t2 ON t1.id = t2.user_id
		WHERE t1.fb_id = :fb_id
		ORDER BY t2.post_date DESC', array(':fb_id' => $user_id));

		$response['data'] = array('user_info' => $user_info, 'user_media' => $user_media);
		$response['widget'] = array('html' => htmlspecialchars('<!DOCTYPE html>
<html lang="es-ES" dir="ltr">
	<head>
		<meta charset="UTF-8" />
		<title>Ajax Upload | Localhost</title>
		<style media="screen">
			* {margin:0;padding:0;}
			body {font:normal 15px/1 arial;}
			h1,h2,h3,h4 {font-family:tahoma;margin-bottom:15px;margin-top:15px;}
			p {margin-bottom:15px;}
			#wrapper {width:1000px;margin-left:auto;margin-right:auto;}
			input {font:inherit;padding:5px;}
			#upload-iframe {display:block;width:0;height:0px;border:0;display:none !important;}
			pre {font: normal 15px/25px consolas;}
			.no-js {display:none;}
			#response-msg {height: 40px;line-height:40px;text-align:center;display:none;margin-bottom:15px;font-weight:bold;}
			#response-msg .success {background-color:#2b8806;color:#fff;}
			#response-msg .error {background-color:#ab0316;color:#fff;}
		</style>
	</head>
	<body>
		<div id="response-msg"></div>
		<div id="wrapper">
			<h1>Ajax Uploader</h1>
			<form method="post" action="https://www.iadrian.pe/paisa-connect/upload.php" id="form" enctype="multipart/form-data" autocomplete="off">
				<input type="hidden" name="client_id" value="'.$user_id.'">
				<input type="hidden" name="submit" value="1" />
				<p><input id="file" type="file" name="file" /></p>
				<p><input id="upload" type="submit" value="Subir fichero" /></p>
			</form>
		</div>
	</body>
</html>'), 'uri' => 'https://www.iadrian.pe/paisa-connect/fetch.php?client_id=' . $user_id);
	} else {
		$response['success'] = false;
		$response['message'] = 'Not a valid access token';
	}

	header('Content-Type: application/json');
	echo json_encode($response);       
}

?>