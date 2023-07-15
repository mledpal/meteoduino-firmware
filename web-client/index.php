<?php
  session_start();
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel=icon href='img/thermostat.svg' sizes="32x32" type="image/svg">
    <link rel="stylesheet" href="css/index.css">
    <title>Monitor de Temperatura</title>
</head>
<body>
  <aside>
    <form id="form">
      <label for="modo">Gráfica</label>
      <select name="modo" id="modo">
        <option value="24h">24 Horas</option>
        <option value="temperaturas">Temperaturas</option>        
        <option value="otros">Presion / Humedad </option>        
      </select>
        <input id="btnSubmit" type="submit" value="Enviar">
    </form> 
    
    <section id="datos">
      <h2>Datos</h2>
      <div id="datos">
        <p><span class="titulo">Hora </span><span class="dato" id="tiempo"></span></p>  
        <p><span class="titulo">Máxima </span><span class="dato" id="tmaxima"></span></p>
        <p><span class="titulo">Media </span><span class="dato" id="tmedia"></span></p>
        <p><span class="titulo">Mínima </span><span class="dato"  id="tminima"></span></p>        
      </div>

    </section>


  </aside>
    
  <div id="graph">
      <canvas id="grafico"></canvas>
  </div>


  <script src="./js/chart.js"></script>
  <script src="./js/graph.js" type="module"></script>  
  <script src="./js/index.js" type="module"></script>
    
</body>
</html>