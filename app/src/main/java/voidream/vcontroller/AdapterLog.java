package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterLog extends BaseAdapter {

    private int[] Id_outputimage;
    private String[] outputName;
    private String[] status;
    private String[] ago;
    private String[] loc;
    private String[] dateString;
    private String[] num;

    private Context context;
    private SQLiteAdapter sqLiteAdapter;
    public AdapterLog(Context ini){
        context = ini;
        sqLiteAdapter = new SQLiteAdapter(context);
    }

    public void updateData(){
        if (sqLiteAdapter.getLog() != null) {
            String[][] data_controller = sqLiteAdapter.getLog();
            int[] id_image = new int[data_controller[5].length];
            String[] output_number = new String[data_controller[5].length];
            dateString = new String[data_controller[5].length];
            Id_outputimage = new int[data_controller[5].length];
            outputName = new String[data_controller[5].length];
            loc = new String[data_controller[5].length];
            status = new String[data_controller[5].length];
            num = new String[data_controller[5].length];
            ago = new String[data_controller[5].length];
            for (int a = 0; a < data_controller[5].length; a++) {
                output_number[a] = data_controller[0][a];
                if (!StringUtils.isBlank(data_controller[5][a])) {
                    id_image[a] = Integer.parseInt(data_controller[5][a]);
                }
            }

            int b = 0;
            for (int d = data_controller[5].length - 1; d >= 0; d--) {
                dateString[b] = data_controller[6][d];
                Id_outputimage[b] = id_image[d];
                outputName[b] = data_controller[1][d];
                loc[b] = data_controller[2][d];
                status[b] = data_controller[4][d];
                num[b] = String.format(Locale.getDefault(), "%02d", Integer.parseInt(output_number[d])+1);
                b++;
            }
        }else {
            ArrayUtils.add(dateString, null);
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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.custom_list_log, null);

        final TextView log_output = (TextView)convertView.findViewById(R.id.textview_log_output);
        final ImageView log_image = (ImageView)convertView.findViewById(R.id.imageview_log_image);
        final TextView log_explanation = (TextView)convertView.findViewById(R.id.textview_log_explanation);
        final TextView log_time_ago = (TextView)convertView.findViewById(R.id.textview_log_time_ago);
        final TextView log_location = (TextView)convertView.findViewById(R.id.textview_log_location);
        final TextView log_time_stamp = (TextView)convertView.findViewById(R.id.textview_log_time_stamp);
        final TextView log_number = (TextView)convertView.findViewById(R.id.textview_log_number);

        ago[position] = timeConverter.howManySecondsAgo(dateString[position]);

        log_image.setImageResource(Id_outputimage[position]);
        log_output.setText(outputName[position]);
        log_explanation.setText(status[position]);
        log_time_ago.setText(ago[position]);
        log_location.setText(loc[position]);
        log_time_stamp.setText(dateString[position]);
        log_number.setText(num[position]);

        convertView.setLongClickable(true);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sqLiteAdapter.deleteLog();
                return true;
            }
        });

        return convertView;
    }
}

class timeConverter {

    private static String now_date = null;
    private static String then_date = null;
    private static int hours = 0;
    private static int minute = 0;

    public static String howManySecondsAgo(String datestring){
        if (!StringUtils.isBlank(datestring)) {
            if (!datestring.equals("Waiting Response")) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                now_date = formatter.format(new Date().getTime()).split("-")[0];
                String now_hours = formatter.format(new Date().getTime()).split(" ")[1].split(":")[0];
                String now_minute = formatter.format(new Date().getTime()).split(" ")[1].split(":")[1];
                then_date = datestring.split("-")[0];
                String then_hours = datestring.split(" ")[1].split(":")[0];
                String then_minute = datestring.split(" ")[1].split(":")[1];
                hours = Integer.parseInt(now_hours) - Integer.parseInt(then_hours);
                minute = Integer.parseInt(now_minute) - Integer.parseInt(then_minute);
            }
            if (now_date.equals(then_date)) {
                if (hours > 1) {
                    return String.format(Locale.getDefault(), "%2d hours ago", hours);
                } else if (minute > 1) {
                    return String.format(Locale.getDefault(), "%2d minutes ago", minute);
                } else {
                    return "seconds ago";
                }
            } else {
                return "days ago";
            }
        }else {
            return "Waiting Response";
        }
    }
}