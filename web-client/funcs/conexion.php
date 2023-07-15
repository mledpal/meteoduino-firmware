<?php

    $server = "localhost";
    $username = "usuario";
    $password = "contraseña";
    $bbdd = "meteo";

    $conn = NEW PDO("mysql:host=$server;dbname=$bbdd", $username, $password);
    $conn -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);   
    
?>
