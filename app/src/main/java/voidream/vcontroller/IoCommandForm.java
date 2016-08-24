package voidream.vcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Text;

public class IoCommandForm extends Activity {

    private String on_c;
    private String off_c;
    private String t_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_command_form);

        final SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);
        final String id_controller = getIntent().getStringExtra("id_controller");

        final EditText on_command = (EditText)findViewById(R.id.edittext_io_command_turn_on);
        final EditText off_command = (EditText)findViewById(R.id.edittext_io_command_turn_off);
        final TextView timer_command = (TextView) findViewById(R.id.edittext_io_command_timerf);
        Button test_on = (Button)findViewById(R.id.button_io_command_test_on);
        test_on.setVisibility(View.GONE);
        Button test_off = (Button)findViewById(R.id.button_io_command_test_off);
        test_off.setVisibility(View.GONE);
        Button save = (Button)findViewById(R.id.button_io_command_save);

        on_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                on_c = s.toString();
                if (StringUtils.isBlank(on_c)){
                    t_c = off_c + " & " + on_c;
                }else {
                    t_c = on_c + " & " + off_c;
                }
                timer_command.setText(t_c);
            }
        });

        off_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                off_c = s.toString();
                if (StringUtils.isBlank(on_c)){
                    t_c = off_c + " & " + on_c;
                }else {
                    t_c = on_c + " & " + off_c;
                }
                timer_command.setText(t_c);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_c = on_command.getText().toString();
                off_c = off_command.getText().toString();
                if (!StringUtils.isBlank(on_c) && !StringUtils.isBlank(off_c)){
                    String[][] io_command = sqLiteAdapter.getIoCommand();
                    int size = io_command[0].length;
                    for (int a=0;a < size;a++){
                        if (!io_command[0][a].equals(id_controller)){
                            sqLiteAdapter.addIoCommand(id_controller, on_c, off_c, t_c);
                            Toast.makeText(IoCommandForm.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            a = size;
                        }else {
                            sqLiteAdapter.editIoCommand(id_controller, on_c, off_c, t_c);
                            Toast.makeText(IoCommandForm.this, "Data Edited", Toast.LENGTH_SHORT).show();
                            a = size;
                        }
                    }
                }else {
                    on_command.setError("Must be filled in");
                    off_command.setError("Must be filled in");
                }
            }
        });

    }
}
