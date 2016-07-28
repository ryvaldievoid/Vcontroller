package voidream.vcontroller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ryvaldie on 04/02/16.
 */
public class TCPClientReceive extends AsyncTask<Void, Void, Void> {

    private String Address;
    private int Port;
    private String response = "";
    private Context context;
    private InputStream inputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private SQLiteAdapter sqLiteAdapter;
    public static boolean stop = false;

    private Intent intent;

    TCPClientReceive(Context ini, String addr, int port){
        context = ini;
        Address = addr;
        Port = port;
        sqLiteAdapter = new SQLiteAdapter(context);
        intent = new Intent(MqttService.BROADCAST_ACTION);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = TCPClient.socket[0];
        try {
            if (socket !=null) {
                socket = new Socket(Address, Port);
                inputStream = socket.getInputStream();

                byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);

                byte[] buffer = new byte[1024];

                int bytesRead;

				/*
				 * notice:
				 * inputStream.read() will block if no data return
				 */
                while (!stop) {
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead != -1) {
                        while (bytesRead != 1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            response += byteArrayOutputStream.toString("UTF-8");
                        }
                        if (response.contains("/")) {
                            updateController(response);
                        }
                    }
                }
            }
            //Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_SHORT).show();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(inputStream != null & byteArrayOutputStream != null){
            try {
                //socket.close();
                inputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateController(String message){
        if (!response.contains("/timer")) {
            String output_name = message.split("/")[0];
            String no = message.split("/")[1];
            if (no.substring(0, 1).equals("0")){
                no = "" + no.charAt(1);
            }
            String status = message.split("/")[2];
            int pos = Integer.parseInt(no) - 1;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String timestamp = formatter.format(new Date().getTime());
            sqLiteAdapter.addControllerStatus(output_name, status, timestamp);
            if (!AdapterController.status[pos].equals("Waiting Response")) {
                sqLiteAdapter.addLog(pos, AdapterController.outputName[pos], AdapterController.position[pos]
                        , AdapterController.power[pos], AdapterController.status[pos],
                        AdapterController.Id_outputimage[pos], timestamp);
            }
            intent.putExtra("update_controller", new String[]{output_name, no, status});
            context.sendBroadcast(intent);
        }else {
            String output_name = message.split("/")[0];
            String no = message.split("/")[1];
            if (no.substring(0, 1).equals("0")){
                no = "" + no.charAt(1);
            }
            String status = message.split("/")[2];
            int pos = Integer.parseInt(no) - 1;
            String time_hour = message.split("/")[4];
            String time_minute = message.split("/")[5];
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String timestamp = formatter.format(new Date().getTime());
            sqLiteAdapter.addControllerStatus(output_name, status, timestamp);
            if (!AdapterController.status[pos].equals("Waiting Response")) {
                sqLiteAdapter.addLog(pos, AdapterController.outputName[pos], AdapterController.position[pos]
                        , AdapterController.power[pos], AdapterController.status[pos],
                        AdapterController.Id_outputimage[pos], timestamp);
            }
            intent.putExtra("update_controller", new String[]{output_name, no, status, time_hour, time_minute});
            context.sendBroadcast(intent);
        }
    }

}
