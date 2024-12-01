package meteoduinoreader;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Miguel Ledesma Palacios
 * @version 2.0
 * @since 2021
 * 
 */
public class LecturaEXT {
    
    private final int epochTime;
    private final float temperatura;
    private final int humedad;
    private final float velocidad_viento;    
    private final String orientacion_viento;
    private final int presion;
    private final float radiacion_solar;
    private final float precipitacion;
    private final float precipitacion_dia;    

    
    
    public LecturaEXT(float temperatura, int humedad, float velocidad_viento, String orientacion_viento, int presion,
            float radiacion_solar, float precipitacion, float precipitacion_dia, int fecha) {
                
        this.epochTime = (int) System.currentTimeMillis();        
        this.humedad = humedad;
        this.orientacion_viento = orientacion_viento;
        this.precipitacion = precipitacion;
        this.precipitacion_dia = precipitacion_dia;
        this.presion = presion;
        this.radiacion_solar = radiacion_solar;
        this.temperatura = temperatura;
        this.velocidad_viento = velocidad_viento;

    }
    
    
    public LecturaEXT(List<String> datos) {
        this.temperatura = !datos.isEmpty() ? valorTemperatura(datos.get(0)) : 0.0f;
        this.humedad = datos.size() > 1 ? valorHumedad(datos.get(1)) : 0;
        
        
        if (datos.size() > 2) {
            String[] vientoDatos = datosViento(datos.get(2));
            this.velocidad_viento = Float.parseFloat(vientoDatos[1]);
            this.orientacion_viento = vientoDatos[0];
        } else {
            this.velocidad_viento = 0.0f;
            this.orientacion_viento = "N/A";
        }
        
        this.presion = datos.size() > 3 ? valorPresion(datos.get(3)) : 0;
        this.radiacion_solar = datos.size() > 4 ? valorRadiacionSolar(datos.get(4)) : 0.0f;
        this.precipitacion = datos.size() > 5 ? valorPrecipitacion(datos.get(5)) : 0.0f;
        this.precipitacion_dia = datos.size() > 6 ? valorPrecipitacion(datos.get(6)) : 0.0f;
        this.epochTime = (int) LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    
    private String[] datosViento(String datos) {
        String[] datoviento = datos.trim().split("\\s+");
        String direccion = datoviento[0];
        String velocidad = datoviento[1].replaceAll("[^0-9]", "");
        return new String[] { direccion, velocidad };
    }

    private float valorTemperatura(String txttemperatura) {
        String datotemperatura = txttemperatura.trim().split("\\s+")[0];
        return Float.parseFloat(datotemperatura);
    }

    private int valorHumedad(String txthumedad) {
        String datohumedad = txthumedad.trim().split("\\s+")[0];
        return Integer.parseInt(datohumedad);
    }

    private int valorPresion(String txtpresion) {
        String datopresion = txtpresion.trim().split("\\s+")[0];
        return Integer.parseInt(datopresion);
    }

    private int valorRadiacionSolar(String txtradiacion) {
        String datoradiacion = txtradiacion.trim().split("\\s+")[0];
        return Integer.parseInt(datoradiacion);
    }

    private float valorPrecipitacion(String txtprecipitacion) {
        String datoprecipitacion = txtprecipitacion.trim().split("\\s+")[0];
        return Float.parseFloat(datoprecipitacion);
    }
    
    public float getTemperatura() {
        return this.temperatura;
    }

    public int getHumedad() {
        return this.humedad;
    }

    public float getVelocidad_viento() {
        return this.velocidad_viento;
    }

    public String getOrientacion_viento() {
        return this.orientacion_viento;
    }

    public int getPresion() {
        return this.presion;
    }

    public float getRadiacion_solar() {
        return this.radiacion_solar;
    }

    public float getPrecipitacion() {
        return this.precipitacion;
    }

    public float getPrecipitacion_dia() {
        return this.precipitacion_dia;
    }
        
    public int getEpochTime() {
        return this.epochTime;
    }

    public String getFecha() {
        return epochToDateTime('F', this.epochTime);
    }

    public String getHora() {
        return epochToDateTime('H', this.epochTime);
    }
    
    /**
     * 
     * @param dato F para devolver fecha, H para devolver hora. Cualquiera para devolver timestamp
     * @param time Entero 
     * @return 
     */
    public static String epochToDateTime(char dato, long time) {
        
        String format;
        
        format = switch (dato) {
            case 'F', 'f' -> "yyyy-MM-dd";
            case 'H', 'h' -> "HH:mm:ss";
            default -> "dd/MM/yyyy HH:mm:ss";
        };
                        
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("España"));
        return sdf.format(new Date(time * 1000));  
        
    } 
    
    public String[] devolverDatos() {
        return new String[] { 
            String.valueOf(this.temperatura),
            String.valueOf(this.humedad),
            String.valueOf(this.velocidad_viento),
            this.orientacion_viento,
            String.valueOf(this.presion),
            String.valueOf(this.radiacion_solar),
            String.valueOf(this.precipitacion),
            String.valueOf(this.precipitacion_dia),
            epochToDateTime('F', this.epochTime),
            epochToDateTime('H', this.epochTime)
        };
    }
    
    @Override
    public String toString() {
        
        String cadena="";
        cadena += "Temperatura: " + this.temperatura + "ºC\n";
        cadena += "Humedad: " + this.humedad + "%\n";
        cadena += "Velocidad del viento: " + this.velocidad_viento + " m/s\n";
        cadena += "Orientación del viento: " + this.orientacion_viento + "\n";
        cadena += "Presión: " + this.presion + " hPa\n";
        cadena += "Radiación solar: " + this.radiacion_solar + " W/m2\n";
        cadena += "Precipitación: " + this.precipitacion + " mm\n";
        cadena += "Precipitación del día: " + this.precipitacion_dia + " mm\n";
        cadena += "Fecha: " + epochToDateTime('F', this.epochTime) + "\n";
        cadena += "Hora: " + epochToDateTime('H', this.epochTime) + "\n";        
        
        return cadena;
    }
    
    
}
