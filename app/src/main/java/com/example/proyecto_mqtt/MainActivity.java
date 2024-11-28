package com.example.proyecto_mqtt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private TextView textViewReceived;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewReceived = findViewById(R.id.textViewReceived);
        editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://test.mosquitto.org:1883", clientId);

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Conexión exitosa");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Error al conectar", exception);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                publishMessage(message);
            }
        });
    }

    private void subscribeToTopic() {
        try {
            client.subscribe("test/topic", 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Conexión perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("MQTT", "Mensaje recibido: " + message.toString());
                    runOnUiThread(() -> textViewReceived.setText("Mensajes recibidos:\n" + message.toString()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Mensaje entregado");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            client.publish("test/topic", mqttMessage);
            Log.d("MQTT", "Mensaje enviado: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
