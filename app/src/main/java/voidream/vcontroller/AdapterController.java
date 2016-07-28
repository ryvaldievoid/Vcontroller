package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;

import java.util.Locale;

public class AdapterController extends BaseAdapter {

    public static int[] Id_outputimage;
    public static String[] outputName;
    public static String[] position;
    public static String[] power;
    public static String[] status;
    public static String[] outputNumber;
    public static boolean tcp_or_mqtt;

    private static Context context;
    private SQLiteAdapter sqLiteAdapter;
    public AdapterController(Context ini) {
        context = ini;
        sqLiteAdapter = new SQLiteAdapter(context);
    }

    public void updateData(){
        if (sqLiteAdapter.getController() != null) {
            String[][] data_controller = sqLiteAdapter.getController();
            int[] id_image = new int[data_controller[4].length];
            String[] output_number = new String[data_controller[4].length];
            int b = 1;
            for (int a = 0; a < data_controller[4].length; a++) {
                output_number[a] = String.format(Locale.getDefault(), "%02d", b++);
                id_image[a] = Integer.parseInt(data_controller[4][a]);
            }
            Id_outputimage = id_image;
            outputName = data_controller[0];
            position = data_controller[1];
            power = data_controller[2];
            status = data_controller[3];
            outputNumber = output_number;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (ArrayUtils.isEmpty(outputName)){
            return 0;
        }else {
            return outputName.length;
        }
    }

    @Override
    public Object getItem(int position) {
        return outputName[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_list_controller, null);

        final TextView output_number = (TextView)convertView.findViewById(R.id.textview_output_number);
        final CheckBox output_image = (CheckBox) convertView.findViewById(R.id.imageview_controller_output_image);
        final TextView output_name = (TextView)convertView.findViewById(R.id.textview_controller_output_name);
        final TextView output_position = (TextView)convertView.findViewById(R.id.textview_controller_output_position);
        final TextView output_power = (TextView)convertView.findViewById(R.id.textview_controller_output_power);
        final TextView output_status = (TextView) convertView.findViewById(R.id.textview_controller_output_status);
        ImageButton button_push = (ImageButton)convertView.findViewById(R.id.imagebutton_controller_push_button);
        ImageButton button_timer = (ImageButton)convertView.findViewById(R.id.imagebutton_controller_timer);

        output_number.setText(outputNumber[position]);
        output_image.setButtonDrawable(Id_outputimage[position]);
        if (status[position].equals("on")) {
            output_image.setChecked(true);
        }else {
            output_image.setChecked(false);
        }
        output_name.setText(outputName[position]);

        output_position.setText(AdapterController.position[position]);
        output_power.setText(power[position]);
        output_status.setText(status[position]);

        final MqttPublisher publisher = new MqttPublisher(context);
        button_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tcp_or_mqtt){
                    if (!output_image.isChecked()) {
                        publisher.publishMqttMessage(outputName[position] + "/" + outputNumber[position] + "/on");
                    }else {
                        publisher.publishMqttMessage(outputName[position] + "/" + outputNumber[position] + "/off");
                    }
                }else {
                    if (!output_image.isChecked()) {
                        TCPClient.sendData(outputName[position] + "/" + outputNumber[position] + "/on");
                    }else {
                        TCPClient.sendData(outputName[position] + "/" + outputNumber[position] + "/off");
                    }
                }
                output_status.setText(context.getString(R.string.wait));
            }
        });

        button_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, SetTimer.class);
                intent.putExtra("output_number", outputNumber[position]);
                intent.putExtra("output_name", outputName[position]);
                intent.putExtra("output_status", status[position]);
                intent.putExtra("output_position", AdapterController.position[position]);
                intent.putExtra("output_power", power[position]);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

}
