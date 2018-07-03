package venicius.sensores.principal;

import android.util.Log;

import venicius.sensores.fragments.Sensores;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Venicius on 03/10/2017.
 */

public class MQTTClient {


    private static final String TAG = "MQTTClient";
    private String mqttBroker = "tcp://IP:PORTA";
    private String mqttUser = "sensoresFCT";
    private static final String mqttPasswd = "*******";


    private String mqttTopicTemperatura = "sensoresFCT2017/temperatura";
    private String deviceIdTemperatura = "ClientTemperatura";

    private String mqttTopicUmidade = "sensoresFCT2017/umidade";
    private String deviceIdUmidade = "ClientUmidade";

    private String mqttTopicPresenca = "sensoresFCT2017/presenca";
    private String deviceIdPresenca = "ClientPresenca";

    private String mqttTopicChamas = "sensoresFCT2017/chamas";
    private String deviceIdChamas = "ClientChamas";

    private String mqttTopicLuz = "sensoresFCT2017/luz";
    private String deviceIdLuz = "ClientLuz";


    // Variables to store reference to the user interface activity.
    private Sensores activity = null;

    public MQTTClient(Sensores activity) {
        this.activity = activity;
    }


    public void connectToMQTT() throws MqttException {



        //configurações de conexao
        Log.i(TAG, "Setting Connection Options");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(mqttUser);
        options.setPassword(mqttPasswd.toCharArray());

        // novos clientes mqtt
        Log.i(TAG, "Creating New Client");
        MqttClient clientTemperatura = new MqttClient(mqttBroker, deviceIdTemperatura, new MemoryPersistence());
        clientTemperatura.connect(options);

        MqttClient clientUmidade = new MqttClient(mqttBroker, deviceIdUmidade, new MemoryPersistence());
        clientUmidade.connect(options);

        MqttClient clientPresenca = new MqttClient(mqttBroker, deviceIdPresenca, new MemoryPersistence());
        clientPresenca.connect(options);

        MqttClient clientChamas = new MqttClient(mqttBroker, deviceIdChamas, new MemoryPersistence());
        clientChamas.connect(options);

        MqttClient clientLuz = new MqttClient(mqttBroker, deviceIdLuz, new MemoryPersistence());
        clientLuz.connect(options);

        // Set callback method name that will be invoked when a new message is posted to topic,
        // MqttEventCallback class is defined later in the code.
        Log.i(TAG, "Subscribing to Topic");
        clientTemperatura.setCallback(new MqttEventCallback());

        clientUmidade.setCallback(new MqttEventCallback());

        clientPresenca.setCallback(new MqttEventCallback());

        clientChamas.setCallback(new MqttEventCallback());

        clientLuz.setCallback(new MqttEventCallback());

        //assinatura do tópico
        clientTemperatura.subscribe(mqttTopicTemperatura, 0);
        clientUmidade.subscribe(mqttTopicUmidade, 0);
        clientPresenca.subscribe(mqttTopicPresenca, 0);
        clientChamas.subscribe(mqttTopicChamas, 0);
        clientLuz.subscribe(mqttTopicLuz, 0);


    }

    // implementação desse método que eh invocado toda vez que chega uma publicação
    private class MqttEventCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable arg0) {
            // Do nothing
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            // Do nothing
        }

        @Override
        public void messageArrived(String topic, final MqttMessage msg) throws Exception {
            Log.i(TAG, "New Message Arrived from Topic - " + topic);

            try {

                DateFormat df = DateFormat.getDateTimeInstance();
                String sensorMessage = new String(msg.getPayload());
                String data = new String(df.format(new Date()));

                // User is not going to be on the screen all the time, so create a notification.
                //activity.createNotification("Intrusion Detection System", sensorMessage);

                // Update the screen with newly received message.
                activity.updateView(topic, sensorMessage,  data);
            } catch (Exception ex) {
                Log.e("TAG", ex.getMessage());
            }
        }
    }



}
