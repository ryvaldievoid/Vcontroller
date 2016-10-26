package voidream.vcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public class SettingOutputForm extends Activity {

    private static String nama_temp = null;
    private static Boolean show_command = true;
    public static final String BROADCAST_ACTION = SettingOutput.class.getSimpleName();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_output_form);

        intent = new Intent(BROADCAST_ACTION);

        final TextView io_command = (TextView) findViewById(R.id.io_command);
        final LinearLayout io_command_layout = (LinearLayout) findViewById(R.id.command_layout);

        io_command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show_command){
                io_command_layout.setVisibility(View.VISIBLE);
                show_command=false;}
                else {
                    io_command_layout.setVisibility(View.GONE);
                    show_command=true;}
            }
        });

        TextView output_number = (TextView)findViewById(R.id.textView_output_number_output_form);
        final ImageView image_set = (ImageView)findViewById(R.id.imageview_setting_output_image_selected);
        image_set.setId(0);
        final EditText nama_ouput = (EditText) findViewById(R.id.editText_nama_output);
        final EditText posisi_output = (EditText) findViewById(R.id.editText_posisi_output);
        final EditText power_output = (EditText)findViewById(R.id.editText_power_output);
        final EditText topic = (EditText)findViewById(R.id.editText_topic);
        final ListView list_ouput = (ListView)findViewById(R.id.listview_output_option);
        final Button add = (Button)findViewById(R.id.button_add);
        final TextView on_command = (TextView)findViewById(R.id.textView_on_command);
        final TextView off_command = (TextView)findViewById(R.id.textView_off_command);
        final TextView on_r_command = (TextView)findViewById(R.id.textView_on_command_receive);
        final TextView off_r_command = (TextView)findViewById(R.id.textView_off_command_receive);
        final TextView timer_on = (TextView)findViewById(R.id.textView_on_command_timer);
        final TextView timer_off = (TextView)findViewById(R.id.textView_off_command_timer);

        final CustomListOutputOptions customListOutputOptions = new CustomListOutputOptions(this);
        list_ouput.setAdapter(customListOutputOptions);

        final SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);

        list_ouput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                image_set.setImageResource(CustomListOutputOptions.id[position]);
                image_set.setId(CustomListOutputOptions.id[position]);
            }
        });

        nama_ouput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nama_ = nama_ouput.getText().toString();
                String on_command_ = nama_ + "/on";
                String off_command_ = nama_ + "/off";
                String timer_on_ = "timer/" + nama_ + "/on";
                String timer_off_ = "timer/" + nama_ + "/off";
                String on_r = "r/" + nama_ + "/on";
                String off_r = "r/" + nama_ + "/off";
                on_command.setText(on_command_);
                off_command.setText(off_command_);
                on_r_command.setText(on_r);
                off_r_command.setText(off_r);
                timer_on.setText(timer_on_);
                timer_off.setText(timer_off_);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id_image = image_set.getId();
                String nama_output_ = nama_ouput.getText().toString();
                String posisi_output_ = posisi_output.getText().toString();
                String power_output_ = power_output.getText().toString();
                String topic_ = topic.getText().toString();
                if (add.getText().toString().equals(getString(R.string.save))){
                    if (id_image != 0 && !StringUtils.isBlank(nama_output_)
                            && !StringUtils.isBlank(posisi_output_)
                            && !StringUtils.isBlank(power_output_)
                            && !StringUtils.isBlank(topic_)) {
                        sqLiteAdapter.deleteController(nama_temp);
                        sqLiteAdapter.AddController(nama_output_, posisi_output_, power_output_
                                ,id_image, topic_);
                        intent.putExtra(getString(R.string.update_list_controller), true);
                        sendBroadcast(intent);
                        finish();
                    }
                }else {
                    if (id_image != 0 && !StringUtils.isBlank(nama_output_)
                            && !StringUtils.isBlank(posisi_output_)
                            && !StringUtils.isBlank(power_output_)
                            && !StringUtils.isBlank(topic_)) {
                        if (sqLiteAdapter.getValidController(nama_ouput.getText().toString())) {
                            sqLiteAdapter.AddController(nama_output_, posisi_output_, power_output_
                                    , id_image, topic_);
                            intent.putExtra(getString(R.string.update_list_controller), true);
                            sendBroadcast(intent);
                            finish();
                        }else {
                            nama_ouput.setError(getString(R.string.nama_sudah_ada));
                        }
                    }
                }
                startMqttService();
            }
        });

        if (getIntent().hasExtra(getString(R.string.edit_controller))) {
            String[] edit = getIntent().getStringArrayExtra(getString(R.string.edit_controller));
            int id_image = getIntent().getIntExtra(getString(R.string.edit_controller_id_image), 0);
            nama_temp = edit[1];
            output_number.setText(edit[0]);
            nama_ouput.setText(edit[1]);
            posisi_output.setText(edit[2]);
            power_output.setText(edit[3]);
            image_set.setImageResource(id_image);
            image_set.setId(id_image);
            topic.setText(edit[4]);
            add.setText(getString(R.string.save));
        }else {
            output_number.setText(String.format(Locale.getDefault(), "%02d", sqLiteAdapter.getController()[0].length + 1));
        }

    }

    private void startMqttService(){
        Intent start_service = new Intent(SettingOutputForm.this, MqttService.class);
        start_service.putExtra(getString(R.string.data_mqtt), new SQLiteAdapter(SettingOutputForm.this).getMqttSetting());
        startService(start_service);
    }

}
