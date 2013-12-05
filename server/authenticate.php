<?php
/**
 * route/path to authenticate the user and add
 * @author NeatProject
 * @name authenticate
 * @package momentage
 * @see config.php
 */

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
// capture type request
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
	$db = DB::getInstance();
	$userID =  isset($_POST['userID']) ? $_POST['userID'] : null;
	$response = array();
        

    if ($user_info = get_user_details($userID)) {
		$user_id = $user_info->id;

		//validate user
		$user_id_exists = $db->GetCell('SELECT id FROM users WHERE fb_id = :fb_id', array(':fb_id' => $user_id));

		if (!$user_id_exists) { // then register
			$db->Execute('INSERT INTO users(fb_id, register_at) VALUES(:fb_id, CURRENT_TIMESTAMP())', array(':fb_id' => $user_id));
			$user_id_exists = $db->GetInsertID();
		}
		//build response
		$user_media = $db->GetAll('
		SELECT t2.*
		FROM users t1
		INNER JOIN resources t2 ON t1.id = t2.user_id
		WHERE t1.fb_id = :fb_id
		ORDER BY t2.post_date DESC', array(':fb_id' => $user_id));

		$response['data'] = array('user_info' => $user_info, 'user_media' => $user_media);
		$response['data_fetch_url'] = 'https://familymelon.com/momentage/fetch.php?client_id=' . $user_id;
	} else {
		$response['success'] = false;
		$response['message'] = 'Not a valid access token';
	}

	header('Content-Type: application/json');
	echo json_encode($response);       
}

?>