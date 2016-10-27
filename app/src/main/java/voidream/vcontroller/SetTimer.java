package voidream.vcontroller;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

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
        ImageView output_icon = (ImageView)findViewById(R.id.imageview_timer_output_image);
        RadioGroup timer_action = (RadioGroup)findViewById(R.id.radiobutton_timer_action);
        final Button timer_pick_time = (Button)findViewById(R.id.button_timer_pick_time);
        Button timer_set = (Button)findViewById(R.id.button_timer_set_timer);

        String number = getIntent().getStringExtra("output_number");
        final String name = getIntent().getStringExtra("output_name");
        String status = getIntent().getStringExtra("output_status");
        final String position = getIntent().getStringExtra("output_position");
        String power = getIntent().getStringExtra("output_power");
        int id_icon = getIntent().getIntExtra("output_icon", 0);
        final int pos = getIntent().getIntExtra("position", 0);

        output_number.setText(number);
        output_name.setText(name);
        output_status.setText(status);
        output_position.setText(position);
        output_power.setText(power);
        output_icon.setImageResource(id_icon);

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
                        String time_hour_s = String.format(Locale.getDefault(), "%02d", time_hour);
                        String time_minute_s = String.format(Locale.getDefault(), "%02d", time_minute);
                        String time = time_hour_s + " : " + time_minute_s;
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
                if (on_off) {
                    publisher.publishMqttMessage(getString(R.string.timer_command, time_hour, time_minute, name, "on"), pos);
                }else {
                    publisher.publishMqttMessage(getString(R.string.timer_command, time_hour, time_minute, name, "off"), pos);
                }
                finish();
            }
        });

    }
}
