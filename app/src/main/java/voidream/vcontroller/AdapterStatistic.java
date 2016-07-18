package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;

import java.util.Locale;

/**
 * Created by Abi Karami on 5/17/2016.
 */
public class AdapterStatistic extends BaseAdapter {

    int[] id_image;
    String[] output_name;
    String[] output_position;
    String[] output_power;
    String[] output_number;

    private Context context;

    public AdapterStatistic(Context ini){
        context = ini;
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(context);
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
    public Object getItem(int position) {
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
        convertView = inflater.inflate(R.layout.custom_list_statistic, null);

        final TextView statistic_output_number = (TextView)convertView.findViewById(R.id.textview_statistic_output_number);
        final ImageView statistic_output_image = (ImageView)convertView.findViewById(R.id.imageview_statistic_output_image);
        final TextView statistic_output_name = (TextView)convertView.findViewById(R.id.textview_statistic_output_name);
        final TextView statistic_output_position = (TextView)convertView.findViewById(R.id.textview_statistic_output_position);
        final TextView statistic_output_power = (TextView)convertView.findViewById(R.id.textview_statistic_output_power);
        final TextView statistic_day = (TextView)convertView.findViewById(R.id.textview_statistic_day);
        final TextView statistic_week = (TextView)convertView.findViewById(R.id.textview_statistic_week);
        final TextView statistic_month = (TextView)convertView.findViewById(R.id.textview_statistic_month);
        final TextView statistic_average = (TextView)convertView.findViewById(R.id.textview_statistic_average);

        statistic_output_number.setText(output_number[position]);
        statistic_output_image.setImageResource(id_image[0]);
        statistic_output_name.setText(output_name[position]);
        statistic_output_position.setText(output_position[position]);
        statistic_output_power.setText(output_power[position]);
        statistic_day.setText("5h");
        statistic_week.setText("35h");
        statistic_month.setText("200h");
        statistic_average.setText("4,5h");

        return convertView;
    }

}
