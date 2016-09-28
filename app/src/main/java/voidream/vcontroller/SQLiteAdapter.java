package voidream.vcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Ryvaldie on 21/10/15.
 * VoidMaerd.Group
 */

public class SQLiteAdapter extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String database_name = "VcontrollerDB";
    private static final int database_version = 7;//naikin setiap ada perubahan

    public SQLiteAdapter(Context context) {
        super(context, database_name, null, database_version);
    }

    private static final String timestamp = "timestamp";

    private static final String key_id = "id", tabel_controller = "controller"
            , tabel_log = "log", tabel_setting_mqtt = "mqtt_settings"
            , tabel_setting_tcp = "tcp_settings",  on_command = "on_command"
            , off_command = "off_command", timer_on_command = "timer_command"
            , timer_off_command = "timer_off_command", on_receive_command = "on_receive_command"
            , off_receive_command = "off_receive_command";
    private static final String nama = "nama", posisi = "posisi", power = "power"
            , id_image = "id_image", status = "status", number = "number";
    private static final String create_tabel_controller =
            "create table " + tabel_controller + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + nama + " TEXT," + posisi + " TEXT,"
                    + power + " TEXT," + status + " TEXT," + id_image + " TEXT,"
                    + timestamp + " TEXT," + on_command + " TEXT," + off_command + " TEXT,"
                    + timer_on_command + " TEXT,"+ timer_off_command + " TEXT," + on_receive_command + " TEXT,"
                    + off_receive_command  + " TEXT" + ")";

    private static final String create_tabel_log =
            "create table " + tabel_log + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + number + " TEXT," + nama + " TEXT," + posisi + " TEXT,"
                    + power + " TEXT," + status + " TEXT," + timestamp + " TEXT," + id_image + " TEXT" + ")";

    private static final String broker_url = "broker_url", port = "port", username = "username"
            , password = "password", topic = "topic";
    private static final String create_tabel_setting_mqtt =
            "create table " + tabel_setting_mqtt + " (" + key_id + " INTEGER PRIMARY KEY,"
                    + broker_url + " TEXT," + port + " TEXT," + username + " TEXT,"
                    + password + " TEXT,"+ topic + " TEXT" + ")";

    private static final String ip_or_domain = "ip_domain";
    private static final String create_tabel_setting_tcp =
            "create table " + tabel_setting_tcp+ " (" + key_id + " INTEGER PRIMARY KEY,"
                    + ip_or_domain + " TEXT," + port + " TEXT" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_tabel_controller);
        db.execSQL(create_tabel_log);
        db.execSQL(create_tabel_setting_mqtt);
        db.execSQL(create_tabel_setting_tcp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tabel_controller);
        db.execSQL("DROP TABLE IF EXISTS " + tabel_log);
        db.execSQL("DROP TABLE IF EXISTS " + tabel_setting_tcp);
        db.execSQL("DROP TABLE IF EXISTS " + tabel_setting_mqtt);
        onCreate(db);
    }


    public void AddController(String nama_, String posisi_, String power_, int id_image_){
        db = this.getWritableDatabase();
        String on_command_, off_command_, timer_on, timer_off
                ,on_r, off_r;

        ContentValues values = new ContentValues();
        if(nama_!=null & posisi_!=null & power_!=null & id_image_!= 0){
            on_command_ = nama_ + "/on";
            off_command_ = nama_ + "/off";
            timer_on = "timer/" + nama_ + "/on";
            timer_off = "timer/" + nama_ + "/off";
            on_r = "r/" + nama_ + "/on";
            off_r = "r/" + nama_ + "/off";
            values.put(nama, nama_);
            values.put(posisi, posisi_);
            values.put(power, power_);
            values.put(status, "Waiting Response");
            values.put(id_image, id_image_);
            values.put(timestamp, "Waiting Response");
            values.put(on_command, on_command_);
            values.put(off_command, off_command_);
            values.put(timer_on_command, timer_on);
            values.put(timer_off_command, timer_off);
            values.put(on_receive_command, on_r);
            values.put(off_receive_command, off_r);
        }

        db.insert(tabel_controller, null, values);
        db.close();
    }

    public String[][] getController(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{nama, posisi, power, status, id_image, timestamp, on_command
                , off_command};
        Cursor cursor = db.query(tabel_controller, columns,
                null, null, null, null, null);

        int size = (int)getRowCount(tabel_controller);
        String[][] result = new String[12][size];

        int a = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0][a] = cursor.getString(0);//nama
            result[1][a] = cursor.getString(1);//posisi
            result[2][a] = cursor.getString(2);//power
            result[3][a] = cursor.getString(3);//status
            result[4][a] = cursor.getString(4);//id_image
            result[5][a] = cursor.getString(5);//timestamp
            result[6][a] = cursor.getString(6);//on command
            result[7][a] = cursor.getString(7);//off command
            result[8][a] = cursor.getString(8);//timer on command
            result[9][a] = cursor.getString(9);//timer off command
            result[10][a] = cursor.getString(10);//on receive command
            result[11][a] = cursor.getString(11);//off receive command
            a++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public boolean getValidController(String nama_){
        db = this.getReadableDatabase();

        String[] columns = new String[]{nama, posisi, power, status, id_image};
        Cursor cursor = db.query(tabel_controller, columns,
                nama + "='" + nama_ + "'", null, null, null, null);

        String check = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            check = cursor.getString(0);//nama
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        assert check != null;
        return StringUtils.isBlank(check);
    }

    public String[] getIOCommand(String nama_){
        db = this.getReadableDatabase();

        String[] columns = new String[]{nama, posisi, power, status, id_image, timestamp, on_command
                , off_command};
        Cursor cursor = db.query(tabel_controller, columns,
                nama + "='" + nama_ + "'", null, null, null, null);

        String[] result = new String[2];

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0] = cursor.getString(6);//on command
            result[1] = cursor.getString(7);//off command
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void addControllerStatus(String nama_, String status_, String timestamp_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(status_!=null){
            values.put(status, status_);
            values.put(timestamp, timestamp_);
        }

        db.update(tabel_controller, values, nama + "='" + nama_ + "'" , null);
        db.close();
    }

    public void deleteController(String nama_) {
        db = this.getWritableDatabase();
        db.delete(tabel_controller, nama + "='" + nama_ + "'", null);
        db.close();
    }

    public void addLog(int number_, String nama_, String posisi_, String power_, String status_, int id_image_,
                       String timestamp_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(nama_!=null & posisi_!=null & power_!=null & id_image_!= 0){
            values.put(number, number_);
            values.put(nama, nama_);
            values.put(posisi, posisi_);
            values.put(power, power_);
            values.put(status, status_);
            values.put(id_image, id_image_);
            values.put(timestamp, timestamp_);
        }

        db.insert(tabel_log, null, values);
        db.close();
    }

    public String[][] getLog(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{number, nama, posisi, power, status, id_image, timestamp};
        Cursor cursor = db.query(tabel_log, columns,
                null, null, null, null, null);

        int size = (int)getRowCount(tabel_log);
        String[][] result = new String[7][size];

        int a = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0][a] = cursor.getString(0);
            result[1][a] = cursor.getString(1);//nama
            result[2][a] = cursor.getString(2);//posisi
            result[3][a] = cursor.getString(3);//power
            result[4][a] = cursor.getString(4);//status
            result[5][a] = cursor.getString(5);//id_image
            result[6][a] = cursor.getString(6);//timestamp
            a++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteLog(){
        db = this.getWritableDatabase();
        db.delete(tabel_log, null, null);
        db.close();
    }

    public void addMqttSetting(String broker_url_, String port_, String username_, String password_
            , String topic_){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (broker_url_ != null & port_ != null){
            values.put(broker_url, broker_url_);
            values.put(port, port_);
            values.put(username, username_);
            values.put(password, password_);
            values.put(topic, topic_);
        }

        db.insert(tabel_setting_mqtt, null, values);
        db.close();
    }

    public String[] getMqttSetting(){
        db = this.getReadableDatabase();
        String[] columns = new String[]{broker_url, port, username, password, topic};
        Cursor cursor = db.query(tabel_setting_mqtt, columns, null
                , null, null, null, null);

        int size = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            for (int a=0;a<5;a++) {
                if (!StringUtils.isBlank(cursor.getString(a))) {
                    size++;
                }
            }
            cursor.moveToNext();
        }
        String[] result = new String[size];
        if (size < 4) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
                result[2] = cursor.getString(4);
                cursor.moveToNext();
            }
        }else {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
                result[2] = cursor.getString(4);
                result[3] = cursor.getString(2);
                result[4] = cursor.getString(3);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteMqttSetting(){
        db = this.getWritableDatabase();
        db.delete(tabel_setting_mqtt, null, null);
        db.close();
    }

    public void addTcpSetting(String ip, String port_tcp){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (ip != null & port_tcp != null){
            values.put(ip_or_domain, ip);
            values.put(port, port_tcp);
        }
        db.insert(tabel_setting_tcp, null, values);
        db.close();
    }

    public String[] getTcpSetting(){
        db = this.getReadableDatabase();

        String[] columns = new String[]{ip_or_domain, port};
        Cursor cursor = db.query(tabel_setting_tcp, columns, null,
                null, null, null, null);
        String[] result = new String[3];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            result[0] = cursor.getString(0);
            result[1] = cursor.getString(1);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return result;
    }

    public void deleteTcpSetting(){
        db = this.getWritableDatabase();
        db.delete(tabel_setting_tcp, null, null);
        db.close();
    }

    public void deleteAll(){
        db = this.getWritableDatabase();
        db.delete(tabel_controller, null, null);
        db.delete(tabel_log, null, null);
        db.delete(tabel_setting_mqtt, null, null);
        db.delete(tabel_setting_tcp, null, null);
        db.close();
    }

    public long getRowCount(String tabel) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, tabel);
    }

}