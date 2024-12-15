package meteoduinoreader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Miguel Ledesma Palacios
 */
public class Lectura {
    
    private final float temp1;
    private final float temp2;
    private final float sensacion;
    
    private final int humedad;
    private final float altura;
    private final int presion;
    private final int presion_mar;
    private final float bateria;
    
    private final int fecha;
    
    public Lectura (float temp1, float temp2, float sensacion, int humedad, float altura, int presion, int presion_mar, float bateria, int fecha) {
        
        this.altura = altura;
        this.fecha = fecha;
        this.humedad = humedad;
        this.presion = presion;
        this.presion_mar = presion_mar;
        this.sensacion = sensacion;
        this.temp1 = temp1;
        this.temp2 = temp2;       
        this.bateria = bateria;
    }
    
    
    public float getSensor1() {
        return this.temp1;
    }
    
    public float getSensor2() {
        return this.temp2;
    }
    
    public float getTMedia() {
        return (this.temp1+this.temp2)/2;        
    }
    
    public float getSTermica() {
        return this.sensacion;
    }
    
    public int getHumedad() {
        return this.humedad;
    }
    
    public int getPresion() {
        return this.presion;
    }
    
    public int getPresionMar() {
        return this.presion_mar;
    }
    
    public float getAltura() {
        return this.altura;
    }
    
    public float getBateria() {
        return this.bateria;
    }
    
    public String getHora() {
        return epochToDateTime('H', this.fecha);        
    }
    
    public String getFecha() {
        return epochToDateTime('F', this.fecha);
    }
    
    public int getEpochTime() {
        return this.fecha;
    }
    
    /**
     * 
     * @param dato F para devolver fecha, H para devolver hora. Cualquiera para devolver timestamp
     * @param time Entero 
     * @return 
     */
    public static String epochToDateTime(char dato, long time) {
        
        String format="";
        
        switch (dato) {
            case 'F':
            case 'f': {
                format = "yyyy-MM-dd";
                break;
            }
            
            case 'H':
            case 'h': {
                format="HH:mm:ss";
                break;
            }
            
            default:
                format="dd/MM/yyyy HH:mm:ss";
        }
        
        
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("España"));
        return sdf.format(new Date(time * 1000));  
        
    } // END epochToDateTime
    
    
    @Override
    public String toString() {
        
        String cadena="";
        cadena+="T1: "+temp1+" | T2: "+temp2+" | ST: "+sensacion;
        cadena+=" | H: " + humedad+ " | PR: " + presion + " | PNM: " + presion_mar;
        cadena+=" | ALT: " + altura;
        cadena+=" | BATT: " + bateria;
        cadena+=" | Epoch: " + fecha;
        
        return cadena;
    }
    
    
}
