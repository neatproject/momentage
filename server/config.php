<?php
/* app settings */
define('APP_ID', '514447938662433');
define('APP_SECRET', 'dd0147d626453d918ccaa95c9289b0a5');
/* Database Settings */
require 'libs/rb.php';

/**
 * Connect to database, use a ORM for easy access to database
 * @author NeatProject
 * @name DB
 * @package momentage
 * @see libs.rb.php
 */
abstract class DB {
	private static $hostname = 'localhost';
	private static $username = 'lightadm_moment';
	private static $password = 'moment_1';
	private static $database = 'lightadm_momentage';
        
	private static $instance;
        
        /**
         * get instance of ORM, link to database
         * @return RedBean 
         */
	public static function getInstance() {
		if (!isset(self::$instance)) {
			$dsn = sprintf('mysql:host=%s;dbname=%s', self::$hostname, self::$database);
			self::$instance = new RedBean_Driver_PDO($dsn, self::$username, self::$password);
		}

		return self::$instance;
	}
}