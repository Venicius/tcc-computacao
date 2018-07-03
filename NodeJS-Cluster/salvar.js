var http = require('http');
var firebase = require("firebase-admin");
var ultTemperatura = 0;
var ultUmidade = 0;
var ultChamas = "";
var ultPresenca = "";
var ultLuz = "";

  firebase.initializeApp({
  credential: firebase.credential.cert({
    projectId: "monitoramentofct",
    clientEmail: "firebase-adminsdk-945lb@monitoramentofct.iam.gserviceaccount.com",
    privateKey:"-----BEGIN PRIVATE KEY ******************* -----END PRIVATE KEY-----\n",
  }),
  databaseURL: "https://monitoramentofct.firebaseio.com"
});


  // Get a reference to the database service
  var database = firebase.database();


function writeSensorData(sensor, valor, ano, mes, dia, hora, minuto, dataCompleta) {
    var mes = Number(mes) + 1;

    if (Number(dia) < 10) {
      dia = '0'+dia;
    }

    if (Number(mes)<10) {
      auxmes='0'+mes;
    }

     if (Number(minuto)<10) {
      minuto='0'+minuto;
    }

     if (Number(hora)<10) {
      hora='0'+hora;
    }

    var data = dia + '-' + auxmes + '-' + ano
    var horaminuto = hora + ":" + minuto


     if(sensor.toString()==="Temperatura"){
      if (ultTemperatura != valor) {
       firebase.database().ref('DadosSensores/' + sensor + '/' + data + '/' + horaminuto).set({
        valor: valor
       });

       firebase.database().ref('DadosSensores/' + sensor + '/atual' ).set({
        valor: valor,
        data: dataCompleta
       });



      if (Number(valor) > 25) {
          firebase.database().ref('Notificar/' + sensor).set({
          valor: valor,
          data: dataCompleta
        });
      }

       ultTemperatura = valor
      }
    }

    if(sensor.toString()==="Umidade"){
     
      if (ultUmidade != valor) {
       firebase.database().ref('DadosSensores/' + sensor + '/' + data + '/' + horaminuto).set({
        valor: valor
       });

       firebase.database().ref('DadosSensores/' + sensor + '/atual' ).set({
        valor: valor,
        data: dataCompleta
       });

      if (Number(valor) > 80) {
        firebase.database().ref('Notificar/' + sensor).set({
          valor: valor,
          data: dataCompleta
        });
      }
        ultUmidade = valor
      }
    }
  

    if(sensor.toString()==="Presenca"){
      if (ultPresenca != valor) {
       firebase.database().ref('DadosSensores/' + sensor + '/' + data + '/' + horaminuto).set({
        valor: valor
       });

       firebase.database().ref('DadosSensores/' + sensor + '/atual' ).set({
         valor: valor,
        data: dataCompleta
       });

       if (valor.toString() === "Com movimento") {
        
        firebase.database().ref('Notificar/' + sensor).set({
          valor: valor,
          data: dataCompleta
        });
        
      }
      ultPresenca = valor
      }
    }

     if(sensor.toString()==="Chamas"){
       if (ultChamas != valor) {
       firebase.database().ref('DadosSensores/' + sensor + '/' + data + '/' + horaminuto).set({
        valor: valor
       });

       firebase.database().ref('DadosSensores/' + sensor + '/atual' ).set({
          valor: valor,
        data: dataCompleta
       });

       if (valor.toString()==="Fogo") {
        firebase.database().ref('Notificar/' + sensor).set({
          valor: valor,
          data: dataCompleta
        });
       
      }
      ultChamas = valor
    }
	}

      if(sensor.toString()==="Luz"){
        if (ultLuz != valor) {
       firebase.database().ref('DadosSensores/' + sensor + '/' + data + '/' + horaminuto).set({
        valor: valor
       });

       firebase.database().ref('DadosSensores/' + sensor + '/atual' ).set({
          valor: valor,
         data: dataCompleta
       });


       if (valor.toString()==="Acesa") {
        
        firebase.database().ref('Notificar/' + sensor).set({
          valor: valor,
          data: dataCompleta
        });
        
      }

       ultLuz = valor
      }
    }
}




function salvar() { 

        var mqtt = require('mqtt')

        var options1 = {
          port: porta,
          username:'sensoresFCT',
          password:'********',

          clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
          keepalive: 60,
          reconnectPeriod: 1000,
          protocolId: 'MQIsdp',
          protocolVersion: 3,
          clean: true,
          encoding: 'utf8' 
        }

        var options2 = {
          port: porta,
          username:'sensoresFCT',
          password:'********',

          clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
          keepalive: 60,
          reconnectPeriod: 1000,
          protocolId: 'MQIsdp',
          protocolVersion: 3,
          clean: true,
          encoding: 'utf8' 
        }

        var options3 = {
          port: porta,
          username:'sensoresFCT',
          password:'********',

          clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
          keepalive: 60,
          reconnectPeriod: 1000,
          protocolId: 'MQIsdp',
          protocolVersion: 3,
          clean: true,
          encoding: 'utf8' 
        }

        var options4 = {
          port: porta,
          username:'sensoresFCT',
          password:'********',

          clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
          keepalive: 60,
          reconnectPeriod: 1000,
          protocolId: 'MQIsdp',
          protocolVersion: 3,
          clean: true,
          encoding: 'utf8' 
        }

        var options5 = {
          port: porta,
          username:'sensoresFCT',
          password:'********',

          clientId: 'mqttjs_' + Math.random().toString(16).substr(2, 8),
          keepalive: 60,
          reconnectPeriod: 1000,
          protocolId: 'MQIsdp',
          protocolVersion: 3,
          clean: true,
          encoding: 'utf8' 
        }

        var clientTemperatura  = mqtt.connect('tcp://IP:porta', options1)
        var clientUmidade  = mqtt.connect('tcp://IP:porta', options2)
        var clientPresenca  = mqtt.connect('tcp://IP:porta', options3)
        var clientChamas  = mqtt.connect('tcp://IP:porta', options4)
        var clientLuz  = mqtt.connect('tcp://IP:porta', options5)



        clientTemperatura.on('connect', function () {
          clientTemperatura.subscribe('sensoresFCT2017/temperatura')
        })

        clientUmidade.on('connect', function () {
          clientUmidade.subscribe('sensoresFCT2017/umidade')
        })

        clientPresenca.on('connect', function () {
          clientPresenca.subscribe('sensoresFCT2017/presenca')
        })

        clientChamas.on('connect', function () {
          clientChamas.subscribe('sensoresFCT2017/chamas')
        })

        clientLuz.on('connect', function () {
          clientLuz.subscribe('sensoresFCT2017/luz')
        })


        clientTemperatura.on('message', function (topic, message) {
          // message is Buffer
          var timestamp = new Date();
          
          writeSensorData("Temperatura", message.toString(), 
            timestamp.getFullYear().toString(),
            timestamp.getMonth().toString(),
            timestamp.getDate().toString(),
            timestamp.getHours().toString(),
            timestamp.getMinutes().toString(),
            timestamp.toString());

          //client.end()
        })

        clientUmidade.on('message', function (topic, message) {
          // message is Buffer
          var timestamp = new Date();
          
           writeSensorData("Umidade", message.toString(), 
            timestamp.getFullYear().toString(),
            timestamp.getMonth().toString(),
            timestamp.getDate().toString(),
            timestamp.getHours().toString(),
            timestamp.getMinutes().toString(),
            timestamp.toString());
          //client.end()
        })

        clientChamas.on('message', function (topic, message) {
          // message is Buffer
          var timestamp = new Date();
          writeSensorData("Chamas", message.toString(), 
            timestamp.getFullYear().toString(),
            timestamp.getMonth().toString(),
            timestamp.getDate().toString(),
            timestamp.getHours().toString(),
            timestamp.getMinutes().toString(),
            timestamp.toString());

          //client.end()
        })

        clientPresenca.on('message', function (topic, message) {
          // message is Buffer
          var timestamp = new Date();
        writeSensorData("Presenca", message.toString(), 
            timestamp.getFullYear().toString(),
            timestamp.getMonth().toString(),
            timestamp.getDate().toString(),
            timestamp.getHours().toString(),
            timestamp.getMinutes().toString(),
            timestamp.toString());
          //client.end()
        })

        clientLuz.on('message', function (topic, message) {
          // message is Buffer
          var timestamp = new Date();
           writeSensorData("Luz", message.toString(), 
            timestamp.getFullYear().toString(),
            timestamp.getMonth().toString(),
            timestamp.getDate().toString(),
            timestamp.getHours().toString(),
            timestamp.getMinutes().toString(),
            timestamp.toString());
          //client.end()
        })
}

salvar();
//setTimeout(salvar,10000);

console.log('MQTT - firebase: executando...');
