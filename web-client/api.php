<?php

/**
 * API para devolver los datos meteorológicos en formato JSON
 * con peticiones POST
 * Autor : Miguel Ledesma Palacios
 */

    switch($_SERVER['REQUEST_METHOD']) {
    case 'POST':                        
        devolverDatos($_POST['modo']);
        break;
    
    case 'GET':
        echo json_encode(['error'=>'Use POST']);
        break;

    default:
        echo json_encode(['error'=>'ERROR default']);   
        header("HTTP/1.1 200 OK");
    }


/**
 * Funcion que devuelve los datos de la base de datos en formato JSON
 * @param string $modo : Modo de devolucion de datos
 * @return array $datos : Array con los datos de la base de datos
 *  
 */
function devolverDatos($modo) {
    
    include_once 'funcs/conexion.php'; // Datos de la conexion a MySQL
    include_once 'funcs/sqlfuncs.php'; // Fichero de funcionalidades. *** En progreso ***

    $datos = array();    
    $modo = $_POST['modo'];
    
    switch($modo) {
        case '24h':
            //$datos = datos24h();
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
            break;

        case 'temperaturas':

            $fecha = array();
            $max1 = array();
            $min1 = array();
            $med1 = array();
            $max2 = array();
            $min2 = array();
            $med2 = array();

            $query = "SELECT subquery.* from (
                SELECT 
                    id, fecha, max(sensor1) as MaxT1, min(sensor1) as MinT1, ROUND(AVG(sensor1),2) as MedT1, 
                    MAX(sensor2) as MaxT2, MIN(sensor2) as MinT2, ROUND(AVG(sensor2), 2) as MedT2                    
                    FROM `datos`  
                    GROUP BY fecha
                    ORDER BY fecha DESC
                    LIMIT 60) as subquery
                    ORDER BY subquery.id ASC";

            $sql = $conn->prepare($query);
            $sql->execute(); 

            while($row = $sql->fetch()) {
                array_push($fecha , (string) $row['fecha']);
                array_push($max1 , (float) $row['MaxT1']);
                array_push($min1 , (float) $row['MinT1']);
                array_push($med1 , (float) $row['MedT1']);
                array_push($max2 , (float) $row['MaxT2']);
                array_push($min2 , (float) $row['MinT2']);
                array_push($med2 , (float) $row['MedT2']);               
            } 

            array_push($datos, $fecha, $max1, $min1, $med1, $max2, $min2, $med2, $maxP, $minP, $medP, $maxH, $minH, $medH);

            break;


        case 'otros':
            $fecha = array();
            
            $maxP = array();
            $minP = array();    
            $medP = array();
            $maxH = array();
            $minH = array();
            $medH = array();
            

            $query = "SELECT subquery.* from (
                SELECT 
                    id, fecha, 
                    MAX(p_mar) as MaxP, MIN(p_mar) as MinP, ROUND(AVG(p_mar), 2) AS MedP,
                    MAX(humedad) as MaxH, MIN(humedad) as MinH, ROUND(AVG(humedad), 2) as MedH
                    FROM `datos`  
                    GROUP BY fecha
                    ORDER BY fecha DESC
                    LIMIT 60) as subquery
                    ORDER BY subquery.id ASC";

            $sql = $conn->prepare($query);
            $sql->execute(); 

            while($row = $sql->fetch()) {
                array_push($fecha , (string) $row['fecha']);
                
                array_push($maxP , (float) $row['MaxP']);
                array_push($minP , (float) $row['MinP']);
                array_push($medP , (float) $row['MedP']);
                array_push($maxH , (float) $row['MaxH']);
                array_push($minH , (float) $row['MinH']);
                array_push($medH , (float) $row['MedH']);                
            } 

            array_push($datos, $fecha, $maxP, $minP, $medP, $maxH, $minH, $medH);

            break;

        default:
            echo json_encode(['error'=>'MODE ERROR']);
            break;
    }



    echo json_encode($datos);
}


header("HTTP/1.1 200 OK");


?>