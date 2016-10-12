package voidream.vcontroller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class Config extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        final SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);

        final EditText broker_url = (EditText)findViewById(R.id.edittext_config_mqtt_broker_url);
        final EditText port = (EditText)findViewById(R.id.edittext_config_mqtt_port);
        final EditText username = (EditText)findViewById(R.id.edittext_config_mqtt_username);
        final EditText password = (EditText)findViewById(R.id.edittext_config_mqtt_password);password.setTypeface(Typeface.DEFAULT);

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getString("tcp_mqtt", "").equals("mqtt")){
            if (!ArrayUtils.isEmpty(sqLiteAdapter.getMqttSetting())){
                String[] set_mqtt = sqLiteAdapter.getMqttSetting();
                //String android_id = Settings.Secure.getString(Config.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                //String deviceId = Md5.md5(android_id).toUpperCase();
                //deviceId = deviceId.substring(deviceId.length() - 25);
                if (set_mqtt.length < 5 ){
                    broker_url.setText(set_mqtt[0]);
                    port.setText(set_mqtt[1]);
                }else {
                    broker_url.setText(set_mqtt[0]);
                    port.setText(set_mqtt[1]);
                    username.setText(set_mqtt[4]);
                    password.setText(set_mqtt[5]);
                }
            }else {
                broker_url.setHint(R.string.default_mqtt_broker);
                port.setHint(R.string.default_mqtt_port);
            }
        }

        Button ok = (Button)findViewById(R.id.button_config_set);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteAdapter.deleteMqttSetting();
                String url = broker_url.getText().toString();
                String port_ = port.getText().toString();
                String username_ = username.getText().toString();
                String password_ = password.getText().toString();
                if (!StringUtils.isBlank(url) & !StringUtils.isBlank(port_)) {
                    sqLiteAdapter.addMqttSetting(url, port_, username_, password_);
                    PreferenceManager.getDefaultSharedPreferences(Config.this).edit()
                            .putString("tcp_mqtt", "mqtt").apply();
                    restart();
                }else {
                    if (StringUtils.isBlank(url)) {
                        broker_url.setError("must be filled in");
                    }
                    if (StringUtils.isBlank(port_)) {
                        port.setError("must be filled in");
                    }
                }
            }
        });

    }

    private void restart(){
        finish();
        Intent config = new Intent(Config.this, Config.class);
        startActivity(config);
    }
}
