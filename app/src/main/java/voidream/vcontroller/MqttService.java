package voidream.vcontroller;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ryvaldie I. H on 6/17/2016.
 *
 */

public class MqttService extends Service {

    public static MqttClient mqttClient;
    private SQLiteAdapter sqLiteAdapter;
    public static final String BROADCAST_ACTION = FragmentAdapter.class.getSimpleName();
    private Intent intent_broadcast;
    private String[] data_intent;
    private Handler handler;
    private String broker_url;
    private String[] topic;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        intent_broadcast = new Intent(BROADCAST_ACTION);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startId) {
        StrictMode.enableDefaults();
        sqLiteAdapter = new SQLiteAdapter(this);
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = Md5.md5(android_id).toUpperCase();
        final String clientId = deviceId.substring(deviceId.length() - 20);
        //deviceId = deviceId.substring(deviceId.length() - 25);

        int size = sqLiteAdapter.getController()[12].length;
        topic = new String[size];
        for (int a = 0;a < size;a++) {
            if (sqLiteAdapter.getController()[12][a] != null) {
                topic[a] = sqLiteAdapter.getController()[12][a];
            }
        }

        if (intent != null) {
            data_intent = intent.getStringArrayExtra(this.getResources().getString(R.string.data_mqtt));
            // Ambil url dan topic
            broker_url = this.getResources().getString(R.string.broker_url_string
                    , data_intent[0], data_intent[1]);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient = new MqttClient(broker_url, clientId, new MemoryPersistence());
                    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                    mqttConnectOptions.setKeepAliveInterval(30);
                    mqttConnectOptions.setWill(mqttClient.getTopic("Error")
                            , "something went wrong!".getBytes(), 1, true);
                    //untuk connect ke broker sendiri
                    if (data_intent.length > 2) {
                        mqttConnectOptions.setUserName(data_intent[2]);
                        mqttConnectOptions.setPassword(data_intent[3].toCharArray());
                        mqttConnectOptions.setCleanSession(true);
                    }
                    mqttClient.connect(mqttConnectOptions);
                    mqttClient.subscribe(topic);

                    mqttClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable throwable) {

                        }

                        @Override
                        public void messageArrived(final MqttTopic mqttTopic
                                , MqttMessage mqttMessage) throws Exception {
                            final String message = mqttMessage.toString();
                            Runnable sendUpdatesToUI = new Runnable() {
                                @Override
                                public void run() {
                                    String[][] data = sqLiteAdapter.getController();
                                    int size = data[0].length;
                                    for (int a = 0;a<size;a++) {
                                        if (message.contains(sqLiteAdapter.getController()[0][a])) {
                                            String nama = sqLiteAdapter.getController()[0][a];
                                            if (message.equals(sqLiteAdapter.getIOCommand(nama)[4])){
                                                updateController(a, nama, "on");
                                            }else if (message.equals(sqLiteAdapter.getIOCommand(nama)[5])) {
                                                updateController(a, nama, "off");
                                            }
                                        }
                                    }
                                }
                            };
                            handler.removeCallbacks(sendUpdatesToUI);
                            handler.post(sendUpdatesToUI);
                        }

                        @Override
                        public void deliveryComplete(MqttDeliveryToken mqttDeliveryToken) {

                        }
                    });

                } catch (MqttException e) {
                    //Toast.makeText(getApplicationContext(), MqttService.this.getResources().getString(R.string.mqtt_error_toast, e.getMessage()), Toast.LENGTH_LONG).show();
                    Log.e(MqttService.class.getSimpleName(), e.getMessage());
                }
            }
        }).start();

        super.onStart(intent, startId);
    }

    private void updateController(int position, String output_name, String status){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String timestamp = formatter.format(new Date().getTime());
        sqLiteAdapter.addControllerStatus(output_name, status, timestamp);
        if (!AdapterController.status[position].equals("Waiting Response")) {
            sqLiteAdapter.addLog(position, AdapterController.outputName[position], AdapterController.position[position]
                    , AdapterController.power[position], AdapterController.status[position],
                    AdapterController.Id_outputimage[position], timestamp);
        }
        intent_broadcast.putExtra("update_controller", new String[]{output_name, ""+position, status});
        sendBroadcast(intent_broadcast);
    }

    @Override
    public void onDestroy() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect(0);
            }
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext()
                    , this.getResources().getString
                            (R.string.mqtt_error_toast, e.getMessage())
                    , Toast.LENGTH_LONG).show();
            Log.e(MqttService.class.getSimpleName(), e.getMessage());
        }
    }

}