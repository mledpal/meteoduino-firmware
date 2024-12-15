#include <NTPClient.h>
#include <WiFiUdp.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266mDNS.h>
#include <WiFiClientSecure.h>
#include <Adafruit_BMP280.h>  // Nuevo HW-611 // BMP280 // Sensor de Presión
#include "SHT2x.h"            // Nuevos sensores de Humedad y Presión GY-21
#include <ArduinoJson.h>      // Librería para JSON
#include <Wire.h>

// #include <DHT.h> // DEPRECATED // SENSOR DE HUMEDAD Y TEMPERATURA (Carcasa azul)


#ifndef STASSID                  // Conexión a la WIFI
#define STASSID "meteoduino"     // ESSID de tu WIFI
#define STAPSK "LTZ3ZNKF2YYYZN"  // KEY de tu WIFI
#endif


const int METEO_ID = 1;


uint32_t start;
uint32_t stop;
SHT2x sht;
// SENSOR DE HUMEDAD Y TEMPERATURA

const char* ssid = STASSID;
const char* password = STAPSK;


WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "0.es.pool.ntp.org", 3600);  // Servidor NTP España
int timeZone = 2;                                         // Zona horaria de España

unsigned long epochTime = timeClient.getEpochTime();
String hora = String(timeClient.getFormattedTime());


ESP8266WebServer server(80);

Adafruit_BMP280 bmp;  // use I2C interface
Adafruit_Sensor* bmp_temp = bmp.getTemperatureSensor();
Adafruit_Sensor* bmp_pressure = bmp.getPressureSensor();


#define ALTITUDE 416.0  // metros

//uint8_t DHTPin = 2; // DEPRECATED
//#define DHTTYPE DHT11
//DHT dht(DHTPin, DHTTYPE);

const int led = 13;

float datos[7];

float temp1, temp2, sensacionTermica, humedad, presion, nivelMar, altura;


// Medición de Voltaje de Batería
const int analogPin = A0;  // Pin analógico del ESP8266
float voltageBattery = 0.0;
float R1 = 12000.0;  // Resistencia R1 en ohmios
float R2 = 26500.0;   // Resistencia R2 en ohmios


//////////// SETUP ////////////

void setup() {

  Serial.begin(9600);

  // Ajusta la fecha y hora
  actualizarNTP();

  // Configuracion del sensor de Presión y Temperatura BMP280
  if (bmp.begin(0x76)) {
    Serial.println("Sensor Iniciado correctamente");
  } else {
    Serial.println("El sensor de humedad tiene problemas");
  }

  bmp.setSampling(Adafruit_BMP280::MODE_NORMAL,     /* Operating Mode. */
                  Adafruit_BMP280::SAMPLING_X2,     /* Temp. oversampling */
                  Adafruit_BMP280::SAMPLING_X16,    /* Pressure oversampling */
                  Adafruit_BMP280::FILTER_X16,      /* Filtering. */
                  Adafruit_BMP280::STANDBY_MS_500); /* Standby time. */
  bmp_temp->printSensorDetails();

  // SENSOR DE HUMEDAD
  //dht.begin(); // DEPRECATED

  // NUEVO SENSOR DE TEMPERATURA SHT20
  sht.begin();
  uint8_t stat = sht.getStatus();
  Serial.print(stat, HEX);

  pinMode(led, OUTPUT);
  digitalWrite(led, 0);

  // WIFI
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  // AJUSTE HORARIO
  timeClient.begin();
  timeClient.setTimeOffset(timeZone * 3600);

  // PRESENTACIÓN EN CONSOLA AVISANDO DE LA CONEXIÓN A LA RED WIFI
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  if (MDNS.begin("esp8266")) {
    Serial.println("MDNS responder started");
  }


  /////////////
  // ROUTING //
  /////////////
  server.on("/", handleRoot);
  server.on("/meteo.html", HTTP_GET, handleTemp);
  server.on("/datos.json", HTTP_GET, APIJSON);
  server.onNotFound(handleNotFound);



  ///////////////////////////////////////////////////////// HOOKS

  server.addHook([](const String& method, const String& url, WiFiClient* client, ESP8266WebServer::ContentTypeFunction contentType) {
    (void)method;       // GET, PUT, ...
    (void)url;          // example: /root/myfile.html
    (void)client;       // the webserver tcp client connection
    (void)contentType;  // contentType(".html") => "text/html"
    Serial.printf("A useless web hook has passed\n");
    Serial.printf("(this hook is in 0x%08x area (401x=IRAM 402x=FLASH))\n", esp_get_program_counter());
    return ESP8266WebServer::CLIENT_REQUEST_CAN_CONTINUE;
  });

  server.addHook([](const String&, const String& url, WiFiClient*, ESP8266WebServer::ContentTypeFunction) {
    if (url.startsWith("/fail")) {
      Serial.printf("An always failing web hook has been triggered\n");
      return ESP8266WebServer::CLIENT_MUST_STOP;
    }
    return ESP8266WebServer::CLIENT_REQUEST_CAN_CONTINUE;
  });

  server.addHook([](const String&, const String& url, WiFiClient* client, ESP8266WebServer::ContentTypeFunction) {
    if (url.startsWith("/dump")) {
      Serial.printf("The dumper web hook is on the run\n");

      // Here the request is not interpreted, so we cannot for sure
      // swallow the exact amount matching the full request+content,
      // hence the tcp connection cannot be handled anymore by the
      // webserver.
#ifdef STREAMSEND_API
      // we are lucky
      client->sendAll(Serial, 500);
#else
      auto last = millis();
      while ((millis() - last) < 500) {
        char buf[32];
        size_t len = client->read((uint8_t*)buf, sizeof(buf));
        if (len > 0) {
          Serial.printf("(<%d> chars)", (int)len);
          Serial.write(buf, len);
          last = millis();
        }
      }
#endif
      // Two choices: return MUST STOP and webserver will close it
      //                       (we already have the example with '/fail' hook)
      // or                  IS GIVEN and webserver will forget it
      // trying with IS GIVEN and storing it on a dumb WiFiClient.
      // check the client connection: it should not immediately be closed
      // (make another '/dump' one to close the first)
      Serial.printf("\nTelling server to forget this connection\n");
      static WiFiClient forgetme = *client;  // stop previous one if present and transfer client refcounter
      return ESP8266WebServer::CLIENT_IS_GIVEN;
    }
    return ESP8266WebServer::CLIENT_REQUEST_CAN_CONTINUE;
  });

  // Hook examples
  /////////////////////////////////////////////////////////




  server.begin();
  Serial.println("HTTP server started");
}


//////////// LOOP ////////////

void loop(void) {
  server.handleClient();
  MDNS.update();

  int minutos = timeClient.getMinutes();
  int seg = timeClient.getSeconds();
  
  if (seg % 5 == 0) {
    Serial.println(estadoBateria());
  }

  delay(1000);
}



// MANEJADORES DE LINKS

void jsonURL() {
  leerDatos();
}

void handleRoot() {

  digitalWrite(led, 1);

  leerDatos();
  float batt = estadoBateria();

  String xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";

  xml += "<meteorologica id='" + String(METEO_ID) + "'>\r\n";
  xml += "\t<fecha>" + String(epochTime) + "</fecha>\r\n";
  xml += "\t<hora>" + hora + "</hora>\r\n";
  xml += "\t<temperaturas media='" + String((temp1 + temp2) / 2) + "' sensacion='" + String(sensacionTermica, 2) + "' unidad ='ºC' >\r\n";
  xml += "\t\t<sensor1>" + String(temp1) + "</sensor1>\r\n";
  xml += "\t\t<sensor2>" + String(temp2) + "</sensor2>\r\n";
  xml += "\t</temperaturas>\r\n";
  xml += "\t<presion altura ='" + String(altura, 2) + "'>\r\n";
  xml += "\t\t<local>" + String(presion, 0) + "</local>";
  xml += "\t\t<mar>" + String(nivelMar, 0) + "</mar>\r\n";
  xml += "\t</presion>\r\n";
  xml += "\t<humedad>" + String(humedad, 0) + "</humedad>\r\n";
  xml += "\t<bateria>" + String(batt, 0) + "</bateria>\r\n";
  xml += "</meteorologica>\r\n";


  server.send(200, "text/xml", xml);
  digitalWrite(led, 0);
}


////////////////////////////////////////////////
// Función que manda los datos en formato XML //
////////////////////////////////////////////////
void handleTemp() {  // Ruta /meteo

  leerDatos();

  String cadena = "<!DOCTYPE html>";

  cadena += "<html lang='es'>";
  cadena += "<head>";
  cadena += "<meta charset='UTF-8'>";
  cadena += "<meta http-equiv='X-UA-Compatible' content='IE=edge'>";
  cadena += "<meta name='viewport' content='width=device-width, initial-scale=1.0'>";
  cadena += "<style>";
  cadena += "table, tr, td, th { color: white; text-align: center; }";
  cadena += "body { font-family: 'Verdana' ; background-color: rgba(0,0,0,0.4); }";
  cadena += "</style>";
  cadena += "<title>MeteoDuino</title>";
  cadena += "</head>";
  cadena += "<body>";
  cadena += "<table border='1' cellpadding='5' align='center'>";
  cadena += "<tr><th>Temperatura 1</th><th>Temperatura 2</th><th>Temperatura Media</th><th>Sensación Térmica</th><th>Presión</th><th>Humedad</th><th>Altura Real</th></tr>";
  cadena += "<tr><td>" + String(temp1) + " ºC </td>";
  cadena += "<td>" + String(temp2) + " ºC </td>";
  cadena += "<td>" + String((temp1 + temp2) / 2) + " ºC</td>";
  cadena += "<td>" + String(sensacionTermica) + " ºC </td>";
  cadena += "<td>" + String(presion) + " Pa </td>";
  cadena += "<td>" + String(humedad) + " % </td>";
  cadena += "<td>" + String(altura) + " m. </td>";
  cadena += "<td>" + String(estadoBateria()) + " V </td>";
  cadena += "</tr></table>";
  cadena += "</body></html>";

  server.send(200, "text/html", cadena);
  digitalWrite(led, 0);
}

void handleNotFound() {
  digitalWrite(led, 1);
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++) {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  digitalWrite(led, 0);
}


/////////////////////////////////////////////////
// Función que manda los datos en formato JSON //
/////////////////////////////////////////////////
void APIJSON() {
  leerDatos();

  JsonDocument doc;  // Esto no está estrictamente mal, pero se puede ignorar el warning
  char json_string[512];

  doc["id"] = METEO_ID;

  // Crear arreglos anidados usando la sintaxis moderna
  JsonArray datetime = doc["datetime"].to<JsonArray>();
  JsonArray temps = doc["temp"].to<JsonArray>();
  JsonArray pressure = doc["pressure"].to<JsonArray>();
  JsonArray humidity = doc["humidity"].to<JsonArray>();
  JsonArray battery = doc["battery"].to<JsonArray>();

  // Asignar valores a los arreglos
  datetime.add(epochTime);
  datetime.add(hora);

  temps.add(temp1);
  temps.add(temp2);
  temps.add(sensacionTermica);

  pressure.add(nivelMar);
  pressure.add(presion);
  pressure.add(altura);

  humidity.add(humedad);

  battery.add(estadoBateria());

  // Serializar JSON
  serializeJson(doc, json_string);
  server.send(200, "application/json", json_string);
}

///////////////////////////////////////////////
// Función que lee los datos de los sensores //
///////////////////////////////////////////////
void leerDatos() {

  actualizarNTP();  // Actualiza datos del servidor NTP y los datos de fecha y hora

  // NUEVO SENSOR DE TEMPERATURA
  sht.read();
  temp2 = sht.getTemperature();
  humedad = sht.getHumidity();

  if (humedad < 0) humedad = 0;

  sensacionTermica = computeHeatIndex(temp2, humedad, false);

  temp1 = bmp.readTemperature();
  presion = bmp.readPressure() / 100;

  nivelMar = bmp.seaLevelForAltitude(ALTITUDE, presion);
  altura = bmp.readAltitude(1013.25);
}

//////////////////////////////////////////////
// Función que calcula la Sensación térmica //
//////////////////////////////////////////////
float computeHeatIndex(float temperature, float percentHumidity, bool isFahrenheit) {
  float hi;

  if (!isFahrenheit)
    temperature = convertCtoF(temperature);

  hi = 0.5 * (temperature + 61.0 + ((temperature - 68.0) * 1.2) + (percentHumidity * 0.094));

  if (hi > 79) {
    hi = -42.379 + 2.04901523 * temperature + 10.14333127 * percentHumidity + -0.22475541 * temperature * percentHumidity + -0.00683783 * pow(temperature, 2) + -0.05481717 * pow(percentHumidity, 2) + 0.00122874 * pow(temperature, 2) * percentHumidity + 0.00085282 * temperature * pow(percentHumidity, 2) + -0.00000199 * pow(temperature, 2) * pow(percentHumidity, 2);

    if ((percentHumidity < 13) && (temperature >= 80.0) && (temperature <= 112.0))
      hi -= ((13.0 - percentHumidity) * 0.25) * sqrt((17.0 - abs(temperature - 95.0)) * 0.05882);

    else if ((percentHumidity > 85.0) && (temperature >= 80.0) && (temperature <= 87.0))
      hi += ((percentHumidity - 85.0) * 0.1) * ((87.0 - temperature) * 0.2);
  }

  return isFahrenheit ? hi : convertFtoC(hi);
}


/////////////////////////////////////////
// Funciones para convertir de ºC a ºF //
/////////////////////////////////////////
float convertCtoF(float c) {
  return c * 1.8 + 32;
}
float convertFtoC(float f) {
  return (f - 32) * 0.55555;
}


///////////////////////////////////////////
// Funcion para actualizar la fecha/hora //
///////////////////////////////////////////

void actualizarNTP() {

  timeClient.setTimeOffset(isSummerTime() * 3600);
  timeClient.update();

  epochTime = timeClient.getEpochTime();
  hora = String(timeClient.getFormattedTime());

  Serial.println("Cliente NTP actualizado");
}

// Funcion para distinguir en que horario nos encontramos (Verano/Invierno)

int isSummerTime() {
  // Obtiene la fecha actual en formato "AAAAMMDD" (Año, Mes, Día)

  String formattedDate = timeClient.getFormattedTime();
  int year = formattedDate.substring(0, 4).toInt();
  int month = formattedDate.substring(5, 7).toInt();
  int day = formattedDate.substring(8, 10).toInt();

  // Comprueba si la fecha actual está dentro del rango del horario de verano en España
  if ((month > 3 && month < 10) || (month == 3 && day >= 25) || (month == 10 && day <= 28)) {
    return 2;  // Está en horario de verano (UTC +2)
  } else {
    return 2;  // Está en horario estándar (UTC +1)
  }
}

float estadoBateria() {
  int rawADC = analogRead(analogPin);          // Leer ADC (0-1023)
  float voltageADC = (rawADC / 1023.0) * 3.3;  // Convertir a voltaje (0-3.3V)
  float correccion = 1.3;

  // Ajustar el voltaje de acuerdo con la relación de medición
  voltageADC = voltageADC * correccion; // Factor de corrección

  // Calcular el voltaje de la batería
  voltageBattery = voltageADC * ((R1 + R2) / R2);
  Serial.print("Valor ADC0 : ");
  Serial.println(rawADC);
  return voltageADC;
}