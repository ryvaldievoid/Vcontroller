package voidream.vcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SettingOutputForm extends Activity {

    private static String nama_temp = null;
    public static final String BROADCAST_ACTION = SettingOutput.class.getSimpleName();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_output_form);

        intent = new Intent(BROADCAST_ACTION);

        TextView output_number = (TextView)findViewById(R.id.textView_output_number_output_form);
        final ImageView image_set = (ImageView)findViewById(R.id.imageview_setting_output_image_selected);
        image_set.setId(0);
        final EditText nama_ouput = (EditText) findViewById(R.id.editText_nama_output);
        final EditText posisi_output = (EditText) findViewById(R.id.editText_posisi_output);
        final EditText power_output = (EditText)findViewById(R.id.editText_power_output);
        final ListView list_ouput = (ListView)findViewById(R.id.listview_output_option);
        final Button add = (Button)findViewById(R.id.button_add);

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add.getText().toString().equals(getString(R.string.save))){
                    if (image_set.getId() != 0 & !isEmpty(nama_ouput) & !isEmpty(posisi_output)
                            & !isEmpty(power_output)) {
                        int id_image = image_set.getId();
                        String nama_output_ = nama_ouput.getText().toString();
                        String posisi_output_ = posisi_output.getText().toString();
                        String power_output_ = power_output.getText().toString();
                        sqLiteAdapter.deleteController(nama_temp);
                        sqLiteAdapter.AddController(nama_output_, posisi_output_, power_output_, id_image);
                        finish();
                        intent.putExtra(getString(R.string.update_list_controller), true);
                        sendBroadcast(intent);
                    }
                }else {
                    if (image_set.getId() != 0 & !isEmpty(nama_ouput) & !isEmpty(posisi_output)
                            & !isEmpty(power_output)) {
                        if (sqLiteAdapter.getValidController(nama_ouput.getText().toString())) {
                            int id_image = image_set.getId();
                            String nama_output_ = nama_ouput.getText().toString();
                            String posisi_output_ = posisi_output.getText().toString();
                            String power_output_ = power_output.getText().toString();
                            sqLiteAdapter.AddController(nama_output_, posisi_output_, power_output_, id_image);
                            finish();
                            intent.putExtra(getString(R.string.update_list_controller), true);
                            sendBroadcast(intent);
                        }else {
                            nama_ouput.setError(getString(R.string.nama_sudah_ada));
                        }
                    }
                }
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
            add.setText(getString(R.string.save));
        }

    }

    private boolean isEmpty(EditText edit_text) {
        return edit_text.getText().toString().trim().length() == 0;
    }

}
