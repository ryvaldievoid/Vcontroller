package voidream.vcontroller;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

public class MqttPublisher {

    Context context;
    String[] data_intent;
    public MqttPublisher(Context ini){
        context = ini;
        data_intent = new SQLiteAdapter(context).getMqttSetting();
    }

    public void publishMqttMessage(String message, int position){
        if (!ArrayUtils.isEmpty(data_intent)) {
            // Ambil Device ID
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceId = Md5.md5(android_id).toUpperCase();
            final String clientId = deviceId.substring(deviceId.length() - 20) + "p";
            final String broker_url = context.getResources().getString(R.string.broker_url_string
                    , data_intent[0], data_intent[1]);
            final String topic = new SQLiteAdapter(context).getController()[12][position];
            final MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            try {
                final MqttClient mqttClientPublish = new MqttClient(broker_url, clientId, new MemoryPersistence());
                final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setKeepAliveInterval(30);
                mqttConnectOptions.setWill(mqttClientPublish.getTopic("Error"), "something went wrong!".getBytes(), 1, true);
                //untuk connect ke broker sendiri
                if (data_intent.length > 2) {
                    mqttConnectOptions.setUserName(data_intent[2]);
                    mqttConnectOptions.setPassword(data_intent[3].toCharArray());
                    mqttConnectOptions.setCleanSession(true);
                }
                Thread connect = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mqttClientPublish.connect(mqttConnectOptions);
                            mqttClientPublish.subscribe(topic);
                            MqttTopic mqttTopic = mqttClientPublish.getTopic(topic);
                            mqttTopic.publish(mqttMessage);
                            mqttClientPublish.disconnect();
                            mqttClientPublish.unsubscribe(topic);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
                connect.start();
            } catch (MqttException e) {
                Log.e(MqttPublisher.class.getSimpleName(), e.getMessage());
            }
        }
    }
}
