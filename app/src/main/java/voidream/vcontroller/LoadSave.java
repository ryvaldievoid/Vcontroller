package voidream.vcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class LoadSave extends Activity {

    Button save;
    Button load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_save);

        save = (Button)findViewById(R.id.button_save_setting);
        load = (Button)findViewById(R.id.button_load_setting);

        File direct = new File(Environment.getExternalStorageDirectory() + "/Vcontroller_db");

        if(!direct.exists())
        {
            if(direct.mkdir())
            {
                Log.v("directory", "created!");
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDB();
            }
        });

    }

    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String backupDBPath  = "/Vcontroller_db/VcontrollerDB";
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(getDatabasePath("VcontrollerDB")).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(this, "Loaded!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("importDB", e.getMessage());
        }
    }

    private void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String backupDBPath = "/Vcontroller_db/VcontrollerDB";
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(getDatabasePath("VcontrollerDB")).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("exportDB", e.getMessage());
        }
    }
}
