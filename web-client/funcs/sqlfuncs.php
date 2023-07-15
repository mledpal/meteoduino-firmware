<?php   

    function datos24h() {
        $horas = array();
        $temperaturas1 = array();
        $temperaturas2 = array();
        $humedad = array();
        $presion = array();

        $query = "SELECT subquery.* FROM (
                    SELECT 
                        id, DATE_FORMAT(hora, '%k:%i') as hora, fecha, sensor1 as T1, sensor2 AS T2, p_mar as presion, humedad 
                        FROM `datos`  
                        ORDER BY id DESC 
                        LIMIT 288
                ) as subquery 
                ORDER BY subquery.id ASC";

        $sql = $conn->prepare($query);
        $sql->execute(); 


        while($row = $sql->fetch()) {
            array_push($horas , (string) $row['hora']);
            array_push($temperaturas1 , (float) $row['T1']);
            array_push($temperaturas2 , (float) $row['T2']);
            array_push($humedad , $row['humedad']);
            array_push($presion , $row['presion']);
        }     

        array_push($datos, $horas, $temperaturas1, $temperaturas2, $humedad, $presion);

        return $datos;
    }