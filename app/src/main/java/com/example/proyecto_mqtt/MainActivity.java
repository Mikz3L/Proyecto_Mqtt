package com.example.proyecto_mqtt;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MQTT";
    private MqttClient mqttClient;
    private EditText editTextMessage;
    private TextView textViewReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        textViewReceived = findViewById(R.id.textViewReceived);
        Button buttonSend = findViewById(R.id.buttonSend);

        String serverUri = "tcp://test.mosquitto.org:1883"; // Change this to your broker
        String clientId = MqttClient.generateClientId();

        try {
            mqttClient = new MqttClient(serverUri, clientId, null); // Using the core MQTT client

            // Set the callback to handle message reception
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "Connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Handle incoming messages
                    String receivedMessage = new String(message.getPayload());
                    runOnUiThread(() -> textViewReceived.setText("Received: " + receivedMessage));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Handle message delivery confirmation
                }
            });

            // Connect to the broker
            connectToBroker();

        } catch (MqttException e) {
            Log.e(TAG, "Error initializing MQTT client", e);
        }

        // Set the button to send the message when clicked
        buttonSend.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString();
            if (!message.isEmpty()) {
                publishMessage(message);
            }
        });
    }

    private void connectToBroker() {
        new Thread(() -> {
            try {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                mqttClient.connect(options);

                // Subscribe to a topic after successful connection
                mqttClient.subscribe("test/topic", 1);

                Log.d(TAG, "Connected to broker");

            } catch (MqttException e) {
                Log.e(TAG, "Error connecting to broker", e);
            }
        }).start();
    }

    private void publishMessage(String message) {
        new Thread(() -> {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1);
                mqttClient.publish("test/topic", mqttMessage); // Publish message to a topic
                Log.d(TAG, "Message sent: " + message);
            } catch (MqttException e) {
                Log.e(TAG, "Error publishing message", e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting", e);
        }
    }
}
