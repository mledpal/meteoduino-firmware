<?php

    $server = "localhost";
    $username = "miguel";
    $password = "5569fvz";
    $bbdd = "meteo";

    $conn = NEW PDO("mysql:host=$server;dbname=$bbdd", $username, $password);
    $conn -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);   
    
?>
