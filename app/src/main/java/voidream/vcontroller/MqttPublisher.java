package voidream.vcontroller;

import android.content.Context;
import android.provider.Settings;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

/**
 * Created by laptop-rnd1 on 26/01/16.
 */
public class MqttPublisher {

    Context context;
    String[] data_intent;
    public MqttPublisher(Context ini){
        context = ini;
        data_intent = new SQLiteAdapter(context).getMqttSetting();
    }

    public void publishMqttMessage(String message, int position){
        // Ambil Device ID
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = Md5.md5(android_id).toUpperCase();
        final String clientId = deviceId.substring(deviceId.length()-20) + "p";
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
            //untuk connect ke geeknesia.com
            //mqttConnectOptions.setUserName(ini.getString(R.string.user_name_mqtt));
            //mqttConnectOptions.setPassword(ini.getString(R.string.password_mqtt).toCharArray());
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
            e.printStackTrace();
        }
    }
}
