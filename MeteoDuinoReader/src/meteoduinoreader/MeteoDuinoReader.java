package meteoduinoreader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Miguel Ledesma Palacios
 * @version 2.0
 * @since 2021 
 */
public class MeteoDuinoReader {
    
    // Servidor al que conectarse para recoger datos
    
    static String cadenaXML="";            // Cadena para guardar el XML
    static String url = "https://www.meteoclimatic.net/perfil/ESAND2300000023700C";
    
    // Variables para guardar los datos de la meteorológica
    static String hora;
    static int fecha;
    static String date; 

    static String temperatura1, temperatura2, temperaturaMedia, sensacionTermica;
    static String presion, altura, presionMar;
    static String humedad;
    public String ipMeteo;
    
    // Variables para guardar los datos de la meteorológica
       
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.text.ParseException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException, SQLException, ClassNotFoundException {

        System.out.println("Informacion de las meteorológicas");
        recogerDatosWeb();
        leerDatos();
        
        // Lectura y guardado de datos de la meteorológica externa
        Scraper scraper = new Scraper(url);
        LecturaEXT lectura2 = scraper.leerDatos();
        Conexion.guardaDatosEXT(lectura2);
        
        // Lectura y guardado de datos de la meteorológica ESP8266
        Lectura lectura1 = new Lectura(Float.parseFloat(temperatura1), Float.parseFloat(temperatura2), Float.parseFloat(sensacionTermica), Integer.parseInt(humedad),
        Float.parseFloat(altura), Integer.parseInt(presion), Integer.parseInt(presionMar), fecha);        
        Conexion.guardaDatos(lectura1);
        
                
        System.out.println(lectura1.toString());
        

    }
    
    
    
    /**
     * Método para recoger los datos de la meteorológica
     * @throws IOException
     * @throws ConnectException
     * @throws Exception
     */
    static void recogerDatosWeb() throws IOException, ConnectException {
        try {
            @SuppressWarnings("deprecation")
            URL url_connect = new URL(leeIPMeteo());   // URL Conexión a la meteorológica ESP8266
            String inputText = ""; 
            
            BufferedReader in = new BufferedReader(new InputStreamReader(url_connect.openStream()));
    
            while (null != (inputText = in.readLine())) {     // Guarda la web (XML) en una cadena para manejarla posteriormente
                cadenaXML = cadenaXML + inputText;
            }
        } catch (ConnectException e) {
            System.out.println("ERROR DE CONEXIÓN");        
        } catch (IOException e) {
            System.out.println(e);
        }
        
        
    }
    
    
    /**
     * Método para leer los datos de la meteorológica
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ParseException
     */
    static void leerDatos() throws ParserConfigurationException, IOException, SAXException, ParseException  {
        
        //String humedadRAW=""; // Se añade esta variable para limpiar de espacios el valor de humedad
        
        InputSource archivo = new InputSource();
        archivo.setCharacterStream(new StringReader(cadenaXML));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document documento = db.parse(archivo);

        documento.getDocumentElement().normalize();
        
        NodeList nodeLista = documento.getElementsByTagName("meteorologica");

        for (int s = 0; s < nodeLista.getLength(); s++) {

            Node primerNodo = nodeLista.item(s);

            if (primerNodo.getNodeType() == Node.ELEMENT_NODE) {

                Element primerElemento = (Element) primerNodo;

                NodeList horaElemento = primerElemento.getElementsByTagName("hora");
                hora = horaElemento.item(0).getTextContent();

                NodeList fechaElemento = primerElemento.getElementsByTagName("fecha");
                fecha = Integer.parseInt(fechaElemento.item(0).getTextContent());
                date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(fecha));

                NodeList TempSensor1 = primerElemento.getElementsByTagName("sensor1");
                Element primerNombreElemento = (Element) TempSensor1.item(0);
                NodeList primerNombre = primerNombreElemento.getChildNodes();

                temperatura1 = ((Node) primerNombre.item(0)).getNodeValue();

                NodeList segundoNombreElementoLista = primerElemento.getElementsByTagName("sensor2");
                Element segundoNombreElemento = (Element) segundoNombreElementoLista.item(0);
                NodeList segundoNombre = segundoNombreElemento.getChildNodes();

                temperatura2 = ((Node) segundoNombre.item(0)).getNodeValue();

                NodeList temperaturas = primerElemento.getElementsByTagName("temperaturas");
                temperaturaMedia = temperaturas.item(0).getAttributes().item(0).getNodeValue();
                sensacionTermica = temperaturas.item(0).getAttributes().item(1).getNodeValue();

                NodeList alturaElemento = primerElemento.getElementsByTagName("presion");
                altura = alturaElemento.item(0).getAttributes().item(0).getNodeValue();
                presion = alturaElemento.item(0).getChildNodes().item(1).getTextContent();
                presionMar = alturaElemento.item(0).getChildNodes().item(3).getTextContent();

                NodeList humedadElemento = primerElemento.getElementsByTagName("humedad");
                String humedadRAW = humedadElemento.item(0).getTextContent();
                humedad = humedadRAW.replace(" ", "");

            }
        }
        
        
        System.out.println("Fecha : " + Lectura.epochToDateTime('f', fecha));
        System.out.println("Hora : " + Lectura.epochToDateTime('h', fecha));
        
        
    }
    
    
    
    /**
     * Método para mostrar los datos de la meteorológica
     */
    public void muestraDatos() {
        // Muestra datos en pantalla
         System.out.println("Fecha : " + fecha);
         System.out.println("Fecha Convertida : " + date);
         System.out.println("Hora : " + hora);
         System.out.println("Temperatura Sensor 1 : " + temperatura1);
         System.out.println("Temperatura Sensor 2 : " + temperatura2);
         System.out.println("Temperatura Media : " + temperaturaMedia);
         System.out.println("Sensacion Termica : " + sensacionTermica);
         System.out.println("Altura Relativa : " + altura);
         System.out.println("Presion Local : " + presion);
         System.out.println("Presion Nivel Mar : " + presionMar);
         System.out.println("Humedad Relativa : " + humedad + " %");        
        
    } 
    
    
    /**
     * Método para leer la IP de la meteorológica
     * @return
     * @throws FileNotFoundException
     */
    protected static String leeIPMeteo() throws FileNotFoundException {
        
        String rutaConfig = "/etc/meteo.conf";
        
        String regex="[\\d]+.[\\d]+.[\\d]+.[\\d]+";
        
        BufferedReader fConf = null;
        
        try {
            
            FileReader fr = new FileReader(rutaConfig);
                       
            fConf = new BufferedReader(fr);
            
            String linea;
                        
            while((linea = fConf.readLine()) != null ) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(linea);
                
                
                if (matcher.find()) {
                    
                                        
                    String[] ip = linea.split("\\.");
                    
                    for (String numero : ip) {
                        if (Integer.parseInt(numero)>254 || Integer.parseInt(numero) < 0) {
                            System.out.println("BAD IP");
                            return "BAD_IP";
                        }
                    }

                    
                }                
                return "http://" + linea;
            }

        } catch (FileNotFoundException e) {
            return "FILE_NOT_FOUND";
        } catch (IOException e) {
            return "IO_EXCEPTION";
        } finally {
            try {
                if (fConf != null) {
                    fConf.close();
                }
            } catch (IOException e) {
                System.out.println("Hubo un error");
            }
        }
        
        return "0";
        
    } // END leeIPMeteo()
         
    
} // END MAIN CLASS




/**
 * Clase para guardar los datos de la meteorológica 
 */
class Conexion {
    
  
    // Variables y constantes para la conexión a MYSQL
    
    final static String CONEXION="jdbc:mariadb://192.168.0.100:3306/meteo";
    final static String USUARIO="meteoduino";
    final static String PASS="meteoduino";      
    
    
    static Connection conn;
    static Statement st;
    static ResultSet resultado;
    
    
    private static void conectar() throws ClassNotFoundException {
        
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(CONEXION, USUARIO, PASS);
            st = conn.createStatement();            
            
        } catch (SQLException e) {
            System.out.println("Hubo un error al acceder a la BBDD"); 
            System.out.println(e);
        } // END TRY / CATCH
        
    } // END conectar()    
    
    protected static ResultSet leeDatos(String condicion) throws SQLException, ClassNotFoundException {
        
        conectar();
        resultado = st.executeQuery(condicion);
        return resultado;
        
        
    } // END leeDatos()   
    

    /**
     * Método para guardar los datos en la base de datos
     * @param lectura1
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected static void guardaDatos(Lectura lectura1) throws SQLException, ClassNotFoundException {
        
        conectar();
        
        String insertSQL ="INSERT INTO datos (epochTime, fecha, hora, sensor1, sensor2, t_sens, t_media, p_local, p_mar, altura, humedad) VALUES ";
        insertSQL += "(" + lectura1.getEpochTime() + ", '"+lectura1.getFecha()+"', '"+lectura1.getHora()+"', "+lectura1.getSensor1()+", "+lectura1.getSensor2();
        insertSQL += ", " + lectura1.getSTermica()+ ", " + lectura1.getTMedia() + ", " + lectura1.getPresion()+ ", " + lectura1.getPresionMar();
        insertSQL += ", " + lectura1.getAltura() + ", " + lectura1.getHumedad()+")";
               
        
        st.executeUpdate(insertSQL);
        
    } // END guardaDatos()

    /**
     * Método para guardar los datos en la base de datos
     * @param lectura1
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected static void guardaDatosEXT(LecturaEXT lectura) throws SQLException, ClassNotFoundException {
        
        conectar();
        
        String insertSQL ="INSERT INTO datosEXT (epochTime, fecha, hora, temperatura, humedad, presion, radiacion_solar, velocidad_viento, orientacion_viento, precipitacion, precipitacion_dias) VALUES ";
        insertSQL += "(" + lectura.getEpochTime() + ", '"+lectura.getFecha()+"', '"+lectura.getHora()+"', "+lectura.getTemperatura()+", "+lectura.getHumedad();
        insertSQL += ", " + lectura.getPresion() + ", " + lectura.getRadiacion_solar() + ", " + lectura.getVelocidad_viento() + ", '" + lectura.getOrientacion_viento();
        insertSQL += "', " + lectura.getPrecipitacion() + ", " + lectura.getPrecipitacion_dia()+")";
        
        st.executeUpdate(insertSQL);
        
    } // END guardaDatos()
    
    
    
    
} // END CLASS Conexion 
