"use strict";

import { graph24, graphTemperaturas, graphOtros } from './graph.js';

const url = 'http://ledemar.ddns.net/chart/api.php';


const btnSubmit = document.getElementById('btnSubmit');
const opcion = document.getElementById('modo');

document.addEventListener('DOMContentLoaded', () => {

  opcion.addEventListener('change', (e) => {
    e.preventDefault();  
    destroyGraph();  
    
  });

  btnSubmit.addEventListener('click', (e) => {
    e.preventDefault();    
    destroyGraph();
    
    let modo = opcion.value;
    drawGraph(modo);    
  });

});

function destroyGraph() {
  document.getElementById('grafico').remove();    
  let canvas = document.createElement('canvas');
  canvas.setAttribute('id', 'grafico');
  document.getElementById('graph').appendChild(canvas);    
}

async function drawGraph(modo) {

  const tiempo = document.getElementById('tiempo');
  const TMaxima = document.getElementById('tmaxima');
  const TMinima = document.getElementById('tminima');
  const TMedia = document.getElementById('tmedia');

  const options = {
    method: "POST",
    body: `modo=${modo}`,    
    cors: 'no-cors',
    headers: {      
      'Access-Control-Allow-Origin': '*',
      'cors': 'no-cors',
      'Content-type': 'application/x-www-form-urlencoded',        
      cache: 'no-cache'        
    }
  }

  const response = await fetch(url, options);  
  const datos = await response.json();
  
  switch(modo) {

    case '24h':
      graph24(datos[0], datos[1], datos[2], datos[3], datos[4]);       
      TMaxima.textContent = `${Math.max(...datos[1])} ºC`;
      TMinima.textContent = `${Math.min(...datos[1])} ºC`;
      TMedia.textContent = `${Math.round(datos[1].reduce((a, b) => a + b, 0) / datos[1].length, 2)} ºC`;
      tiempo.textContent = datos[0][0];
      break;

    case 'temperaturas':            
      graphTemperaturas(datos[0], datos[1], datos[2], datos[3], datos[4], datos[5], datos[6]);
      break;

    case 'otros':          
      graphOtros(datos[0], datos[1], datos[2], datos[3], datos[4], datos[5], datos[6]);
      break;
  }
  
  
}


drawGraph('24h');  // Dibuja el gráfico por defecto al cargar la página
