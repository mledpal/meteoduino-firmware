use meteo;

/*
SELECT 
	fecha, MAX(sensor1) as MAX1, min(sensor1) as MIN1, max(sensor2) as MAX2, min(sensor2) AS MIN2 
    from `datos`  
    group by fecha 
    having year(fecha)="2023" AND month(fecha)="7" 
    order by fecha; 
    */
    
/*
SELECT 
	DATE_FORMAT(hora, '%k:%i') as hora, fecha, sensor1 as T1, sensor2 AS T2, p_mar as PRESION, humedad 
    FROM `datos` 
    WHERE YEAR(fecha)="2023" AND MONTH(fecha)=7 AND DAY(fecha) = 13
    ORDER BY id ASC;
 */ 
 /*
SELECT subquery.* FROM (
	SELECT 
		id, DATE_FORMAT(hora, '%k:%i') as hora, fecha, sensor1 as T1, sensor2 AS T2, p_mar as presion, humedad 
		FROM `datos`  
		ORDER BY id DESC 
		LIMIT 288
) as subquery 
ORDER BY subquery.id ASC; */



SELECT 
	id, fecha, max(sensor1) as MaxT1, min(sensor1) as MinT1, AVG(sensor1) as MedT1, MAX(sensor2) as MaxT2, MIN(sensor2) as MinT2, AVG(sensor2) as MedT2
	FROM `datos`  
	ORDER BY id DESC ;



