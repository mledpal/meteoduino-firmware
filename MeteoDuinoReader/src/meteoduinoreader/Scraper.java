package meteoduinoreader;

import com.google.gson.JsonObject;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Scraper {
        
    private final String url;    
    
    public Scraper (String url)  throws IOException {
        // this.url = "https://www.meteoclimatic.net/perfil/ESAND2300000023700C";         
        this.url = url;        
    }
    
    // Método para obtener datos en formato JSON
    public LecturaEXT leerDatos() throws IOException {
        try {
            // Conectar a la página web
            Document doc = Jsoup.connect(url).get();
                        
            Elements tds = doc.select("td.dadesactuals");

            // Almacenar los datos extraídos
            List<String> dataList = new ArrayList<>();
            for (Element td : tds) {
                dataList.add(td.text());
            }           

            return new LecturaEXT(dataList);
            
            
        } catch (IllegalArgumentException e) {
            // Manejar errores y retornar un mensaje en formato JSON
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("error", "Error al obtener los datos: " + e.getMessage());
            return null;
        }
    }            
   
}
