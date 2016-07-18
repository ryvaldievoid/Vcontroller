package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;

import java.util.Locale;

/**
 * Created by RnD2 on 4/19/2016.
 */
public class AdapterSettingOutput extends BaseAdapter {

    private SQLiteAdapter sqLiteAdapter;
    private int[] id_image = null;// = {R.drawable.out_1};
    private String[] output_name = null;//={"Lampu Kuning", "Lampu LED", "Lampu Belajar", "Test", "Test", "Test", "Test"};;
    private String[] output_position = null;//={"Ruang Tamu", "Dapur", "Kamar", "Test", "Test", "Test", "Test"};;
    private String[] output_power = null;//={"10", "20", "5", "10", "1", "2", "3"};
    private String[] output_number = null;//String[] output_number;//={"01", "02", "03", "04","05","06","07"};

    private static Context context;

    AdapterSettingOutput(Context ini){
        context = ini;
        sqLiteAdapter = new SQLiteAdapter(context);
    }

    public void updateData(){
        if (sqLiteAdapter.getController() != null) {
            String[][] data_controller = sqLiteAdapter.getController();
            int[] id_image = new int[data_controller[4].length];
            output_number = new String[data_controller[4].length];
            int b = 1;
            for (int a=0;a<data_controller[4].length;a++){
                output_number[a] = String.format(Locale.getDefault(), "%02d", b++);
                id_image[a] = Integer.parseInt(data_controller[4][a]);
            }
            this.id_image = id_image;
            output_name = data_controller[0];
            output_position = data_controller[1];
            output_power = data_controller[2];
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (ArrayUtils.isEmpty(output_name)){
            return 0;
        }else {
            return output_name.length;
        }
    }

    @Override
    public String getItem(int position) {
        return output_name[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_list_setting_output, null);

        final TextView setting_output_number = (TextView)convertView.findViewById(R.id.textview_setting_output_number);
        final ImageView setting_output_image = (ImageView)convertView.findViewById(R.id.imageview_setting_output_image);
        final TextView setting_output_name = (TextView)convertView.findViewById(R.id.textview_setting_output_name);
        final TextView setting_output_position = (TextView)convertView.findViewById(R.id.textview_setting_output_position);
        final TextView setting_output_power = (TextView)convertView.findViewById(R.id.textview_setting_output_power);
        Button setting_output_edit = (Button)convertView.findViewById(R.id.button_edit_output_edit);

        setting_output_number.setText(output_number[position]);
        setting_output_name.setText(output_name[position]);
        setting_output_position.setText(output_position[position]);
        setting_output_power.setText(output_power[position]);
        setting_output_image.setImageResource(id_image[position]);

        setting_output_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, SettingOutputForm.class);
                String[] edit = new String[]{output_number[position], output_name[position]
                        , output_position[position], output_power[position]};
                intent.putExtra(context.getString(R.string.edit_controller), edit);
                intent.putExtra(context.getString(R.string.edit_controller_id_image), id_image[position]);
                context.startActivity(intent);
            }
        });

        convertView.setLongClickable(true);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sqLiteAdapter.deleteController(output_name[position]);
                Intent intent = new Intent(SettingOutputForm.BROADCAST_ACTION);
                intent.putExtra(context.getString(R.string.update_list_controller), true);
                context.sendBroadcast(intent);
                return true;
            }
        });

        return convertView;
    }

}
