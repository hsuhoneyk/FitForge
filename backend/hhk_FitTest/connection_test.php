<?php
// mysql connection parameters 

$hostname = "127.0.0.1";
$dbusername = "root"; 
$password = "";

// Default username fpr phpmyadmin is 
// default password in XAMPP
$dbname = "fitforge_db";
// create connection
$connection = new mysqli ($hostname, $dbusername, $password, $dbname);

// check connection
if ($connection -> connect_error){
    die("Connection failed: . $connection-> connect_error");
} 
// else {echo "Connection successful!";}
?>