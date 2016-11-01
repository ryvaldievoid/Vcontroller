/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package voidream.vcontroller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.commons.lang.ArrayUtils;

public class FragmentAdapter extends Fragment {

	private static final String ARG_POSITION = "position";

    private int position;
    private SQLiteAdapter sqLiteAdapter;
    private AdapterController adapterController;
    private AdapterLog adapterLog;
    private ListView controller, log_list;
    private View controller_view;
    private View log_view;
    public static ImageView background_empty;
    //private Handler handler;

	public static FragmentAdapter newInstance(int position) {
		FragmentAdapter f = new FragmentAdapter();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@SuppressLint("InflateParams")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(MqttService.BROADCAST_ACTION));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(SettingOutputForm.BROADCAST_ACTION));
        //handler = new Handler(Looper.getMainLooper());
		position = getArguments().getInt(ARG_POSITION);
        sqLiteAdapter = new SQLiteAdapter(getActivity());

        adapterController = new AdapterController(getActivity());
        controller_view = inflater.inflate(R.layout.controller, null,false);
        controller = (ListView)controller_view.findViewById(R.id.listController);
        View footer = inflater.inflate(R.layout.custom_list_addnew, null);
        controller.addFooterView(footer);
        ImageButton add_new = (ImageButton) footer.findViewById(R.id.button);
        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ArrayUtils.isEmpty(sqLiteAdapter.getMqttSetting())){
                    Intent intent_mqtt_setting= new Intent(getActivity(), Config.class);
                    startActivity(intent_mqtt_setting);
                }else {
                    Intent setting_output_form = new Intent(getActivity(), SettingOutputForm.class);
                    startActivity(setting_output_form);
                }
            }
        });

        adapterLog = new AdapterLog(getActivity());
        log_view = inflater.inflate(R.layout.log, null, false);
        log_list = (ListView)log_view.findViewById(R.id.loglist);
        background_empty = (ImageView)log_view.findViewById(R.id.imageview_empty);

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Controller
		if(position==0){
            if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString("tcp_mqtt", "").equals("mqtt")){
                if (CheckConnection.data(getActivity())) {
                    startMqttService();
                }
            }
            adapterController.updateData();
            controller.setAdapter(adapterController);

            return controller_view;
		}

		//Log
		if(position==1){
            adapterLog.updateData();
            log_list.setAdapter(adapterLog);

			return  log_view;
		}

		return  null;

	}

    private void startMqttService(){
        Intent start_service = new Intent(getActivity(), MqttService.class);
        start_service.putExtra(getString(R.string.data_mqtt), sqLiteAdapter.getMqttSetting());
        getActivity().startService(start_service);
    }

    private void stopMqttService(){
        Intent start_service = new Intent(getActivity(), MqttService.class);
        if (MqttService.mqttClient!=null) {
            getActivity().stopService(start_service);
        }
    }

    private void updateUI(Intent intent){
        if (intent.hasExtra(getString(R.string.update_list_controller))){
            adapterLog.updateData();
            adapterController.updateData();
            controller.setAdapter(adapterController);
            log_list.setAdapter(adapterLog);
        }else {
            String[] getData = intent.getStringArrayExtra("update_controller");
            if (!ArrayUtils.isEmpty(getData)) {
                //int pos = Integer.parseInt(getData[1]);
                //String status_ = getData[2];
                adapterLog.updateData();
                adapterController.updateData();
                controller.setAdapter(adapterController);
                log_list.setAdapter(adapterLog);
            }
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(MqttService.BROADCAST_ACTION));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopMqttService();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

}