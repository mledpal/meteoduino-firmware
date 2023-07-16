export function graph24(horas, temperaturas1, temperaturas2, humedad, presion)  {
  
  const ctx = document.getElementById('grafico');
  
  new Chart(ctx, {   
        data: {
          labels: horas,            
          datasets: [{
            label: 'BMP ºC',
            type: 'line',
            data: temperaturas1,
            borderWidth: 1,                
            yAxisID: 'y-temperatura'
          },
          {
            label: 'DHT ºC',
            type: 'line',
            data: temperaturas2,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: true
          },
          {
            type: 'line',
            label: 'Humedad',
            data: humedad,
            borderWidth: 1,
            yAxisID: 'y-humedad',
            hidden: true
          },
          {
            type: 'line',
            label: 'Presion',
            data: presion,
            borderWidth: 1,
            hidden: true,
            yAxisID: 'y-presion'        
          }
        ]
        },
        options: {        
          pointStyle : false,
          scales: {      
              'y-temperatura': {                        
                type: 'linear',      
                position: 'left'      
              },
              'y-humedad': {
                label: 'Humedad',
                min: 0,
                max: 100,
                type: 'linear',
                position: 'right',
                step: 10
              }, 
              'y-presion': {
                label: 'Presión',
                min: 960,
                max: 1050,
                position: 'right'            
              }          
          } 
        }
      });
}

export function graphTemperaturas(fecha, max1, min1, med1, max2, min2, med2)  {
  
  const ctx = document.getElementById('grafico');
  
  new Chart(ctx, {    
        data: {
          labels: fecha,            
          datasets: [{
            label: 'BMP MAX',
            type: 'line',
            data: max1,
            borderWidth: 1,    
            hidden: false,            
            yAxisID: 'y-temperatura'
          },
          {
            label: 'BMP MIN',
            type: 'line',
            data: min1,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: false
          },
          {
            type: 'line',
            label: 'BMP MED',
            data: med1,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: false
          },{
            label: 'DHT MAX',
            type: 'line',
            data: max2,
            borderWidth: 1,    
            hidden: true,            
            yAxisID: 'y-temperatura'
          },
          {
            label: 'DHT MIN',
            type: 'line',
            data: min2,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: true
          },
          {
            type: 'line',
            label: 'DHT MED',
            data: med2,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: true
          }
        ]
        },
        options: {          
          pointStyle : false,
          scales: {      
              'y-temperatura': {                        
                type: 'linear',      
                position: 'left'      
              },
              'y-humedad': {
                label: 'Humedad',
                min: 0,
                max: 100,
                type: 'linear',
                position: 'right',
                step: 20
              }, 
              'y-presion': {
                label: 'Presión',
                min: 960,
                max: 1050,
                position: 'right'            
              }          
          } 
        }
      });
}


export function graphOtros(fecha, maxp, minp, medp, maxh, minh, medh )  {
  
  const ctx = document.getElementById('grafico');
  
  new Chart(ctx, {    
        data: {
          labels: fecha,            
          datasets: [{
            label: 'Humedad MAX',
            type: 'line',
            data: maxh,
            borderWidth: 1,    
            hidden: true,            
            yAxisID: 'y-humedad'
          },
          {
            label: 'Humedad MIN',
            type: 'line',
            data: minh,
            borderWidth: 1,
            yAxisID: 'y-humedad',
            hidden: true
          },
          {
            type: 'line',
            label: 'Humedad MED',
            data: medh,
            borderWidth: 1,
            yAxisID: 'y-humedad',
            hidden: false
          },{
            label: 'Presion MAX',
            type: 'line',
            data: maxp,
            borderWidth: 1,    
            hidden: true,            
            yAxisID: 'y-presion'
          },
          {
            label: 'Presion MIN',
            type: 'line',
            data: minp,
            borderWidth: 1,
            yAxisID: 'y-presion',
            hidden: true
          },
          {
            type: 'line',
            label: 'Presion MED',
            data: medp,
            borderWidth: 1,
            yAxisID: 'y-presion',
            hidden: false
          }
          
        ]
        },
        options: {          
          pointStyle : false,
          scales: {      
              'y-humedad': {
                label: 'Humedad',
                type: 'linear',
                position: 'left',
                step: 20
              }, 
              'y-presion': {
                label: 'Presión',
                
                position: 'right'            
              }          
          } 
        }
      });
}


export function graphLastDays(fecha, t1, t2, p, h)  {
  
  const ctx = document.getElementById('grafico');
  
  new Chart(ctx, {    
        data: {
          labels: fecha,            
          datasets: [{
            label: 'T1',
            type: 'line',
            data: t1,
            borderWidth: 1,    
            hidden: false,            
            yAxisID: 'y-temperatura'
          },
          {
            label: 'T2',
            type: 'line',
            data: t2,
            borderWidth: 1,
            yAxisID: 'y-temperatura',
            hidden: true
          },
          {
            type: 'line',
            label: 'Presión',
            data: p,
            borderWidth: 1,
            yAxisID: 'y-presion',
            hidden: true
          },{
            label: 'Humedad',
            type: 'line',
            data: h,
            borderWidth: 1,    
            hidden: true,            
            yAxisID: 'y-humedad'
          }]
        },
        options: {          
          pointStyle : false,
          scales: {      
              'y-temperatura': {                        
                type: 'linear',      
                position: 'left'      
              },
              'y-humedad': {
                label: 'Humedad',
                min: 0,
                max: 100,
                type: 'linear',
                position: 'right',
                step: 20
              }, 
              'y-presion': {
                label: 'Presión',
                min: 960,
                max: 1050,
                position: 'right'            
              }          
          } 
        }
      });
}
