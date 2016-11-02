package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
        final ImageButton button_push = (ImageButton)convertView.findViewById(R.id.imagebutton_controller_push_button);
        ImageButton button_timer = (ImageButton)convertView.findViewById(R.id.imagebutton_controller_timer);
        RelativeLayout open_menu = (RelativeLayout)convertView.findViewById(R.id.longclick_item);

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

        button_push.setVisibility(View.VISIBLE);
        final MqttPublisher publisher = new MqttPublisher(context);
        button_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.data((Activity)context)) {
                    if (!output_image.isChecked()) {
                        publisher.publishMqttMessage(sqLiteAdapter.getIOCommand(outputName[position])[0]
                                , position);
                    } else {
                        publisher.publishMqttMessage(sqLiteAdapter.getIOCommand(outputName[position])[1]
                                , position);
                    }
                    output_status.setText(context.getString(R.string.wait));
                    button_push.setVisibility(View.GONE);
                }
            }
        });

        button_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, SetTimer.class);
                intent.putExtra("output_number", outputNumber[position]);
                intent.putExtra("output_name", sqLiteAdapter.getController()[13][position]);
                intent.putExtra("output_status", status[position]);
                intent.putExtra("output_position", AdapterController.position[position]);
                intent.putExtra("output_power", power[position]);
                intent.putExtra("output_icon", Id_outputimage[position]);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

        open_menu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view = inflater.inflate(R.layout.popup1, null);
                final Dialog dialog = new Dialog(context, R.style.AppTheme_PopUp);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(view);

                TextView title = (TextView)view.findViewById(R.id.textview_title);
                Button edit = (Button)view.findViewById(R.id.button_edit);
                Button delete = (Button)view.findViewById(R.id.button_delete);
                assert title != null;
                assert edit != null;
                assert delete != null;
                title.setText(outputName[position]);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent= new Intent(context, SettingOutputForm.class);
                        String[] edit = new String[]{outputNumber[position], outputName[position]
                                , AdapterController.position[position], power[position]
                                , sqLiteAdapter.getController()[12][position]
                                , sqLiteAdapter.getController()[13][position]};
                        intent.putExtra(context.getString(R.string.edit_controller), edit);
                        intent.putExtra(context.getString(R.string.edit_controller_id_image), Id_outputimage[position]);
                        context.startActivity(intent);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sqLiteAdapter.deleteController(outputName[position]);
                        sqLiteAdapter.deleteLog(outputName[position]);
                        Intent intent = new Intent(SettingOutputForm.BROADCAST_ACTION);
                        intent.putExtra(context.getString(R.string.update_list_controller), true);
                        context.sendBroadcast(intent);
                    }
                });

                dialog.show();
                return true;
            }
        });

        return convertView;
    }

}
