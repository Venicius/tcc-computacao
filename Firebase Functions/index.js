const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

//onWrite verifica alteração no banco do firebase
exports.sendFollowerNotification = functions.database.ref('/Notificar/{nomeSensor}').onWrite((change,context) => {
  const nome = context.params.nomeSensor;
  const dados = change.after.val();

    // Notification details.
  var valorSensor = dados.valor;
  var dataSensor = dados.data;

  return loadUsers().then(users => { //buscando usuarios 
        let tokens = [];

        //adicionando usuarios a lista de tokens para notificação
        for (let user of users) {
            //adicionando apenas os sensores que o usuario deseja receber a notificação
            if (nome.toString()==="Temperatura"){
              if ((user.temperatura).toString()==="y") {
                tokens.push(user.FirebaseToken);
              }
            } else if (nome.toString()==="Umidade") {
               if ((user.umidade).toString()==="y") {
                tokens.push(user.FirebaseToken);
              }
            } else if (nome.toString()==="Chamas") {
               if ((user.chamas).toString()==="y") {
                tokens.push(user.FirebaseToken);
              }
            } else if (nome.toString()==="Luz") {
               if ((user.luz).toString()==="y") {
                tokens.push(user.FirebaseToken);
              }
            } else if (nome.toString()==="Presenca") {
               if ((user.presenca).toString()==="y") {
                tokens.push(user.FirebaseToken);
              }
            }
            
            
        }

        let payload = {
            notification: {
                title: nome,
                body: valorSensor,
                sound: 'default',
                vibrate: 'default',
                lights: 'default',
                visibility: 'public'
                
            }
        };

//verificando nome e valor de cada sensor publicado

        if (nome.toString()==="Temperatura") {

          if (Number(valorSensor) > 25 ) {
            return admin.messaging().sendToDevice(tokens, payload)
          }

        } else if (nome.toString()==="Umidade") {

           if (Number(valorSensor) > 85 ) {
            return admin.messaging().sendToDevice(tokens, payload)
          
          }

        } else if (nome.toString()==="Chamas") {

           if (valorSensor.toString()==="Fogo" ) {
            return admin.messaging().sendToDevice(tokens, payload)
        }

        } else if (nome.toString()==="Presenca") {

           if (valorSensor.toString()==="Com movimento" ) {
            return admin.messaging().sendToDevice(tokens, payload)
            
          }

        } else if (nome.toString()==="Luz") {

           if (valorSensor.toString()==="Acesa" ) {
            return admin.messaging().sendToDevice(tokens, payload)
             
          }

        }

        return;

    });


});//sendFollowers


//buscar usuarios no firebase
function loadUsers() {
    let dbRef = admin.database().ref('/Users');
    let defer = new Promise((resolve, reject) => {
        dbRef.once('value', (snap) => {
            let data = snap.val();
            let users = [];
            for (var property in data) {
                users.push(data[property]);
            }
            resolve(users);
        }, (err) => {
            reject(err);
        });
    });
    return defer;
}
