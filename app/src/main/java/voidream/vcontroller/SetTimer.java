package voidream.vcontroller;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetTimer extends Activity {

    private boolean on_off = true;
    private int time_hour, time_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

        TextView output_number = (TextView)findViewById(R.id.textview_output_number);
        TextView output_name = (TextView)findViewById(R.id.textview_timer_output_name);
        TextView output_status = (TextView)findViewById(R.id.textview_timer_output_status);
        TextView output_position = (TextView)findViewById(R.id.textview_timer_output_position);
        TextView output_power = (TextView)findViewById(R.id.textview_timer_output_power);
        RadioGroup timer_action = (RadioGroup)findViewById(R.id.radiobutton_timer_action);
        final Button timer_pick_time = (Button)findViewById(R.id.button_timer_pick_time);
        Button timer_set = (Button)findViewById(R.id.button_timer_set_timer);

        String number = getIntent().getStringExtra("output_number");
        final String name = getIntent().getStringExtra("output_name");
        String status = getIntent().getStringExtra("output_status");
        final String position = getIntent().getStringExtra("output_position");
        String power = getIntent().getStringExtra("output_power");

        output_number.setText(number);
        output_name.setText(name);
        output_status.setText(status);
        output_position.setText(position);
        output_power.setText(power);

        timer_action.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean checked = ((RadioButton) group.findViewById(checkedId)).isChecked();
                switch(checkedId) {
                    case R.id.radiobutton_timer_action_on:
                        if (checked)
                            //Toast.makeText(getApplicationContext(), "Action ON", Toast.LENGTH_SHORT).show();
                            on_off = true;
                        break;
                    case R.id.radiobutton_timer_action_off:
                        if (checked)
                            //Toast.makeText(getApplicationContext(), "Action OFF", Toast.LENGTH_SHORT).show();
                            on_off = false;
                        break;
                    default:
                        on_off = true;
                        break;
                }
            }
        });

        timer_pick_time.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Set Time", Toast.LENGTH_SHORT).show();
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                @SuppressWarnings("deprecation")
                TimePickerDialog timePickerDialog = new TimePickerDialog(SetTimer.this
                        , TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time_hour = hourOfDay;
                        time_minute = minute;
                        String time = time_hour + " : " + time_minute;
                        timer_pick_time.setText(time);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        final MqttPublisher publisher = new MqttPublisher(this);
        timer_set.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Timer set", Toast.LENGTH_SHORT).show();
                if (AdapterController.tcp_or_mqtt){
                    if (on_off) {
                        publisher.publishMqttMessage(name + "/" + position + "/on/timer/" + time_hour
                                + "/" + time_minute);
                    }else {
                        publisher.publishMqttMessage(name + "/" + position + "/off/timer/" + time_hour
                                + "/" + time_minute);
                    }
                }else {
                    if (on_off) {
                        TCPClient.sendData(name + "/" + position + "/on/timer/" + time_hour
                                + "/" + time_minute);
                    }else {
                        TCPClient.sendData(name + "/" + position + "/off/timer/" + time_hour
                                + "/" + time_minute);
                    }
                }
                finish();
            }
        });

    }
}
