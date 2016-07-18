package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Ryvaldie I H on 09/06/16.
 * CV. Newtronic Solution
 */
public class CustomListOutputOptions extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    CustomListOutputOptions (Context context){
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static int[] id = new int[]{R.drawable.output1_on_off, R.drawable.output2_on_off
            , R.drawable.output3_on_off};

    public void updateData(int[] id){
        CustomListOutputOptions.id = id;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (id != null) {
            return id.length;
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return id[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = layoutInflater.inflate(R.layout.custom_list_output_image, null);
        ImageView image_option = (ImageView)view.findViewById(R.id.imageview_output_option);

        image_option.setImageResource(id[position]);
        //image_option.setId(id[position]);

        return view;
    }
}
