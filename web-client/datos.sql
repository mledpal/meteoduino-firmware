use meteo;

select * from datos as t1
	INNER JOIN datos as t2 ON t1.hora = DATE_FORMAT(t2.hora, '%k:%i')
    LIMIT 10;
    

//select DATE_FORMAT(hora, '%k:%i') as hora from datos ORDER BY id DESC limit 1;


#SELECT id, sensor1,sensor2 from datos where id between 66706 and 66729;

/*UPDATE datos AS t1
	INNER JOIN datos t2 ON t1.id = t2.id
	SET t1.sensor1 = t2.sensor2
	WHERE t1.id BETWEEN 66706 AND 66729;*/

# UPDATE datos set p_mar=1012 WHERE id = 66584;

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



/*SELECT 
	id, fecha, max(sensor1) as MaxT1, min(sensor1) as MinT1, ROUND(AVG(sensor1),2) as MedT1, 
    MAX(sensor2) as MaxT2, MIN(sensor2) as MinT2, ROUND(AVG(sensor2), 2) as MedT2,
    MAX(p_mar) as MaxP, MIN(p_mar) as MinP, ROUND(AVG(p_mar), 2) AS MedP,
    MAX(humedad) as MaxH, MIN(humedad) as MinH, ROUND(AVG(humedad), 2) as MedH
	FROM `datos`  
    GROUP BY fecha
	ORDER BY id DESC ;*/



