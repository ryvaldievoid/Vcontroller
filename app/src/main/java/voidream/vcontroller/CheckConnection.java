package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ryvaldie on 06/06/16.
 */
@SuppressWarnings("DefaultFileTemplate")
class CheckConnection {

    @SuppressWarnings("deprecation")
    public static boolean data(final Activity activity){
        boolean connected = false;
        ConnectivityManager checkInternet = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = checkInternet.getAllNetworkInfo();
        for (NetworkInfo aNetworkInfo : networkInfo) {
            if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }

        if (!connected) {
            final Dialog dialog = new Dialog(activity, R.style.AppTheme_PopUp);
            @SuppressLint("InflateParams")
            View view = activity.getLayoutInflater().inflate(R.layout.popup2, null);
            dialog.setContentView(view);
            Button no = (Button)view.findViewById(R.id.button_no);
            assert no != null;
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button yes = (Button)view.findViewById(R.id.button_yes);
            assert yes != null;
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        return connected;
    }

}
