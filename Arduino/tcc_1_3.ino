//INCLUDES
#include <DHT.h>
#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>


//DEFINES
#define DHTPIN A1 // DHT conectado no pino A1
#define DHTTYPE DHT11 // DHT 11

//VARIÁVEIS
int ldrPin = 0; //LDR no pino analígico 0
int ldrValor = 0; //Valor lido do LDR
int pinopir = 7;  //Pino ligado ao sensor PIR
int acionamento;  //Variavel para guardar valor do sensor PIR
int mqPin = A2; //Pino Sensor MQ2-Gas
int mqPinD = 6; //Pino Digital Sensor MQ2-Gas
int nivelMq = 480; //Variável para selecionar a quantidade de Gás/Fumaça detectada
int mqValor = 0;


DHT dht(DHTPIN, DHTTYPE); //iniciando o dht

// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };



IPAddress ip(, , , ); //ip
IPAddress subnet(, , , );//mascara
IPAddress gateway(, , , );//gateway padrao
IPAddress dnsIP(, , , );//dns

// Initialize the Ethernet client library
// with the IP address and port of the server
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

void setup() 
{
Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
// start the Ethernet connection:
    Ethernet.begin(mac, ip, dnsIP, gateway, subnet);
  
  
  // Define os pinos de leitura do sensor como entrada
  pinMode(mqPinD, INPUT);
  pinMode(mqPin, INPUT);
  pinMode(pinopir, INPUT);   //Define pino sensor PIR como entrada
  Serial.begin(9600);
  dht.begin();
}


void loop() {
  
LerDadosSensores();

delay(5000);
}


/*************************************************************/
void LerDadosSensores(){
  String chamas;
  String luz;
  String presenca;
  float umidade = dht.readHumidity();
  float temperatura = dht.readTemperature();
  // testa se retorno é valido, caso contrário algo está errado.
  if (isnan(temperatura) || isnan(umidade)) 
  {
    Serial.println("Failed to read from DHT");
  } 
  else 
  {
    Serial.print("Umidade: ");
    Serial.print(umidade);
    Serial.print(" %t");
    Serial.print("Temperatura: ");
    Serial.print(temperatura);
    Serial.println(" *C");
  }
/***************************************************************/

///ler o valor do LDR
 ldrValor = analogRead(ldrPin); //O valor lido será entre 0 e 1023
 //se o valor lido for maior que 500, liga o led
 if (ldrValor >= 500){ 
 luz = "Apagada";
  Serial.println("Luz Apagada");
 }
 else  {
  luz = "Acesa";
  Serial.println("Luz Acesa"); 
 }
 
 //imprime o valor lido do LDR no monitor serial
 //Serial.println(ldrValor);
 /***************************************************************/

acionamento = digitalRead(pinopir); //Le o valor do sensor PIR
if (acionamento == LOW)  //Sem movimento
{
  presenca = "Sem movimento";
   Serial.println("Sem Movimento");
   Serial.println(acionamento);
 }
 else  //Caso seja detectado um movimento, aciona o led vermelho
 {
    presenca = "Com movimento";
    Serial.println("Com Movimento");
    Serial.println(acionamento);
 }
/****************************************************************/

// Le os dados do pino digital 7 do sensor
  int valor_digital = digitalRead(mqPinD);
  // Le os dados do pino analogico 2 do sensor
  int valor_analogico = analogRead(mqPin);

  // Verifica o nivel de gas/fumaca detectado
  if (valor_analogico > nivelMq)
  {
    chamas = "Fogo";
    Serial.println(valor_analogico);
    Serial.println("Fogo");
  }
  else
  {
     chamas = "Sem registro";
     Serial.println(valor_analogico);
    Serial.println("Sem fogo :)");
  }


publicarDadosSensores(temperatura, umidade, chamas, presenca, luz);


}



/*****************************************************************************
 * Publicando os dados dos sensores por tópicos
*/

// IP address of the MQTT broker
char server[] = {"          "};//ip
int port = ;//porta
char topicTemperatura[] = {"sensoresFCT2017/temperatura"};
char topicUmidade[] = {"sensoresFCT2017/umidade"};
char topicChamas[] = {"sensoresFCT2017/chamas"};
char topicPresenca[] = {"sensoresFCT2017/presenca"};
char topicLuz[] = {"sensoresFCT2017/luz"};
char mqttUser[] = {"sensoresFCT"};
char mqttPasswd[] = {"**********"};//senha

void callback(char* topic, byte* payload, unsigned int length)
{
  //Handle message arrived
}


PubSubClient pubSubClient(server, port, 0, client);


void publicarDadosSensores(float temperatura, float umidade, String chamas, String presenca, String luz){

char charValTemperatura[10];
char charValUmidade[10];
char charValPresenca[20];   
char charValLuz[20];   
char charValChamas[20];   

//4 is mininum width, 3 is precision; float value is copied onto buff
dtostrf(temperatura, 4, 2, charValTemperatura);
dtostrf(umidade, 4, 2, charValUmidade);

//Stringtochar
chamas.toCharArray(charValChamas,20);
presenca.toCharArray(charValPresenca,20);
luz.toCharArray(charValLuz,20);
    
   // Connect MQTT Broker
  Serial.println("[INFO] Connecting to MQTT Broker");

  if (pubSubClient.connect("arduinoIoTClient", mqttUser, mqttPasswd)) 
  {
    Serial.println("[INFO] Connection to MQTT Broker Successfull");
  }
  else
  {
    Serial.println("[INFO] Connection to MQTT Broker Failed");
  }   
    
  // Publish to MQTT Topic
  if (pubSubClient.connected()) 
  {    
    Serial.println("[INFO] Publishing to MQTT Broker");
    pubSubClient.publish(topicTemperatura, charValTemperatura);
    pubSubClient.publish(topicUmidade, charValUmidade);
    pubSubClient.publish(topicChamas, charValChamas);
    pubSubClient.publish(topicPresenca, charValPresenca);
    pubSubClient.publish(topicLuz, charValLuz);
    Serial.println("[INFO] Publish to MQTT Broker Complete");
  }
  else
  {
    Serial.println("[ERROR] Publish to MQTT Broker Failed");
  }
  
  pubSubClient.disconnect(); 
}



















