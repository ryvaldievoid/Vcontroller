package voidream.vcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class IoCommandForm extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_command_form);

        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);
        String id_controller = getIntent().getStringExtra("id_controller");

        EditText on_command = (EditText)findViewById(R.id.edittext_io_command_turn_on);
        EditText off_command = (EditText)findViewById(R.id.edittext_io_command_turn_off);
        TextView timer_command = (TextView) findViewById(R.id.edittext_io_command_timerf);
        Button test_on = (Button)findViewById(R.id.button_io_command_test_on);
        Button test_off = (Button)findViewById(R.id.button_io_command_test_off);
        Button save = (Button)findViewById(R.id.button_io_command_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
