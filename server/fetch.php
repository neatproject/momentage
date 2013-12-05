<?php
header('Access-Control-Allow-Origin: *');
/**
 * fetch data by client_id
 * @author NeatProject
 * @see config.php
 */
require 'config.php';

$db = DB::getInstance();
$client_id = isset($_GET['client_id']) ? $_GET['client_id'] : null;
$user_id_exists = $db->GetCell('SELECT id FROM users WHERE fb_id = :fb_id', array(':fb_id' => $client_id));

if (!$user_id_exists) die('not a valid client id');
//get resource by client
$user_media = $db->GetAll('
SELECT t2.*
FROM users t1
INNER JOIN resources t2 ON t1.id = t2.user_id
WHERE t1.fb_id = :fb_id
ORDER BY t2.post_date DESC', array(':fb_id' => $client_id));

header('Content-Type: application/json');
echo json_encode(array('success' => true, 'data' => array('uid' => $user_id_exists, 'media' => $user_media)));
