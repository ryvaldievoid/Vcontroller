package voidream.vcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class IoCommand extends Activity {

    ListView list_io_command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_command);

        AdapterListIoCommand adapterListIoCommand = new AdapterListIoCommand(this);
        list_io_command = (ListView)findViewById(R.id.listview_io_command_list_output);
        list_io_command.setAdapter(adapterListIoCommand);
        list_io_command.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent io_form = new Intent(IoCommand.this, IoCommandForm.class);
                startActivity(io_form);
            }
        });

    }
}
