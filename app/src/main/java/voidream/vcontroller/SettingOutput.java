package voidream.vcontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class SettingOutput extends Activity {

    private AdapterSettingOutput adapterSettingOutput;
    private ListView setting_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_output);

        registerReceiver(broadcastReceiver, new IntentFilter(SettingOutputForm.BROADCAST_ACTION));

        adapterSettingOutput = new AdapterSettingOutput(this);
        adapterSettingOutput.updateData();
        setting_output = (ListView) findViewById(R.id.listview_setting_output);
        setting_output.setAdapter(adapterSettingOutput);

        Button add_new = (Button)findViewById(R.id.button_setting_output_add);
        assert add_new != null;
        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_output_form = new Intent(SettingOutput.this, SettingOutputForm.class);
                startActivity(setting_output_form);
            }
        });
    }

    private void updateUI(Intent intent){
        if (intent.hasExtra(getString(R.string.update_list_controller))){
            adapterSettingOutput.updateData();
            setting_output.setAdapter(adapterSettingOutput);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(SettingOutputForm.BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
